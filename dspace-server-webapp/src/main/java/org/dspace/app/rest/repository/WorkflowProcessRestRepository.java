/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.converter.*;
import org.dspace.app.rest.enums.WorkFlowAction;
import org.dspace.app.rest.enums.WorkFlowStatus;
import org.dspace.app.rest.enums.WorkFlowUserType;
import org.dspace.app.rest.exception.DSpaceBadRequestException;
import org.dspace.app.rest.exception.RepositoryMethodNotImplementedException;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.jbpm.JbpmServerImpl;
import org.dspace.app.rest.jbpm.models.JBPMResponse;
import org.dspace.app.rest.model.*;
import org.dspace.app.rest.model.patch.Patch;
import org.dspace.app.rest.repository.handler.service.UriListHandlerService;
import org.dspace.app.util.AuthorizeUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.*;
import org.dspace.content.service.*;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.RegistrationData;
import org.dspace.util.UUIDUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is the repository responsible to manage Item Rest object
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */

@Component(WorkFlowProcessRest.CATEGORY + "." + WorkFlowProcessRest.NAME)
public class WorkflowProcessRestRepository extends DSpaceObjectRestRepository<WorkflowProcess, WorkFlowProcessRest> {

    private static final Logger log = LogManager.getLogger(WorkflowProcessRestRepository.class);

    @Autowired
    WorkFlowProcessConverter workFlowProcessConverter;
    @Autowired
    WorkflowProcessReferenceDocConverter workflowProcessReferenceDocConverter;
    @Autowired
    private WorkflowProcessService workflowProcessService;
    @Autowired
    private WorkflowProcessReferenceDocService workflowProcessReferenceDocService;
    @Autowired
    private WorkflowProcessSenderDiaryService workflowProcessSenderDiaryService;
    @Autowired
    private WorkFlowProcessMasterValueConverter workFlowProcessMasterValueConverter;
    @Autowired
    JbpmServerImpl jbpmServer;
    @Autowired
    ModelMapper modelMapper ;
    public WorkflowProcessRestRepository(WorkflowProcessService dsoService) {
        super(dsoService);
    }
    @Override
    @PreAuthorize("hasPermission(#id, 'WORKSPACEITEM', 'WRITE')")
    public WorkFlowProcessRest findOne(Context context, UUID id) throws SQLException {
        WorkflowProcess workflowProcess= workflowProcessService.find(context,id);
        return converter.toRest(workflowProcess, utils.obtainProjection());
    }

