/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.converter.WorkFlowProcessConverter;
import org.dspace.app.rest.enums.WorkFlowAction;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.jbpm.JbpmServerImpl;
import org.dspace.app.rest.model.WorkFlowProcessRest;
import org.dspace.app.rest.model.WorkflowProcessEpersonRest;
import org.dspace.app.rest.repository.AbstractDSpaceRestRepository;
import org.dspace.app.rest.repository.LinkRestRepository;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.app.util.Util;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.WorkFlowProcessHistory;
import org.dspace.content.WorkflowProcess;
import org.dspace.content.WorkflowProcessEperson;
import org.dspace.content.service.WorkflowProcessService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.dspace.app.rest.utils.RegexUtils.REGEX_REQUESTMAPPING_IDENTIFIER_AS_UUID;

/**
 * This is a specialized controller to provide access to the bitstream binary
 * content
 * <p>
 * The mapping for requested endpoint try to resolve a valid UUID, for example
 * <pre>
 * {@code
 * https://<dspace.server.url>/api/core/bitstreams/26453b4d-e513-44e8-8d5b-395f62972eff/content
 * }
 * </pre>
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 * @author Tom Desair (tom dot desair at atmire dot com)
 * @author Frederic Van Reet (frederic dot vanreet at atmire dot com)
 */
@RestController
@RequestMapping("/api/" + WorkFlowProcessRest.CATEGORY + "/" + WorkFlowProcessRest.PLURAL_NAME
        + REGEX_REQUESTMAPPING_IDENTIFIER_AS_UUID)
public class WorkflowProcessController extends AbstractDSpaceRestRepository
        implements LinkRestRepository {
    private static final Logger log = org.apache.logging.log4j.LogManager
            .getLogger(WorkflowProcessController.class);

    @Autowired
    WorkflowProcessService workflowProcessService;
    @Autowired
    WorkFlowProcessConverter workFlowProcessConverter;
    @Autowired
    JbpmServerImpl jbpmServer;

    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD}, value = "forword")
    public WorkFlowProcessRest forword(@PathVariable UUID uuid,
                                       HttpServletRequest request) throws IOException, SQLException, AuthorizeException {
        WorkFlowProcessRest workFlowProcessRest = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            ObjectMapper mapper = new ObjectMapper();
            workFlowProcessRest = mapper.readValue(request.getInputStream(), WorkFlowProcessRest.class);
            Optional<WorkflowProcessEpersonRest> workflowProcessEpersonRestOptional = workFlowProcessRest.getWorkflowProcessEpersonRests().stream().findFirst();
            if (!workflowProcessEpersonRestOptional.isPresent()) {
                throw new RuntimeException("User is not added");
            }
            WorkflowProcess workFlowProcess = workflowProcessService.find(context, uuid);
            if (workFlowProcess == null) {
                throw new RuntimeException("Workflow not found");
            }
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction workFlowAction = WorkFlowAction.FORWARD;
            try {
                String body=  jbpmServer.forwardTask(workFlowProcessRest, workFlowAction);
                System.out.println("body:::"+body);
                workflowProcessService.update(context, workFlowProcess);
                storeHistory(context,workFlowAction,workFlowProcess);
            } catch (RuntimeException e) {
                e.printStackTrace();
                throw new UnprocessableEntityException("error in BPM Server..");
            }
            return workFlowProcessRest;
        } catch (RuntimeException e) {
            throw new UnprocessableEntityException("error in forwardTask Server..");
        }
    }
    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD}, value = "backward")
    public WorkFlowProcessRest backward(@PathVariable UUID uuid,
                                       HttpServletRequest request) throws IOException, SQLException, AuthorizeException {
        WorkFlowProcessRest workFlowProcessRest = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            WorkflowProcess workFlowProcess = workflowProcessService.find(context, uuid);
            if (workFlowProcess == null) {
                throw new RuntimeException("Workflow not found");
            }
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction workFlowAction = WorkFlowAction.BACKWARD;
            try {
                String body=  jbpmServer.backwardTask(workFlowProcessRest, workFlowAction);
                System.out.println("body:::"+body);
                storeHistory(context,workFlowAction,workFlowProcess);
            } catch (RuntimeException e) {
                e.printStackTrace();
                throw new UnprocessableEntityException("error in BPM Server..");
            }

            return workFlowProcessRest;
        } catch (RuntimeException e) {
            throw new UnprocessableEntityException("error in forwardTask Server..");
        }
    }
    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD}, value = "suspend")
    public WorkFlowProcessRest suspend(@PathVariable UUID uuid,
                                        HttpServletRequest request) throws IOException, SQLException, AuthorizeException {
        WorkFlowProcessRest workFlowProcessRest = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            WorkflowProcess workFlowProcess = workflowProcessService.find(context, uuid);
            if (workFlowProcess == null) {
                throw new RuntimeException("Workflow not found");
            }
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction workFlowAction = WorkFlowAction.HOLD;
            try {
                String body=  jbpmServer.holdTask(workFlowProcessRest, workFlowAction);
                System.out.println("body:::"+body);
                storeHistory(context,workFlowAction,workFlowProcess);
            } catch (RuntimeException e) {
                e.printStackTrace();
                throw new UnprocessableEntityException("error in BPM Server..");
            }

            return workFlowProcessRest;
        } catch (RuntimeException e) {
            throw new UnprocessableEntityException("error in forwardTask Server..");
        }
    }
    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD}, value = "resumetask")
    public WorkFlowProcessRest resumetask(@PathVariable UUID uuid,
                                       HttpServletRequest request) throws IOException, SQLException, AuthorizeException {
        WorkFlowProcessRest workFlowProcessRest = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            WorkflowProcess workFlowProcess = workflowProcessService.find(context, uuid);
            if (workFlowProcess == null) {
                throw new RuntimeException("Workflow not found");
            }
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction workFlowAction = WorkFlowAction.UNHOLD;
            try {
                String body=  jbpmServer.holdTask(workFlowProcessRest, workFlowAction);
                System.out.println("body:::"+body);
                storeHistory(context,workFlowAction,workFlowProcess);
            } catch (RuntimeException e) {
                e.printStackTrace();
                throw new UnprocessableEntityException("error in BPM Server..");
            }

            return workFlowProcessRest;
        } catch (RuntimeException e) {
            throw new UnprocessableEntityException("error in forwardTask Server..");
        }
    }
    public WorkFlowProcessHistory storeHistory(Context context,WorkFlowAction workFlowAction,WorkflowProcess workflowProcess) throws SQLException, AuthorizeException {
        EPerson ePerson= context.getCurrentUser();
        WorkFlowProcessHistory workFlowProcessHistory = new WorkFlowProcessHistory();
        workFlowProcessHistory.setWorkflowProcess(workflowProcess);
        Optional<WorkflowProcessEperson> workflowProcessEpersonPerfomerOption= workflowProcess.getWorkflowProcessEpeople().stream().filter(we->we.getePerson().getID().equals(ePerson.getID())).findFirst();
        if(workflowProcessEpersonPerfomerOption.isPresent()){
            return workFlowAction.perfomeAction(context, workFlowProcessHistory);
        }
        return null;
    }

}
