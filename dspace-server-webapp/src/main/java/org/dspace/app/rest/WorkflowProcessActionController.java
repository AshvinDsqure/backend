/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.text.*;
import net.sf.saxon.expr.instruct.ForEach;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.dspace.app.rest.converter.*;
import org.dspace.app.rest.enums.WorkFlowAction;
import org.dspace.app.rest.enums.WorkFlowStatus;
import org.dspace.app.rest.enums.WorkFlowUserType;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.jbpm.JbpmServerImpl;
import org.dspace.app.rest.model.WorkFlowProcessOutwardDetailsRest;
import org.dspace.app.rest.model.WorkFlowProcessRest;
import org.dspace.app.rest.model.WorkflowProcessEpersonRest;
import org.dspace.app.rest.repository.AbstractDSpaceRestRepository;
import org.dspace.app.rest.repository.BundleRestRepository;
import org.dspace.app.rest.repository.LinkRestRepository;
import org.dspace.app.rest.utils.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.*;
import org.dspace.content.service.*;
import org.dspace.core.Context;
import org.dspace.disseminate.service.CitationDocumentService;
import org.dspace.eperson.EPerson;
import org.dspace.services.ConfigurationService;
import org.dspace.services.EventService;
import org.dspace.usage.UsageEvent;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

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
@RequestMapping("/api/" + WorkFlowProcessRest.CATEGORY
        + REGEX_REQUESTMAPPING_IDENTIFIER_AS_UUID)