    @Override
    public Page<WorkFlowProcessRest> findAll(Context context, Pageable pageable) {
        try {
            String uuid=context.getCurrentUser().getID().toString();
            String jbpmResponce=jbpmServer.gettasklist(uuid);
            JBPMResponse jbpmResponse=new JBPMResponse();
            jbpmResponse=modelMapper.map(jbpmResponce,JBPMResponse.class);
            List<String> ides=jbpmResponse.getMessage().stream().map(Message->Message.getQueueid()).collect(Collectors.toList());
            List<WorkflowProcess> workflowProcesses= workflowProcessService.findByWorkFlowProcessIds(context,ides,Math.toIntExact(pageable.getOffset()),pageable.getPageSize());
            return converter.toRestPage(workflowProcesses, pageable, jbpmResponse.getCount(), utils.obtainProjection());
        }catch (Exception e){
            throw  new RuntimeException(e.getMessage(),e);
        }
    }
    @Override
    public Class<WorkFlowProcessRest> getDomainClass() {
        return null;
    }
    @Override
    @PreAuthorize("hasPermission(#id, 'WORKSPACEITEM', 'WRITE')")
    protected WorkFlowProcessRest createAndReturn(Context context)
            throws AuthorizeException {
        // this need to be revisited we should receive an EPersonRest as input
        HttpServletRequest req = getRequestService().getCurrentRequest().getHttpServletRequest();
        ObjectMapper mapper = new ObjectMapper();
        WorkFlowProcessRest workFlowProcessRest = null;
        WorkflowProcess workflowProcess=null;

        try {
            workFlowProcessRest = mapper.readValue(req.getInputStream(), WorkFlowProcessRest.class);
            if(workFlowProcessRest.getIsDraft()){
                workFlowProcessRest.getWorkflowProcessEpersonRests().clear();
                //clear user if workflowis Draft
            }
            //set submitorUser
            if(context.getCurrentUser() != null){
                WorkflowProcessEpersonRest workflowProcessEpersonSubmitor=new WorkflowProcessEpersonRest();
                EPersonRest ePersonRest=new EPersonRest();
                ePersonRest.setUuid(context.getCurrentUser().getID().toString());
                workflowProcessEpersonSubmitor.setIndex(0);
                Optional<WorkFlowProcessMasterValue> workFlowUserTypOptional = WorkFlowUserType.INITIATOR.getUserTypeFromMasterValue(context);
                if(workFlowUserTypOptional.isPresent()){
                    workflowProcessEpersonSubmitor.setUserType(workFlowProcessMasterValueConverter.convert(workFlowUserTypOptional.get(),utils.obtainProjection()));
                }
                workflowProcessEpersonSubmitor.setePersonRest(ePersonRest);
                workFlowProcessRest.getWorkflowProcessEpersonRests().add(workflowProcessEpersonSubmitor);
            }
            workflowProcess= createworkflowProcessFromRestObject(context,workFlowProcessRest);
            workFlowProcessRest=workFlowProcessConverter.convert(workflowProcess,utils.obtainProjection());
            try {
                if(!workFlowProcessRest.getDraft()) {
                    String jbpmResponce = jbpmServer.startProcess(workFlowProcessRest);
                    WorkFlowAction workFlowAction= WorkFlowAction.FORWARD;
                    String forwardResponce=jbpmServer.forwardTask(workFlowProcessRest,workFlowAction);
                    storeHistory(context,workflowProcess,WorkFlowAction.CREATE,workflowProcess.getWorkflowProcessEpeople().stream().filter(we->we.getIndex()==0).findFirst().get());
                    storeHistory(context,workflowProcess,workFlowAction,workflowProcess.getWorkflowProcessEpeople().stream().filter(we->we.getIndex()==1).findFirst().get());
                }else{
                    String jbpmResponce = jbpmServer.startProcess(workFlowProcessRest);
                    storeHistory(context,workflowProcess,WorkFlowAction.CREATE,workflowProcess.getWorkflowProcessEpeople().stream().filter(we->we.getIndex()==0).findFirst().get());
                }
                context.commit();
            }catch (RuntimeException e){
                e.printStackTrace();
                throw new UnprocessableEntityException("error parsing the body... maybe this is not the right error code");
            }
        } catch (Exception e1) {
            throw new UnprocessableEntityException("error parsing the body... maybe this is not the right error code");
        }
        return converter.toRest(workflowProcess, utils.obtainProjection());
    }
    private WorkFlowProcessHistory storeHistory(Context context,WorkflowProcess workflowProcess,WorkFlowAction workFlowAction,WorkflowProcessEperson workflowProcessEperson) throws SQLException, AuthorizeException {
        WorkFlowProcessHistory workFlowProcessHistory=new WorkFlowProcessHistory();
        workFlowProcessHistory.setWorkflowProcessEpeople(workflowProcessEperson);
        workFlowProcessHistory.setWorkflowProcess(workflowProcess);
        return workFlowAction.perfomeAction(context,workFlowProcessHistory);
    }
    private WorkflowProcess createworkflowProcessFromRestObject(Context context, WorkFlowProcessRest workFlowProcessRest) throws AuthorizeException {
        WorkflowProcess workflowProcess =null;
        try {

            workflowProcess=workFlowProcessConverter.convert(workFlowProcessRest,context);
            Optional<WorkflowProcessSenderDiary> workflowProcessSenderDiaryOptional=Optional.ofNullable(workflowProcessSenderDiaryService.findByEmailID(context,workflowProcess.getWorkflowProcessSenderDiary().getEmail()));
            if(workflowProcessSenderDiaryOptional.isPresent()){
                workflowProcess.setWorkflowProcessSenderDiary(workflowProcessSenderDiaryOptional.get());
            }
            Optional<WorkFlowProcessMasterValue> workflowstatusopOptionalWorkFlowProcessMasterValue=null;
            if(workFlowProcessRest.getIsDraft()){
                workflowstatusopOptionalWorkFlowProcessMasterValue =WorkFlowStatus.INPROGRESS.getUserTypeFromMasterValue(context);
            }else{
                workflowstatusopOptionalWorkFlowProcessMasterValue =WorkFlowStatus.SUSPEND.getUserTypeFromMasterValue(context);
            }
            if(workflowstatusopOptionalWorkFlowProcessMasterValue.isPresent()) {
                workflowProcess.setWorkflowStatus(workflowstatusopOptionalWorkFlowProcessMasterValue.get());
            }
            workflowProcess = workflowProcessService.create(context,workflowProcess);
            WorkflowProcess finalWorkflowProcess = workflowProcess;
            workflowProcess.setWorkflowProcessReferenceDocs(workFlowProcessRest.getWorkflowProcessReferenceDocRests().stream().map(d -> {
                try {
                    WorkflowProcessReferenceDoc workflowProcessReferenceDoc = workflowProcessReferenceDocConverter.convertByService(context, d);
                    workflowProcessReferenceDoc.setWorkflowProcess(finalWorkflowProcess);
                    return workflowProcessReferenceDoc;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList()));
            workflowProcessService.update(context,workflowProcess);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
        return workflowProcess;
    }
    private WorkflowProcess forward(Context context, WorkFlowProcessRest workFlowProcessRest) throws AuthorizeException {
        try{
            // jbpmServer.startProcess(workflowProcess);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
        return  null;
    }

}