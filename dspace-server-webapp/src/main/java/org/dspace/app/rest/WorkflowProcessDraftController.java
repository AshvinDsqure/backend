/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import com.google.gson.Gson;
import com.itextpdf.text.DocumentException;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.converter.WorkFlowProcessConverter;
import org.dspace.app.rest.converter.WorkFlowProcessEpersonConverter;
import org.dspace.app.rest.converter.WorkFlowProcessMasterValueConverter;
import org.dspace.app.rest.enums.WorkFlowAction;
import org.dspace.app.rest.enums.WorkFlowStatus;
import org.dspace.app.rest.enums.WorkFlowType;
import org.dspace.app.rest.enums.WorkFlowUserType;
import org.dspace.app.rest.exception.MissingParameterException;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.jbpm.JbpmServerImpl;
import org.dspace.app.rest.model.EPersonRest;
import org.dspace.app.rest.model.WorkFlowProcessRest;
import org.dspace.app.rest.model.WorkflowProcessEpersonRest;
import org.dspace.app.rest.repository.AbstractDSpaceRestRepository;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.WorkFlowProcessMasterValue;
import org.dspace.content.WorkflowProcess;
import org.dspace.content.service.*;
import org.dspace.core.Context;
import org.dspace.disseminate.service.CitationDocumentService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

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
@RequestMapping("/api/" + WorkFlowProcessRest.CATEGORY
        + "/"+WorkFlowProcessRest.CATEGORY_DRAFT)
public class WorkflowProcessDraftController extends AbstractDSpaceRestRepository
        implements InitializingBean {
    private static final Logger log = org.apache.logging.log4j.LogManager
            .getLogger(WorkflowProcessDraftController.class);

    @Autowired
    WorkflowProcessService workflowProcessService;
    @Autowired
    WorkFlowProcessConverter workFlowProcessConverter;
    @Autowired
    WorkFlowProcessEpersonConverter workFlowProcessEpersonConverter;
    @Autowired
    WorkflowProcessEpersonService workflowProcessEpersonService;
    @Autowired
    private BundleService bundleService;
    @Autowired
    JbpmServerImpl jbpmServer;
    @Autowired
    BitstreamService bitstreamService;
    @Autowired
    MetadataFieldService metadataFieldService;
    @Autowired
    private DiscoverableEndpointsService discoverableEndpointsService;



    @Override
    public void afterPropertiesSet() throws Exception {
        discoverableEndpointsService
                .register(this, Arrays
                        .asList(Link.of("/api/" + WorkFlowProcessRest.CATEGORY +"/"+WorkFlowProcessRest.CATEGORY_DRAFT, WorkFlowProcessRest.CATEGORY_DRAFT)));
    }
    @Autowired
    private WorkFlowProcessMasterValueConverter workFlowProcessMasterValueConverter;
    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @ExceptionHandler(MissingParameterException.class)
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD})
    public ResponseEntity create( @RequestBody WorkFlowProcessRest workFlowProcessRest) throws Exception {
        try {
           HttpServletRequest request = getRequestService().getCurrentRequest().getHttpServletRequest();
            Context context = ContextUtil.obtainContext(request);
            Optional<WorkflowProcessEpersonRest> WorkflowProcessEpersonRest = Optional.ofNullable((getSubmitor(context)));
            if (!WorkflowProcessEpersonRest.isPresent()) {
                return ResponseEntity.badRequest().body("no user found");
            }
            WorkFlowType workFlowType = WorkFlowType.DRAFT;
            workFlowType.setWorkFlowStatus(WorkFlowStatus.INPROGRESS);
            WorkFlowAction create = WorkFlowAction.CREATE;
            workFlowType.setWorkFlowAction(create);
            workFlowType.setProjection(utils.obtainProjection());
            workFlowProcessRest.getWorkflowProcessEpersonRests().add(WorkflowProcessEpersonRest.get());
            //perfome and stor to db
            workFlowProcessRest = workFlowType.storeWorkFlowProcess(context, workFlowProcessRest);
            context.commit();
            create.setComment(null);
            create.setWorkflowProcessReferenceDocs(null);
        }catch (Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.ok(workFlowProcessRest);
    }
    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD},value = "/draft")
    public ResponseEntity draft( @RequestBody WorkFlowProcessRest workFlowProcessRest, HttpServletRequest request) throws Exception {
        WorkflowProcess workFlowProcess=null;
        System.out.println("data "+workFlowProcessRest);
        System.out.println("id "+workFlowProcessRest.getId());

        try {
            Context context = ContextUtil.obtainContext(request);
            if(workFlowProcessRest!=null && workFlowProcessRest.getId()!=null) {
             workFlowProcess = workFlowProcessConverter.convertDraftwithID(workFlowProcessRest,context,UUID.fromString(workFlowProcessRest.getId()));
            }
            if (workFlowProcess == null) {
                throw new RuntimeException("Workflow not found");
            }
            Optional<WorkFlowProcessMasterValue> workFlowTypeStatus = WorkFlowStatus.DRAFT.getUserTypeFromMasterValue(context);
            if (workFlowTypeStatus.isPresent()) {
                workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            }
            workflowProcessService.create(context, workFlowProcess);
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            context.commit();

            return ResponseEntity.ok(workFlowProcessRest);
        } catch (RuntimeException e) {
            throw new UnprocessableEntityException("error in suspendTask Server..");
        } catch (AuthorizeException e) {
            throw new RuntimeException(e);
        }
    }
    public WorkflowProcessEpersonRest getSubmitor(Context context) throws SQLException {
        if(context.getCurrentUser() != null ){
            WorkflowProcessEpersonRest workflowProcessEpersonSubmitor=new WorkflowProcessEpersonRest();
            EPersonRest ePersonRest=new EPersonRest();
            ePersonRest.setUuid(context.getCurrentUser().getID().toString());
            workflowProcessEpersonSubmitor.setIndex(0);
            workflowProcessEpersonSubmitor.setSequence(0);
            Optional<WorkFlowProcessMasterValue> workFlowUserTypOptional = WorkFlowUserType.INITIATOR.getUserTypeFromMasterValue(context);
            if(workFlowUserTypOptional.isPresent()){
                workflowProcessEpersonSubmitor.setUserType(workFlowProcessMasterValueConverter.convert(workFlowUserTypOptional.get(),utils.obtainProjection()));
            }
            workflowProcessEpersonSubmitor.setePersonRest(ePersonRest);
            return  workflowProcessEpersonSubmitor;
        }
        return  null;

    }
}
