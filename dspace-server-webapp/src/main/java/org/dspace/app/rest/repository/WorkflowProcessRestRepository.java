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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.converter.MetadataConverter;
import org.dspace.app.rest.converter.WorkFlowProcessConverter;
import org.dspace.app.rest.exception.DSpaceBadRequestException;
import org.dspace.app.rest.exception.RepositoryMethodNotImplementedException;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.jbpm.JbpmServerImpl;
import org.dspace.app.rest.model.BundleRest;
import org.dspace.app.rest.model.EPersonRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.model.WorkFlowProcessRest;
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
    private WorkflowProcessService workflowProcessService;
    @Autowired
    JbpmServerImpl jbpmServer;
    public WorkflowProcessRestRepository(WorkflowProcessService dsoService) {
        super(dsoService);
    }
    @Override
    @PreAuthorize("hasPermission(#id, 'ITEM', 'STATUS') || hasPermission(#id, 'ITEM', 'READ')")
    public WorkFlowProcessRest findOne(Context context, UUID id) {

        return converter.toRest(null, utils.obtainProjection());
    }

    @Override
    public Page<WorkFlowProcessRest> findAll(Context context, Pageable pageable) {
        try {
            List<WorkflowProcess> workflowProcesses= workflowProcessService.findAll(context,pageable.getPageSize(),Math.toIntExact(pageable.getOffset()));
            return converter.toRestPage(workflowProcesses, pageable, 0, utils.obtainProjection());
        }catch (Exception e){
            throw  new RuntimeException(e.getMessage(),e);
        }
    }
    @Override
    public Class<WorkFlowProcessRest> getDomainClass() {
        return null;
    }
    @Override
    protected WorkFlowProcessRest createAndReturn(Context context)
            throws AuthorizeException {
        // this need to be revisited we should receive an EPersonRest as input
        HttpServletRequest req = getRequestService().getCurrentRequest().getHttpServletRequest();
        ObjectMapper mapper = new ObjectMapper();
        WorkFlowProcessRest workFlowProcessRest = null;
        WorkflowProcess workflowProcess=null;
        try {
            workFlowProcessRest = mapper.readValue(req.getInputStream(), WorkFlowProcessRest.class);
            System.out.println("workFlowProcessRest::"+workFlowProcessRest.getWorkflowProcessReferenceDocRests().size());
            workflowProcess= createworkflowProcessFromRestObject(context,workFlowProcessRest);
            jbpmServer.startProcess(workflowProcess);
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new UnprocessableEntityException("error parsing the body... maybe this is not the right error code");
        }
        return converter.toRest(workflowProcess, utils.obtainProjection());
    }
    private WorkflowProcess createworkflowProcessFromRestObject(Context context, WorkFlowProcessRest workFlowProcessRest) throws AuthorizeException {
        WorkflowProcess workflowProcess =null;
        try {
            System.out.println("Priority::"+workFlowProcessRest.getPriority());
            workflowProcess=workFlowProcessConverter.convert(workFlowProcessRest,context);
            workflowProcess.setSubmitter(context.getCurrentUser());
            System.out.println("workflow Doc::"+workflowProcess.getWorkflowProcessReferenceDocs().size());
            workflowProcess = workflowProcessService.create(context,workflowProcess);
        } catch (Exception e) {
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