public class WorkflowProcessActionController extends AbstractDSpaceRestRepository
        implements LinkRestRepository {
    private static final Logger log = org.apache.logging.log4j.LogManager
            .getLogger(WorkflowProcessActionController.class);
    private static final int BUFFER_SIZE = 4096 * 10;
    @Autowired
    WorkflowProcessService workflowProcessService;

    @Autowired
    ItemConverter itemConverter;
    @Autowired
    WorkFlowProcessConverter workFlowProcessConverter;

    @Autowired
    WorkflowProcessReferenceDocVersionService workflowProcessReferenceDocVersionService;

    @Autowired
    WorkflowProcessNoteService workflowProcessNoteService;
    @Autowired
    BundleRestRepository bundleRestRepository;

    @Autowired
    CitationDocumentService citationDocumentService;

    @Autowired
    ConfigurationService configurationService;
    @Autowired
    WorkFlowProcessMasterValueService workFlowProcessMasterValueService;

    @Autowired
    WorkFlowProcessCommentService workFlowProcessCommentService;
    @Autowired
    WorkFlowProcessMasterService workFlowProcessMasterService;
    @Autowired
    WorkflowProcessReferenceDocService workflowProcessReferenceDocService;

    @Autowired
    WorkflowProcessReferenceDocConverter workflowProcessReferenceDocConverter;

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
    EPersonConverter ePersonConverter;


    @Autowired
    EventService eventService;
    @Autowired
    MetadataFieldService metadataFieldService;
    @Autowired
    MetadataValueService metadataValueService;

    @Autowired
    WorkFlowProcessOutwardDetailsConverter outwardDetailsConverter;

    private static Font COURIER = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);
    private static Font COURIER_SMALL = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static Font COURIER_SMALL_FOOTER = new Font(Font.FontFamily.UNDEFINED, 10, Font.NORMAL);
    private static Font COURIER_SMALL_FOOTER1 = new Font(Font.FontFamily.COURIER, 10, Font.BOLD);


    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD}, value = "forward")
    public WorkFlowProcessRest forword(@PathVariable UUID uuid,
                                       HttpServletRequest request) throws IOException, SQLException, AuthorizeException {
        WorkflowProcessEpersonRest workflowProcessEpersonRest = null;
        WorkFlowProcessRest workFlowProcessRest = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            ObjectMapper mapper = new ObjectMapper();
            workflowProcessEpersonRest = mapper.readValue(request.getInputStream(), WorkflowProcessEpersonRest.class);
            System.out.println("workFlowProcessRest" + new Gson().toJson(workflowProcessEpersonRest));
            String comment = workflowProcessEpersonRest.getComment();
            System.out.println("Comment::::::::::::::::::::::::::" + comment);
            WorkflowProcess workFlowProcess = workflowProcessService.find(context, uuid);
            WorkflowProcessEperson workflowProcessEperson = workFlowProcessEpersonConverter.convert(context, workflowProcessEpersonRest);

            workflowProcessEperson.setWorkflowProcess(workFlowProcess);
            Optional<WorkFlowProcessMasterValue> userTypeOption = WorkFlowUserType.NORMAL.getUserTypeFromMasterValue(context);
            if (userTypeOption.isPresent()) {
                workflowProcessEperson.setUsertype(userTypeOption.get());
            }
            if (workFlowProcess.getItem() == null && workflowProcessEpersonRest.getItemRest() != null) {
                workFlowProcess.setItem(itemConverter.convert(workflowProcessEpersonRest.getItemRest(), context));
            }
            Optional<WorkFlowProcessMasterValue> workFlowTypeStatus = WorkFlowStatus.INPROGRESS.getUserTypeFromMasterValue(context);
            if (workFlowTypeStatus.isPresent()) {
                workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            }
            workFlowProcess.setnewUser(workflowProcessEperson);
            workflowProcessService.create(context, workFlowProcess);
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction action = WorkFlowAction.FORWARD;
            if (comment != null) {
                action.setComment(comment);
                if (workflowProcessEpersonRest.getWorkflowProcessReferenceDocRests() != null && workflowProcessEpersonRest.getWorkflowProcessReferenceDocRests().size() != 0) {
                    List<WorkflowProcessReferenceDoc> doc = getCommentDocuments(context, workflowProcessEpersonRest, null);
                    if (doc != null) {
                        action.setWorkflowProcessReferenceDocs(doc);
                    }
                }
            }
            action.perfomeAction(context, workFlowProcess, workFlowProcessRest);
            context.commit();
            action.setComment(null);
            action.setWorkflowProcessReferenceDocs(null);
            return workFlowProcessRest;
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new UnprocessableEntityException("error in forwardTask Server..");
        }
    }

    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD}, value = "forwordDraft")
    public WorkFlowProcessRest forwordDraft(@PathVariable UUID uuid,
                                            HttpServletRequest request) throws IOException, SQLException, AuthorizeException {
        WorkFlowProcessRest workFlowProcessRest = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            ObjectMapper mapper = new ObjectMapper();
            workFlowProcessRest = mapper.readValue(request.getInputStream(), WorkFlowProcessRest.class);
            System.out.println("workFlowProcessRest" + new Gson().toJson(workFlowProcessRest));
            String comment = workFlowProcessRest.getComment();
            System.out.println("Comment::::::::::::::::::::::::::" + comment);
            WorkflowProcess workFlowProcess = workflowProcessService.find(context, uuid);
            if (workFlowProcess.getItem() == null && workFlowProcessRest.getItemRest() != null) {
                workFlowProcess.setItem(itemConverter.convert(workFlowProcessRest.getItemRest(), context));
            }
            List<UUID> newEpesons = new ArrayList<>();
            for (WorkflowProcessEperson e : workFlowProcess.getWorkflowProcessEpeople()) {
                newEpesons.add(e.getePerson().getID());
            }
            System.out.println("oldids" + newEpesons);
            List<WorkflowProcessEperson> newUserList = new ArrayList<>();
            for (WorkflowProcessEpersonRest newEpeson : workFlowProcessRest.getWorkflowProcessEpersonRests()) {
                if (!newEpeson.getIssequence()) {
                    if (newEpesons.contains(UUID.fromString(newEpeson.getePersonRest().getId()))) {
                        System.out.println("in match");
                    } else {
                        System.out.println("in not match");
                        newUserList.add(workFlowProcessEpersonConverter.convert(context, newEpeson));
                    }
                }
            }
            Comparator<WorkflowProcessEperson> c = (a, b) -> a.getIndex().compareTo(b.getIndex());
            List<WorkflowProcessEperson> list = newUserList.stream().sorted(c).collect(Collectors.toList());
            for (WorkflowProcessEperson workflowProcessEperson : list) {
                System.out.println("test" + workflowProcessEperson.getIndex());
                workflowProcessEperson.setWorkflowProcess(workFlowProcess);
                Optional<WorkFlowProcessMasterValue> userTypeOption = WorkFlowUserType.NORMAL.getUserTypeFromMasterValue(context);
                if (userTypeOption.isPresent()) {
                    workflowProcessEperson.setUsertype(userTypeOption.get());
                }
                workFlowProcess.setnewUser(workflowProcessEperson);
            }
            workflowProcessService.create(context, workFlowProcess);
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction action = WorkFlowAction.FORWARD;
            if (comment != null) {
                action.setComment(comment);
                if (workFlowProcessRest.getWorkflowProcessReferenceDocRests() != null && workFlowProcessRest.getWorkflowProcessReferenceDocRests().size() != 0) {
                    List<WorkflowProcessReferenceDoc> doc = getCommentDocuments(context, null, workFlowProcessRest);
                    if (doc != null) {
                        action.setWorkflowProcessReferenceDocs(doc);
                    }
                }
            }
            action.perfomeAction(context, workFlowProcess, workFlowProcessRest);
            context.commit();
            action.setComment(null);
            action.setWorkflowProcessReferenceDocs(null);
            return workFlowProcessRest;
        } catch (
                RuntimeException e) {
            e.printStackTrace();
            throw new UnprocessableEntityException("error in forwardTask Server..");
        }

    }

    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD}, value = "backward")
    public WorkFlowProcessRest backward(@PathVariable UUID uuid,
                                        HttpServletRequest request) throws IOException, SQLException, AuthorizeException {
        WorkFlowProcessRest workFlowProcessRest = null;
        WorkflowProcessEpersonRest workflowProcessEpersonRest = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            ObjectMapper mapper = new ObjectMapper();
            workflowProcessEpersonRest = mapper.readValue(request.getInputStream(), WorkflowProcessEpersonRest.class);
            String comment = workflowProcessEpersonRest.getComment();
            WorkflowProcess workFlowProcess = workflowProcessService.find(context, uuid);
            if (workFlowProcess.getItem() == null && workflowProcessEpersonRest.getItemRest() != null) {
                workFlowProcess.setItem(itemConverter.convert(workflowProcessEpersonRest.getItemRest(), context));
            }
            Optional<WorkFlowProcessMasterValue> workFlowTypeStatus = WorkFlowStatus.INPROGRESS.getUserTypeFromMasterValue(context);
            if (workFlowTypeStatus.isPresent()) {
                workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            }

            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction backward = WorkFlowAction.BACKWARD;
            if (comment != null) {
                backward.setComment(comment);
                if (workflowProcessEpersonRest.getWorkflowProcessReferenceDocRests() != null && workflowProcessEpersonRest.getWorkflowProcessReferenceDocRests().size() != 0) {
                    List<WorkflowProcessReferenceDoc> doc = getCommentDocuments(context, workflowProcessEpersonRest, null);
                    if (doc != null) {
                        backward.setWorkflowProcessReferenceDocs(doc);
                    }
                }
            }

            backward.perfomeAction(context, workFlowProcess, workFlowProcessRest);
            context.commit();
            backward.setComment(null);
            backward.setWorkflowProcessReferenceDocs(null);
            return workFlowProcessRest;
        } catch (RuntimeException e) {
            throw new UnprocessableEntityException("error in forwardTask Server..");
        }
    }

    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD}, value = "completed")
    public WorkFlowProcessRest complete(@PathVariable UUID uuid,
                                        HttpServletRequest request) throws Exception {
        WorkFlowProcessRest workFlowProcessRest = null;
        WorkflowProcessEpersonRest workflowProcessEpersonRest = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            ObjectMapper mapper = new ObjectMapper();
            workflowProcessEpersonRest = mapper.readValue(request.getInputStream(), WorkflowProcessEpersonRest.class);
            String comment = workflowProcessEpersonRest.getComment();
            WorkflowProcess workFlowProcess = workflowProcessService.find(context, uuid);
            if (workFlowProcess.getEligibleForFiling() != null && workFlowProcess.getEligibleForFiling().getPrimaryvalue() == "Yes" && workFlowProcess.getItem() == null) {
                throw new ResourceNotFoundException("Item ID not found");
            }
            if (workFlowProcess.getItem() == null && workflowProcessEpersonRest.getItemRest() != null) {
                workFlowProcess.setItem(itemConverter.convert(workflowProcessEpersonRest.getItemRest(), context));
            }
            Optional<WorkFlowProcessMasterValue> workFlowTypeStatus = WorkFlowStatus.CLOSE.getUserTypeFromMasterValue(context);
            if (workFlowTypeStatus.isPresent()) {
                workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            }
            Item item = workFlowProcess.getItem();
            if (item != null) {
                workFlowProcess.getWorkflowProcessReferenceDocs().forEach(wd -> {
                    try {
                        workflowProcessService.storeWorkFlowMataDataTOBitsream(context, wd, item);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (AuthorizeException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                throw new ResourceNotFoundException("Item not found");
            }
            workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            workflowProcessService.create(context, workFlowProcess);
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction COMPLETE = WorkFlowAction.COMPLETE;
            if (comment != null) {
                COMPLETE.setComment(comment);
                if (workflowProcessEpersonRest.getWorkflowProcessReferenceDocRests() != null && workflowProcessEpersonRest.getWorkflowProcessReferenceDocRests().size() != 0) {
                    List<WorkflowProcessReferenceDoc> doc = getCommentDocuments(context, workflowProcessEpersonRest, null);
                    if (doc != null) {
                        COMPLETE.setWorkflowProcessReferenceDocs(doc);
                    }
                }
            }
            COMPLETE.perfomeAction(context, workFlowProcess, workFlowProcessRest);
            if (workFlowProcess.getWorkflowType().getPrimaryvalue().equals("Draft")) {
                WorkflowProcessReferenceDoc notedoc = createFinalNote(context, workFlowProcess);
                if (notedoc != null && item != null) {
                    workflowProcessService.storeWorkFlowMataDataTOBitsream(context, notedoc, item);
                }
            }
            context.commit();
            COMPLETE.setComment(null);
            COMPLETE.setWorkflowProcessReferenceDocs(null);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        return workFlowProcessRest;
    }

    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD}, value = "dispatch")
    public WorkFlowProcessRest dispatch(@PathVariable UUID uuid,
                                        HttpServletRequest request, @RequestBody String comment) throws IOException, SQLException, AuthorizeException {
        WorkFlowProcessRest workFlowProcessRest = null;
        WorkflowProcessEpersonRest workflowProcessEpersonRest = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            WorkflowProcess workFlowProcess = workflowProcessService.find(context, uuid);
            if (workFlowProcess.getWorkFlowProcessOutwardDetails() == null || workFlowProcess.getWorkFlowProcessOutwardDetails().getOutwardDepartment() == null) {
                throw new ResourceNotFoundException("Dispatch not found");
            }
            Optional<WorkFlowProcessMasterValue> workFlowTypeStatus = WorkFlowStatus.DISPATCH.getUserTypeFromMasterValue(context);
            if (workFlowTypeStatus.isPresent()) {
                workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            }
            workFlowProcess.getWorkFlowProcessOutwardDetails().getOutwardDepartment().getMembers().forEach(e -> {
                System.out.println("eperson:::" + e.getEmail());
                WorkflowProcessEperson workflowProcessEpersonFromGroup = workFlowProcessEpersonConverter.convert(context, e);
                try {
                    Optional<WorkFlowProcessMasterValue> workFlowUserTypOptional = WorkFlowUserType.DISPATCH.getUserTypeFromMasterValue(context);
                    if (workFlowUserTypOptional.isPresent()) {
                        workflowProcessEpersonFromGroup.setUsertype(workFlowUserTypOptional.get());
                    }
                    workflowProcessEpersonFromGroup.setWorkflowProcess(workFlowProcess);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                workFlowProcess.setnewUser(workflowProcessEpersonFromGroup);
            });
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction DISPATCH = WorkFlowAction.DISPATCH;
            if (comment != null) {
                DISPATCH.setComment(comment);
            }
            DISPATCH.perfomeAction(context, workFlowProcess, workFlowProcessRest);
            workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            workflowProcessService.create(context, workFlowProcess);
            DISPATCH.setComment(null);
            context.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return workFlowProcessRest;
    }

    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD}, value = "dispatchComplete")
    public WorkFlowProcessRest dispatchComplete(@PathVariable String uuid, HttpServletRequest request, @RequestBody @Valid WorkFlowProcessOutwardDetailsRest outwardDetailsRest) throws IOException, SQLException, AuthorizeException {
        WorkFlowProcessRest workFlowProcessRest = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            String comment = "Close";
            WorkflowProcess workFlowProcess = workflowProcessService.find(context, UUID.fromString(uuid));
            if (workFlowProcess.getWorkFlowProcessOutwardDetails() == null || workFlowProcess.getWorkFlowProcessOutwardDetails().getOutwardDepartment() == null) {
                throw new ResourceNotFoundException("Dispatch not found");
            }
            if (workFlowProcess.getWorkFlowProcessOutwardDetails() != null) {
                WorkFlowProcessOutwardDetails workFlowProcessOutwardDetails = workFlowProcess.getWorkFlowProcessOutwardDetails();
                workFlowProcessOutwardDetails.setAwbno(outwardDetailsRest.getAwbno());
                workFlowProcessOutwardDetails.setServiceprovider(outwardDetailsRest.getServiceprovider());
                workFlowProcessOutwardDetails.setDispatchdate(outwardDetailsRest.getDispatchdate());
                workFlowProcess.setWorkFlowProcessOutwardDetails(workFlowProcessOutwardDetails);
            }
            Optional<WorkFlowProcessMasterValue> workFlowTypeStatus = WorkFlowStatus.CLOSE.getUserTypeFromMasterValue(context);
            if (workFlowTypeStatus.isPresent()) {
                workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            }
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction COMPLETE = WorkFlowAction.COMPLETE;
            if (comment != null) {
                COMPLETE.setComment(comment);
            }
            COMPLETE.perfomeAction(context, workFlowProcess, workFlowProcessRest);
            workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            workflowProcessService.create(context, workFlowProcess);
            context.commit();
            COMPLETE.setComment(null);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return workFlowProcessRest;
    }

    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD}, value = "suspend")
    public WorkFlowProcessRest suspend(@PathVariable UUID uuid,
                                       HttpServletRequest request) throws IOException, SQLException {
        WorkFlowProcessRest workFlowProcessRest = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            WorkflowProcess workFlowProcess = workflowProcessService.find(context, uuid);
            if (workFlowProcess.getWorkflowType().getPrimaryvalue().equals("Draft")) {
                createFinalNote(context, workFlowProcess);
            }
            if (workFlowProcess == null) {
                throw new RuntimeException("Workflow not found");
            }
            Optional<WorkFlowProcessMasterValue> workFlowTypeStatus = WorkFlowStatus.SUSPEND.getUserTypeFromMasterValue(context);
            if (workFlowTypeStatus.isPresent()) {
                workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            }
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction holdAction = WorkFlowAction.HOLD;
            holdAction.perfomeAction(context, workFlowProcess, workFlowProcessRest);
            workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            workflowProcessService.create(context, workFlowProcess);
            context.commit();
            holdAction.setComment(null);
            return workFlowProcessRest;
        } catch (RuntimeException | ParseException e) {
            throw new UnprocessableEntityException("error in suspendTask Server..");
        } catch (AuthorizeException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
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
            Optional<WorkFlowProcessMasterValue> workFlowTypeStatus = WorkFlowStatus.INPROGRESS.getUserTypeFromMasterValue(context);
            if (workFlowTypeStatus.isPresent()) {
                workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            }
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction unholdAction = WorkFlowAction.UNHOLD;
            unholdAction.perfomeAction(context, workFlowProcess, workFlowProcessRest);
            workFlowProcess.setWorkflowStatus(workFlowTypeStatus.get());
            workflowProcessService.create(context, workFlowProcess);
            context.commit();
            unholdAction.setComment(null);
            return workFlowProcessRest;
        } catch (RuntimeException e) {
            throw new UnprocessableEntityException("error in forwardTask Server..");
        }
    }


    public WorkflowProcessReferenceDoc createFinalNote(Context context, WorkflowProcess workflowProcess) throws IOException, SQLException, AuthorizeException, DocumentException, ParseException {
        boolean isTextEditorFlow = false;
        Map<String, Object> map = new HashMap<String, Object>();
        InputStream input2 = null;
        final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
        System.out.println("start.......createFinalNote");
        StringBuffer sb = new StringBuffer("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head><style>@page{size:A4;margin: 0;}</style>\n" +
                "<title>Note</title>\n" +
                "</head>\n" +
                "<body style=\"padding-right: 20px;padding-left: 20px;background-color:#c5e6c1;\">");
        long notecount = 0;
        if (workflowProcess.getItem() != null && workflowProcess.getItem().getName() != null) {
            UUID statusid = WorkFlowStatus.CLOSE.getUserTypeFromMasterValue(context).get().getID();
            notecount = workflowProcessNoteService.getNoteCountNumber(context, workflowProcess.getItem().getID(), statusid);
            map.put("notecount", notecount);
        }
        notecount = notecount + 1;
        File tempFileDoc = new File(TEMP_DIRECTORY, "Note#" + notecount + ".docx");
        File tempFile1html = new File(TEMP_DIRECTORY, "Note#" + notecount + ".pdf");
        if (!tempFile1html.exists()) {
            try {
                tempFile1html.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (!tempFileDoc.exists()) {
            try {
                tempFileDoc.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("start.......createFinalNote" + tempFile1html.getAbsolutePath());
        //Items
        sb.append("<p><center> <b>Note # " + notecount + "</b></center></p>");
        for (WorkflowProcessReferenceDoc workflowProcessReferenceDoc : workflowProcess.getWorkflowProcessReferenceDocs()) {
            if (workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue() != null && workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue().equals("Notesheet")) {
                System.out.println("in notsheet pdf");
                List<WorkflowProcessReferenceDocVersion> versions = workflowProcessReferenceDocVersionService.getDocVersionBydocumentID(context, workflowProcessReferenceDoc.getID(), 0, 20);
                Optional<WorkflowProcessReferenceDocVersion> vvv = versions.stream().filter(d -> d.getIsactive()).findFirst();
                WorkflowProcessReferenceDocVersion version = null;
                if (versions != null) {
                    for (WorkflowProcessReferenceDocVersion v : versions) {
                        if (v.getIsactive()) {
                            System.out.println("Active version" + v.getVersionnumber());
                            version = v;
                        }
                    }
                    InputStream out = null;
                    if (version.getBitstream() != null && version.getBitstream().getName() != null) {
                        out = bitstreamService.retrieve(context, version.getBitstream());
                        if (out != null) {
                            input2 = out;
                            try (XWPFDocument doc = new XWPFDocument(out)) {
                                XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(doc);
                                sb.append("<div>\n" +
                                        "<p>" + xwpfWordExtractor.getText() + "</p>\n" +
                                        "</div>");
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("error in Doc Read");
                            }
                        }
                    }
                    if (version.getEditortext() != null && !version.getEditortext().isEmpty()) {
                        if (version != null) {
                            if (version.getEditortext() != null && version.getBitstream() == null) {
                                isTextEditorFlow = true;
                                sb.append("<div>" + version.getEditortext() + "</div>");
                            }
                        }
                    }
                }
            }
        }

        //manager
        if (workflowProcess.getWorkflowProcessNote() != null && workflowProcess.getWorkflowProcessNote().getSubmitter() != null) {
            EPerson creator = workflowProcess.getWorkflowProcessNote().getSubmitter();
            String Designation1 = workFlowProcessMasterValueService.find(context, creator.getDesignation().getID()).getPrimaryvalue();
            List<String> aa = new ArrayList<>();
            aa.add(workflowProcess.getWorkflowProcessNote().getSubmitter().getFullName());
            if (Designation1 != null) {
                aa.add(Designation1);
                sb.append("<br><br><br><div style=\"width:100%;    text-align: right;\">\n" +
                        "<span>" + workflowProcess.getWorkflowProcessNote().getSubmitter().getFullName() + "<br>" + Designation1);
                aa.add(DateFormate(workflowProcess.getWorkflowProcessNote().getInitDate()));
                sb.append("<br>" + DateFormate(workflowProcess.getWorkflowProcessNote().getInitDate()) + "</span></div>");
            }
            map.put("creator", aa);
        }
        Map<String, String> referencedocumentmap = new HashMap<String, String>();
        sb.append("<br><br><div style=\"width:100%;\"> ");
        sb.append("<div style=\"width:70%;  float:left;\"> <p><b>Reference Documents</b></p> ");
        //Reference Documents dinamix
        for (WorkflowProcessReferenceDoc workflowProcessReferenceDoc : workflowProcess.getWorkflowProcessReferenceDocs()) {
            if (workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue() != null && workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue().equals("Reference Document")) {
                // InputStream out = null;
                if (workflowProcessReferenceDoc.getBitstream() != null) {
                    String baseurl = configurationService.getProperty("dspace.server.url");
                    referencedocumentmap.put("name", FileUtils.getNameWithoutExtension(workflowProcessReferenceDoc.getBitstream().getName()));
                    referencedocumentmap.put("link", baseurl + "/api/core/bitstreams/" + workflowProcessReferenceDoc.getBitstream().getID() + "/content");
                    sb.append("<span style=\"text-align: left;\"><a href=" + baseurl + "/api/core/bitstreams/" + workflowProcessReferenceDoc.getBitstream().getID() + "/content>" + FileUtils.getNameWithoutExtension(workflowProcessReferenceDoc.getBitstream().getName()) + "</a> ");
                   /* if (workflowProcessReferenceDoc.getReferenceNumber() != null) {
                        sb.append(workflowProcessReferenceDoc.getReferenceNumber());
                    } else {
                        sb.append("-");
                    }
                    if (workflowProcessReferenceDoc.getInitdate() != null) {
                        sb.append("<br>" + DateUtils.DateFormateDDMMYYYY(workflowProcessReferenceDoc.getInitdate()));
                    } else {
                        sb.append("<br>-");
                    }
                    if (workflowProcessReferenceDoc.getLatterCategory() != null && workflowProcessReferenceDoc.getLatterCategory().getPrimaryvalue() != null) {
                        sb.append(" (" + workflowProcessReferenceDoc.getLatterCategory().getPrimaryvalue() + ")");
                    } else {
                        sb.append("(-)");
                    }
                    if (workflowProcessReferenceDoc.getSubject() != null) {
                        sb.append("<br>" + workflowProcessReferenceDoc.getSubject());
                    } else {
                        sb.append("<br> -");
                    }
                    sb.append("</span><br><br>");*/
                    if (workflowProcessReferenceDoc.getBitstream().getMetadata() != null) {
                        int i = 0;
                        String refnumber = null;
                        String date = null;
                        String lettercategory = null;
                        String description = null;
                        for (MetadataValue metadataValue : workflowProcessReferenceDoc.getBitstream().getMetadata()) {
                            if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_ref_number")) {
                                refnumber = metadataValue.getValue();
                                System.out.println(i + "dc_ref_number :" + metadataValue.getValue());
                            }
                            if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_date")) {
                                date = metadataValue.getValue();
                                System.out.println(i + "dc_date :" + metadataValue.getValue());
                            }
                            if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_letter_category")) {
                                lettercategory = metadataValue.getValue();
                                System.out.println(i + "dc_letter_category :" + metadataValue.getValue());
                            }
                            if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_description")) {
                                description = metadataValue.getValue();
                                System.out.println(i + "dc_description :" + metadataValue.getValue());
                            }
                            if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_title")) {
                                System.out.println(i + "title :" + metadataValue.getValue());
                            }
                            i++;
                        }

                        if (refnumber != null) {
                            sb.append("("+refnumber+")");
                        } else {
                            sb.append("-");
                        }
                        if (date != null) {
                            sb.append("<br>" + DateUtils.strDateToString(date));
                        } else {
                            sb.append("<br>-");
                        }
                        if (lettercategory != null) {
                            sb.append(" (" + lettercategory + ")");
                        } else {
                            sb.append("(-)");
                        }
                        if (description != null) {
                            sb.append("<br>" + description);
                        } else {
                            sb.append("<br> -");
                        }
                        sb.append("</span><br><br>");
                    }
                }
            }

        }
        sb.append("</div>");
        map.put("Reference Documents", referencedocumentmap);
        sb.append("<div style=\"width:23%;float:right;\"> <p><b>Reference Noting</b></p> ");
        Map<String, String> referencenottingmap = new HashMap<String, String>();
        for (WorkflowProcessReferenceDoc workflowProcessReferenceDoc : workflowProcess.getWorkflowProcessReferenceDocs()) {
            if (workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue() != null && workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue().equals("Reference Noting")) {
                if (workflowProcessReferenceDoc.getBitstream() != null) {
                    String baseurl = configurationService.getProperty("dspace.server.url");
                    referencenottingmap.put("name", FileUtils.getNameWithoutExtension(workflowProcessReferenceDoc.getBitstream().getName()));
                    referencenottingmap.put("link", baseurl + "/api/core/bitstreams/" + workflowProcessReferenceDoc.getBitstream().getID() + "/content");
                    sb.append("<span style=\"text-align: left;\"><a href=" + baseurl + "/api/core/bitstreams/" + workflowProcessReferenceDoc.getBitstream().getID() + "/content>" + FileUtils.getNameWithoutExtension(workflowProcessReferenceDoc.getBitstream().getName()) + "</a> ");

                   /* if (workflowProcessReferenceDoc.getReferenceNumber() != null) {
                        sb.append(workflowProcessReferenceDoc.getReferenceNumber());
                    } else {
                        sb.append("-");
                    }
                    if (workflowProcessReferenceDoc.getInitdate() != null) {
                        sb.append("<br>" + DateUtils.DateFormateDDMMYYYY(workflowProcessReferenceDoc.getInitdate()));
                    } else {
                        sb.append("<br>-");
                    }
                    if (workflowProcessReferenceDoc.getLatterCategory() != null && workflowProcessReferenceDoc.getLatterCategory().getPrimaryvalue() != null) {
                        sb.append(" (" + workflowProcessReferenceDoc.getLatterCategory().getPrimaryvalue() + ")");
                    } else {
                        sb.append("(-)");
                    }
                    if (workflowProcessReferenceDoc.getSubject() != null) {
                        sb.append("<br>" + workflowProcessReferenceDoc.getSubject());
                    } else {
                        sb.append("<br> -");
                    }
                    sb.append("</span><br><br>");*/

                    if (workflowProcessReferenceDoc.getBitstream().getMetadata() != null) {
                        int i = 0;
                        String refnumber = null;
                        String date = null;
                        String lettercategory = null;
                        String description = null;
                     for (MetadataValue metadataValue : workflowProcessReferenceDoc.getBitstream().getMetadata()) {
                            if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_ref_number")) {
                                refnumber = metadataValue.getValue();
                                System.out.println(i + "dc_ref_number :" + metadataValue.getValue());
                            }
                            if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_date")) {
                                date = metadataValue.getValue();
                                System.out.println(i + "dc_date :" + metadataValue.getValue());
                            }
                            if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_letter_category")) {
                                lettercategory = metadataValue.getValue();
                                System.out.println(i + "dc_letter_category :" + metadataValue.getValue());
                            }
                            if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_description")) {
                                description = metadataValue.getValue();
                                System.out.println(i + "dc_description :" + metadataValue.getValue());
                            }
                            if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_title")) {
                                System.out.println(i + "title :" + metadataValue.getValue());
                            }
                            i++;
                        }

                        if (refnumber != null) {
                            sb.append("("+refnumber+")");
                        } else {
                            sb.append("-");
                        }
                        if (date != null) {
                            sb.append("<br>" + DateUtils.strDateToString(date));
                        } else {
                            sb.append("<br>-");
                        }
                        if (lettercategory != null) {
                            sb.append(" (" + lettercategory + ")");
                        } else {
                            sb.append("(-)");
                        }
                        if (description != null) {
                            sb.append("<br>" + description);
                        } else {
                            sb.append("<br> -");
                        }
                        sb.append("</span><br><br>");
                    }
                }
            }
        }
        sb.append("</div></div><br>");
        map.put("Reference Noting", referencenottingmap);
        // pending file
        List<WorkFlowProcessComment> comments = workFlowProcessCommentService.getComments(context, workflowProcess.getID());
        map.put("comment", comments);
        sb.append("<h3 style=\"width:100% ;text-align: left; float:left;\"> Comments [" + comments.size() + "]</h3>");
        int i = 1;
        for (WorkFlowProcessComment comment : comments) {
            sb.append("<div style=\"width:100% ;text-align: left; float:left;\">");
            //coment count
            sb.append("<p><b>Comment # " + i + "</b></p>");

            //comment text
            if (comment.getComment() != null) {
                sb.append("<span>" + comment.getComment() + "</span>");
            }
            if(comment.getWorkflowProcessReferenceDoc()!=null && comment.getWorkflowProcessReferenceDoc().size()!=0){
                sb.append("<br><br>");
                sb.append("<span><b>Reference Comments.</b></span> <br>");
                for (WorkflowProcessReferenceDoc workflowProcessReferenceDoc : comment.getWorkflowProcessReferenceDoc()) {
                    if (workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue() != null && workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue().equals("Comment")) {
                        if (workflowProcessReferenceDoc.getBitstream() != null) {
                            String baseurl = configurationService.getProperty("dspace.server.url");
                            sb.append("<span> <a href=" + baseurl + "/api/core/bitstreams/" + workflowProcessReferenceDoc.getBitstream().getID() + "/content>" + FileUtils.getNameWithoutExtension(workflowProcessReferenceDoc.getBitstream().getName()) + "</a> ");
                            if (workflowProcessReferenceDoc.getBitstream().getMetadata() != null) {
                                int ii = 0;
                                String refnumber = null;
                                String date = null;
                                String lettercategory = null;
                                String description = null;
                                for (MetadataValue metadataValue : workflowProcessReferenceDoc.getBitstream().getMetadata()) {
                                    if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_ref_number")) {
                                        refnumber = metadataValue.getValue();
                                        System.out.println(ii + "dc_ref_number :" + metadataValue.getValue());
                                    }
                                    if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_date")) {
                                        date = metadataValue.getValue();
                                        System.out.println(ii + "dc_date :" + metadataValue.getValue());
                                    }
                                    if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_letter_category")) {
                                        lettercategory = metadataValue.getValue();
                                        System.out.println(ii + "dc_letter_category :" + metadataValue.getValue());
                                    }
                                    if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_description")) {
                                        description = metadataValue.getValue();
                                        System.out.println(ii + "dc_description :" + metadataValue.getValue());
                                    }
                                    if (metadataValue.getMetadataField() != null && metadataValue.getMetadataField().toString().equalsIgnoreCase("dc_title")) {
                                        System.out.println(ii + "title :" + metadataValue.getValue());
                                    }
                                    ii++;
                                }

                                if (refnumber != null) {
                                    sb.append("("+refnumber+")");
                                } else {
                                    sb.append("-");
                                }
                                if (date != null) {
                                    sb.append("<br>" + DateUtils.strDateToString(date));
                                } else {
                                    sb.append("<br>-");
                                }
                                if (lettercategory != null) {
                                    sb.append(" (" + lettercategory + ")");
                                } else {
                                    sb.append("(-)");
                                }
                                if (description != null) {
                                    sb.append("<br>" + description);
                                } else {
                                    sb.append("<br> -");
                                }
                                sb.append("</span><br><br>");
                            }
                        }
                    }
                }

            }
            sb.append("</div>");
            //Comment by
            if (comment.getComment() != null) {
                sb.append("<div style=\"    float: right;  width:23%\"><b>Comment By</b><br><span>");
            }
            //MAnager
            if (comment.getSubmitter() != null) {
                if (comment.getSubmitter().getFullName() != null) {
                    sb.append("<br>" + comment.getSubmitter().getFullName());
                }
                if (comment.getSubmitter().getDesignation() != null) {
                    String Designation = workFlowProcessMasterValueService.find(context, comment.getSubmitter().getDesignation().getID()).getPrimaryvalue();
                    sb.append("<br>" + Designation);
                }
            }
            //
            if (comment.getWorkFlowProcessHistory() != null && comment.getWorkFlowProcessHistory().toString() != null) {
                sb.append("<br>" + DateFormate(comment.getWorkFlowProcessHistory().getActionDate()));
            }
            sb.append("</div>");
            i++;
        }
        sb.append("</body></html>");
        if (isTextEditorFlow) {
            System.out.println("::::::::::IN isTextEditorFlow :::::::::");
            FileOutputStream files = new FileOutputStream(new File(tempFile1html.getAbsolutePath()));
            System.out.println("HTML:::" + sb.toString());
            int result = PdfUtils.HtmlconvertToPdf(sb.toString(), files);
            System.out.println("HTML CONVERT DONE::::::::::::::: :" + tempFile1html.getAbsolutePath());
            WorkflowProcessReferenceDoc margedoc = new WorkflowProcessReferenceDoc();
            margedoc.setSubject(workflowProcess.getSubject());
            margedoc.setDescription(workflowProcess.getSubject() + " for " + tempFile1html.getName());
            margedoc.setInitdate(new Date());
            WorkFlowProcessMaster workFlowProcessMaster = workFlowProcessMasterService.findByName(context, "Draft Type");
            if (workFlowProcessMaster != null) {
                WorkFlowProcessMasterValue workFlowProcessMasterValue = workFlowProcessMasterValueService.findByName(context, "Note", workFlowProcessMaster);
                if (workFlowProcessMasterValue != null) {
                    margedoc.setDrafttype(workFlowProcessMasterValue);
                }
            }
            margedoc.setWorkflowProcess(workflowProcess);
            FileInputStream outputfile = new FileInputStream(new File(tempFile1html.getAbsolutePath()));
            Bitstream bitstream = bundleRestRepository.processBitstreamCreationWithoutBundle(context, outputfile, "", tempFile1html.getName());
            margedoc.setBitstream(bitstream);
            if (workflowProcess.getWorkflowProcessNote() != null) {
                margedoc.setWorkflowprocessnote(workflowProcess.getWorkflowProcessNote());
                for (WorkflowProcessReferenceDoc d : workflowProcess.getWorkflowProcessReferenceDocs()) {
                    if (d.getDrafttype() != null && d.getDrafttype().getPrimaryvalue() != null && !d.getDrafttype().getPrimaryvalue().equalsIgnoreCase("Notesheet"))
                        d.setWorkflowprocessnote(workflowProcess.getWorkflowProcessNote());
                    workflowProcessReferenceDocService.update(context, d);
                }
            }
            margedoc = workflowProcessReferenceDocService.create(context, margedoc);
            return margedoc;
        } else {
            System.out.println(":::::::::In Document flow:::::::::::::::");
            MargedDocUtils.DocOneWrite(notecount);
            MargedDocUtils.DocTwoWrite(input2);
            MargedDocUtils.DocthreWrite(map);
            MargedDocUtils.finalwriteDocument(tempFileDoc.getAbsolutePath());

            System.out.println("tmp path :" + tempFileDoc.getAbsolutePath());
            System.out.println("tmp tempFile1html :" + tempFileDoc.getAbsolutePath());
            WorkflowProcessReferenceDoc margedoc = new WorkflowProcessReferenceDoc();
            margedoc.setSubject(workflowProcess.getSubject());
            margedoc.setDescription(workflowProcess.getSubject() + " for " + tempFileDoc.getName());
            margedoc.setInitdate(new Date());
            margedoc.setReferenceNumber(""+notecount);
            WorkFlowProcessMaster workFlowProcessMaster = workFlowProcessMasterService.findByName(context, "Draft Type");
            if (workFlowProcessMaster != null) {
                WorkFlowProcessMasterValue workFlowProcessMasterValue = workFlowProcessMasterValueService.findByName(context, "Note", workFlowProcessMaster);
                if (workFlowProcessMasterValue != null) {
                    margedoc.setDrafttype(workFlowProcessMasterValue);
                }
            }
            margedoc.setWorkflowProcess(workflowProcess);
            FileInputStream outputfile = new FileInputStream(new File(tempFileDoc.getAbsolutePath()));
            Bitstream bitstream = bundleRestRepository.processBitstreamCreationWithoutBundle(context, outputfile, "", tempFileDoc.getName());
            margedoc.setBitstream(bitstream);
            if (workflowProcess.getWorkflowProcessNote() != null) {
                margedoc.setWorkflowprocessnote(workflowProcess.getWorkflowProcessNote());
                for (WorkflowProcessReferenceDoc d : workflowProcess.getWorkflowProcessReferenceDocs()) {
                    if (d.getDrafttype() != null && d.getDrafttype().getPrimaryvalue() != null && !d.getDrafttype().getPrimaryvalue().equalsIgnoreCase("Notesheet"))
                        d.setWorkflowprocessnote(workflowProcess.getWorkflowProcessNote());
                    workflowProcessReferenceDocService.update(context, d);
                }
            }
            margedoc = workflowProcessReferenceDocService.create(context, margedoc);
            return margedoc;
        }
    }

    public List<WorkflowProcessReferenceDoc> getCommentDocuments(Context context, WorkflowProcessEpersonRest erest, WorkFlowProcessRest wrest) {
        List<WorkflowProcessReferenceDoc> docs = null;
        if (erest.getWorkflowProcessReferenceDocRests() != null) {
            docs = erest.getWorkflowProcessReferenceDocRests().stream().map(d ->
                    {
                        try {
                            return workflowProcessReferenceDocConverter.convertByService(context, d);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ).collect(Collectors.toList());
        }else {
            if (wrest.getWorkflowProcessReferenceDocRests() != null) {
                if (wrest.getWorkflowProcessReferenceDocRests() != null) {
                    docs = wrest.getWorkflowProcessReferenceDocRests().stream().map(d ->
                            {
                                try {
                                    return workflowProcessReferenceDocConverter.convertByService(context, d);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    ).collect(Collectors.toList());
                }
            }
        }
        return docs;
    }

    private static String DateFormate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        return formatter.format(date);
    }
}
