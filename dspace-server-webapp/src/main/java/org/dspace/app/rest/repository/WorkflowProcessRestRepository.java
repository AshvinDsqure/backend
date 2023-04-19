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
import org.dspace.app.rest.SearchRestMethod;
import org.dspace.app.rest.converter.*;
import org.dspace.app.rest.enums.WorkFlowAction;
import org.dspace.app.rest.enums.WorkFlowStatus;
import org.dspace.app.rest.enums.WorkFlowUserType;
import org.dspace.app.rest.exception.DSpaceBadRequestException;
import org.dspace.app.rest.exception.RepositoryMethodNotImplementedException;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.exception.WorkFlowValiDationException;
import org.dspace.app.rest.jbpm.JbpmServerImpl;
import org.dspace.app.rest.jbpm.models.JBPMResponse;
import org.dspace.app.rest.model.*;
import org.dspace.app.rest.model.patch.Patch;
import org.dspace.app.rest.repository.handler.service.UriListHandlerService;
import org.dspace.app.rest.validation.WorkflowProcessValid;
import org.dspace.app.rest.validation.impl.ValidWorkFlowProcessCheck;
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
import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
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
    @Autowired
    private ValidatorFactory validatorFactory;
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
    @PreAuthorize("hasPermission(#uuid, 'ITEM', 'WRITE')")
    public Page<WorkFlowProcessRest> findAll(Context context, Pageable pageable) {
        try {
            UUID statusid=WorkFlowStatus.CLOSE.getUserTypeFromMasterValue(context).get().getID();
            System.out.println("status id:"+statusid);
            int count=workflowProcessService.countfindByWorkflowProcessId(context,context.getCurrentUser().getID());
            List<WorkflowProcess> workflowProcesses= workflowProcessService.findByWorkflowProcessId(context,context.getCurrentUser().getID(),statusid,Math.toIntExact(pageable.getOffset()),pageable.getPageSize());
            return converter.toRestPage(workflowProcesses, pageable,count , utils.obtainProjection());
        }catch (Exception e){
            e.printStackTrace();
            throw  new RuntimeException(e.getMessage(),e);
        }
    }
    @Override
    @PreAuthorize("hasPermission(#uuid, 'ITEM', 'WRITE')")
    @SearchRestMethod(name = "history")
    public Page<WorkFlowProcessRest> history(Context context, Pageable pageable) {
        try {
            int count=workflowProcessService.countfindByWorkflowProcessId(context,context.getCurrentUser().getID());
            List<WorkflowProcess> workflowProcesses= workflowProcessService.findByWorkflowProcessId(context,context.getCurrentUser().getID(),statusid,Math.toIntExact(pageable.getOffset()),pageable.getPageSize());
            return converter.toRestPage(workflowProcesses, pageable,count , utils.obtainProjection());
        }catch (Exception e){
            e.printStackTrace();
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
            Set<ConstraintViolation<WorkFlowProcessRest>> violations=validatorFactory.getValidator().validate(workFlowProcessRest);
            if (!violations.isEmpty()){
                //throw new WorkFlowValiDationException(violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()));
            }
            boolean isDraft=workFlowProcessRest.getDraft();
            String comment= workFlowProcessRest.getComment();

            if(isDraft){
                workFlowProcessRest.getWorkflowProcessEpersonRests().clear();
                //clear user if workflowis Draft
            }
            //set submitorUser
            if(context.getCurrentUser() != null && !isDraft){
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
                System.out.println("isDraft:::"+isDraft);
                if(!isDraft) {
                   WorkFlowAction create= WorkFlowAction.CREATE;
                    create.setComment(comment);
                    create.perfomeAction(context,workflowProcess,workFlowProcessRest);

                }
                context.commit();
            }catch (RuntimeException | SQLException e){
                e.printStackTrace();
                throw new UnprocessableEntityException("error parsing the body... maybe this is not the right error code");
            }
        } catch (RuntimeException | IOException e1) {
            e1.printStackTrace();
            throw new UnprocessableEntityException("error parsing the body... maybe this is not the right error code");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return workFlowProcessRest;
    }
    private WorkflowProcess createworkflowProcessFromRestObject(Context context, WorkFlowProcessRest workFlowProcessRest) throws AuthorizeException {
        WorkflowProcess workflowProcess =null;
        try {

            workflowProcess=workFlowProcessConverter.convert(workFlowProcessRest,context);
            Optional<WorkflowProcessSenderDiary> workflowProcessSenderDiaryOptional=Optional.ofNullable(workflowProcessSenderDiaryService.findByEmailID(context,workflowProcess.getWorkflowProcessSenderDiary().getEmail()));
            if(workflowProcessSenderDiaryOptional.isPresent()){
                workflowProcess.setWorkflowProcessSenderDiary(workflowProcessSenderDiaryOptional.get());
            }
            WorkFlowProcessMasterValue workflowstatusopOptionalWorkFlowProcessMasterValue=null;
                System.out.println("workFlowProcessRest.getDraft()::"+workFlowProcessRest.getDraft());
            if(!workFlowProcessRest.getDraft()){
                System.out.println(">>>>>>>>>>>>>>>>>>>>"+WorkFlowStatus.INPROGRESS.getUserTypeFromMasterValue(context).get().getPrimaryvalue());
                workflowstatusopOptionalWorkFlowProcessMasterValue =WorkFlowStatus.INPROGRESS.getUserTypeFromMasterValue(context).get();
            }else{
                workflowstatusopOptionalWorkFlowProcessMasterValue =WorkFlowStatus.SUSPEND.getUserTypeFromMasterValue(context).get();
            }
            if(workflowstatusopOptionalWorkFlowProcessMasterValue!=null) {
                System.out.println(">>>>>>>>>>>>>>>>>{{}}>>>"+workflowstatusopOptionalWorkFlowProcessMasterValue.getPrimaryvalue());

                workflowProcess.setWorkflowStatus(workflowstatusopOptionalWorkFlowProcessMasterValue);
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
            System.out.println("workflowProcess::: update ......");
            workflowProcessService.update(context,workflowProcess);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
        return workflowProcess;
    }

}