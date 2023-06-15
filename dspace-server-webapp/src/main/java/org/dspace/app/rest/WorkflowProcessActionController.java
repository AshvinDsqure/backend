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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.dspace.app.rest.converter.ItemConverter;
import org.dspace.app.rest.converter.WorkFlowProcessConverter;
import org.dspace.app.rest.converter.WorkFlowProcessEpersonConverter;
import org.dspace.app.rest.converter.WorkFlowProcessOutwardDetailsConverter;
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
    EventService eventService;
    @Autowired
    MetadataFieldService metadataFieldService;
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
            System.out.println("Comment::::::::::::::::::::::::::"+comment);
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
            workFlowProcess.setnewUser(workflowProcessEperson);
            workflowProcessService.create(context, workFlowProcess);
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction action = WorkFlowAction.FORWARD;
            if (comment != null) {
                action.setComment(comment);
            }
            action.perfomeAction(context, workFlowProcess, workFlowProcessRest);
            context.commit();
            action.setComment(null);
            return workFlowProcessRest;
        } catch (RuntimeException e) {
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
            workFlowProcessRest = workFlowProcessConverter.convert(workFlowProcess, utils.obtainProjection());
            WorkFlowAction backward = WorkFlowAction.BACKWARD;
            if (comment != null) {
                backward.setComment(comment);
            }
            backward.perfomeAction(context, workFlowProcess, workFlowProcessRest);
            context.commit();
            backward.setComment(null);
            return workFlowProcessRest;
        } catch (RuntimeException e) {
            throw new UnprocessableEntityException("error in forwardTask Server..");
        }
    }

    @PreAuthorize("hasPermission(#uuid, 'ITEAM', 'READ')")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.HEAD}, value = "completed")
    public WorkFlowProcessRest complete(@PathVariable UUID uuid,
                                        HttpServletRequest request) throws IOException, SQLException, AuthorizeException {
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
        } catch (RuntimeException e) {
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
            WorkFlowAction workFlowAction = WorkFlowAction.UNHOLD;
            try {
                String body = jbpmServer.resumeTask(workFlowProcessRest, workFlowAction);
                System.out.println("body:::" + body);
                // storeHistory(context,workFlowAction,workFlowProcess);
                workflowProcessService.create(context, workFlowProcess);
                context.commit();
                workFlowAction.setComment(null);
            } catch (RuntimeException e) {
                e.printStackTrace();
                throw new UnprocessableEntityException("error in BPM Server..");
            }

            return workFlowProcessRest;
        } catch (RuntimeException e) {
            throw new UnprocessableEntityException("error in forwardTask Server..");
        }
    }


    public WorkflowProcessReferenceDoc createFinalNote(Context context, WorkflowProcess workflowProcess) throws IOException, SQLException, AuthorizeException, DocumentException {
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
            List<Bundle> bundles = workflowProcess.getItem().getBundles("ORIGINAL");
            List<Bitstream> bitstreams = new ArrayList<>();
            if (bundles.size() != 0) {
                bitstreams = bundles.stream().findFirst().get().getBitstreams();
            }
            notecount = bitstreams.stream().filter(b -> b.getName().contains("Note#")).count();
            map.put("notecount", notecount);
        }
        notecount = notecount + 1;
        Rectangle pageSize = new Rectangle(PageSize.A4);
        pageSize.setBackgroundColor(new BaseColor(0xD9, 0xFD, 0xD4));
        Document document = new Document(pageSize);
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
        FileOutputStream outputstream = new FileOutputStream(new File(tempFile1html.getAbsolutePath()));
        PdfWriter.getInstance(document, outputstream);
        document.open();
        //Items
        Paragraph p4 = new Paragraph("Note#" + notecount, COURIER_SMALL_FOOTER);
        sb.append("<p><center> <b>Note # " + notecount + "</b></center></p>");
        leaveEmptyLine(p4, 1);
        p4.setAlignment(Element.ALIGN_CENTER);
        document.add(p4);
        if (workflowProcess.getSubject() != null) {
            Paragraph p4q = new Paragraph(workflowProcess.getSubject() + ",", COURIER_SMALL_FOOTER);
            leaveEmptyLine(p4q, 1);
            p4q.setAlignment(Element.ALIGN_CENTER);
            document.add(p4q);
        }
        for (WorkflowProcessReferenceDoc workflowProcessReferenceDoc : workflowProcess.getWorkflowProcessReferenceDocs()) {
            if (workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue() != null && workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue().equals("Notesheet")) {
                System.out.println("in notsheet pdf");
                List<WorkflowProcessReferenceDocVersion> versions = workflowProcessReferenceDocVersionService.getDocVersionBydocumentID(context, workflowProcessReferenceDoc.getID(), 0, 20);
               Optional<WorkflowProcessReferenceDocVersion>vvv= versions.stream().filter(d->d.getIsactive()).findFirst();
                WorkflowProcessReferenceDocVersion version = null;
                if (versions != null) {
                    for (WorkflowProcessReferenceDocVersion v : versions) {
                        if (v.getIsactive()) {
                            System.out.println("Active version"+v.getVersionnumber());
                            version = v;
                        }
                    }
                    InputStream out = null;
                    if (version.getBitstream() != null && version.getBitstream().getName() != null) {
                        out = bitstreamService.retrieve(context, version.getBitstream());
                        if (out != null) {
                            input2=out;
                            try (XWPFDocument doc = new XWPFDocument(out)) {
                                XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(doc);
                                sb.append("<div>\n" +
                                        "<p>" + xwpfWordExtractor.getText() + "</p>\n" +
                                        "</div>");
                                Paragraph p6 = new Paragraph(xwpfWordExtractor.getText(), COURIER_SMALL_FOOTER);
                                p6.setAlignment(Element.ALIGN_LEFT);
                                document.add(p6);
                                leaveEmptyLine(p6, 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("error in Doc Read");
                            }
                        }
                    }
                    if (version.getEditortext() != null && !version.getEditortext().isEmpty()) {
                        if (version != null) {
                            if (version.getEditortext() != null && version.getBitstream() == null) {
                                isTextEditorFlow=true;
                                System.out.println("editor::::" + version.getEditortext());
                                sb.append("<div>" + version.getEditortext() + "</div>");
                                Paragraph p6 = new Paragraph(PdfUtils.htmlToText(version.getEditortext()), COURIER_SMALL_FOOTER);
                                p6.setAlignment(Element.ALIGN_LEFT);
                                document.add(p6);
                                leaveEmptyLine(p6, 1);
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
                sb.append("<div style=\"width:100%;    text-align: right;\">\n" +
                        "<span >" + workflowProcess.getWorkflowProcessNote().getSubmitter().getFullName() + "<br>" + Designation1);

                Paragraph p6 = new Paragraph(workflowProcess.getWorkflowProcessNote().getSubmitter().getFullName() + "\n" + Designation1, COURIER_SMALL_FOOTER);
                p6.setAlignment(Element.ALIGN_RIGHT);
                document.add(p6);
                aa.add(DateFormate(workflowProcess.getWorkflowProcessNote().getInitDate()));
                sb.append("<br>" + DateFormate(workflowProcess.getWorkflowProcessNote().getInitDate()) + "</span>");

            }
            Paragraph p7 = new Paragraph(DateFormate(workflowProcess.getWorkflowProcessNote().getInitDate()), COURIER_SMALL_FOOTER);
            p7.setAlignment(Element.ALIGN_RIGHT);
            document.add(p7);
            map.put("creator", aa);
        }
        Map<String, String> referencedocumentmap = new HashMap<String, String>();
        sb.append("</div> <div style=\"width:100%;    text-align: left;\"> <p><b>Reference Documents</b></p> ");
        Paragraph p9 = new Paragraph("Reference Documents", COURIER_SMALL_FOOTER);
        p9.setAlignment(Element.ALIGN_LEFT);
        document.add(p9);
        //Reference Documents dinamix
        for (WorkflowProcessReferenceDoc workflowProcessReferenceDoc : workflowProcess.getWorkflowProcessReferenceDocs()) {
            if (workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue() != null && workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue().equals("Reference Document")) {
                // InputStream out = null;
                if (workflowProcessReferenceDoc.getBitstream() != null) {
                    //   out = bitstreamService.retrieve(context, workflowProcessReferenceDoc.getBitstream());
                    //  if (out!=null) {
                    Phrase phrase = new Phrase();
                    Font anchorFont = new Font(Font.FontFamily.UNDEFINED, 11);
                    anchorFont.setColor(BaseColor.BLUE);
                    anchorFont.setStyle(Font.FontStyle.UNDERLINE.getValue());
                    String baseurl = configurationService.getProperty("dspace.server.url");
                    Anchor anchor = new Anchor(workflowProcessReferenceDoc.getBitstream().getName(), anchorFont);
                    anchor.setReference(baseurl + "/api/core/bitstreams/" + workflowProcessReferenceDoc.getBitstream().getID() + "/content");
                    phrase.add(anchor);
                    document.add(phrase);
                    referencedocumentmap.put("name", workflowProcessReferenceDoc.getBitstream().getName());
                    referencedocumentmap.put("link", baseurl + "/api/core/bitstreams/" + workflowProcessReferenceDoc.getBitstream().getID() + "/content");

                    sb.append("<span><a href=" + baseurl + "/api/core/bitstreams/" + workflowProcessReferenceDoc.getBitstream().getID() + "/content>" + workflowProcessReferenceDoc.getBitstream().getName() + "</a> <br>");
                    // }
                }
            }
            sb.append("</span></div>");
        }
        map.put("Reference Documents", referencedocumentmap);
        sb.append("</div> <div style=\"width:100%;    text-align: left;\"> <p><b>Reference Noting</b></p> ");
        Paragraph p10 = new Paragraph("Reference Noting", COURIER_SMALL_FOOTER);
        p10.setAlignment(Element.ALIGN_LEFT);
        document.add(p10);
        //Reference Noting Dynemic
        Map<String, String> referencenottingmap = new HashMap<String, String>();
        for (WorkflowProcessReferenceDoc workflowProcessReferenceDoc : workflowProcess.getWorkflowProcessReferenceDocs()) {
            if (workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue() != null && workflowProcessReferenceDoc.getDrafttype().getPrimaryvalue().equals("Reference Noting")) {
                if (workflowProcessReferenceDoc.getBitstream() != null) {
                    Phrase phrase = new Phrase();
                    Font anchorFont = new Font(Font.FontFamily.UNDEFINED, 11);
                    anchorFont.setColor(BaseColor.BLUE);
                    anchorFont.setStyle(Font.FontStyle.UNDERLINE.getValue());
                    Anchor anchor = new Anchor(workflowProcessReferenceDoc.getBitstream().getName(), anchorFont);
                    String baseurl = configurationService.getProperty("dspace.server.url");
                    anchor.setReference(baseurl + "/api/core/bitstreams/" + workflowProcessReferenceDoc.getBitstream().getID() + "/content");
                    phrase.add(anchor);
                    document.add(phrase);
                    leaveEmptyLine(new Paragraph(" "), 1);
                    sb.append("<span><a href=" + baseurl + "/api/core/bitstreams/" + workflowProcessReferenceDoc.getBitstream().getID() + "/content>" + workflowProcessReferenceDoc.getBitstream().getName() + "</a> <br>");
                    referencenottingmap.put("name", workflowProcessReferenceDoc.getBitstream().getName());
                    referencenottingmap.put("link", baseurl + "/api/core/bitstreams/" + workflowProcessReferenceDoc.getBitstream().getID() + "/content");

                }

            }
            sb.append("</span>");

        }
        map.put("Reference Noting", referencenottingmap);
        leaveEmptyLine(new Paragraph(""), 1);
        StringBuilder sb1 = new StringBuilder("Note Creator : ");
        if (workflowProcess.getWorkflowProcessNote() != null && workflowProcess.getWorkflowProcessNote().getSubmitter() != null) {
            EPerson creator = workflowProcess.getWorkflowProcessNote().getSubmitter();
            if (creator.getFullName() != null) {
                sb1.append(creator.getFullName());
            }
            String Designation1 = workFlowProcessMasterValueService.find(context, creator.getDesignation().getID()).getPrimaryvalue();
            if (Designation1 != null) {
                sb1.append(" | " + Designation1);
            }
            Paragraph p11 = new Paragraph(sb1.toString(), COURIER_SMALL_FOOTER);
            p11.setAlignment(Element.ALIGN_LEFT);
            document.add(p11);
            sb.append("<p>" + sb1.toString() + "</p>");
        }
        sb.append("</div>");


        leaveEmptyLine(new Paragraph(""), 3);
        // pending file
        List<WorkFlowProcessComment> comments = workFlowProcessCommentService.getComments(context, workflowProcess.getID());
        map.put("comment", comments);
        // comment
        Paragraph p13 = new Paragraph();
        p13.setAlignment(Element.ALIGN_LEFT);
        p13.add(new Paragraph("Comment [" + comments.size() + "]", COURIER_SMALL_FOOTER));
        sb.append("<br><h5> Comments [" + comments.size() + "]</h5>");
        document.add(p13);
        int i = 1;
        for (WorkFlowProcessComment comment : comments) {
            sb.append("<div style=\"width:100% ;text-align: left;\">");
            //coment count
            Paragraph commentcount = new Paragraph();
            commentcount.setAlignment(Element.ALIGN_LEFT);
            commentcount.add(new Paragraph("Comment #" + i, COURIER_SMALL_FOOTER));
            sb.append("<p><b>Comment # " + i + "</b></p>");
            document.add(commentcount);

            //comment text
            Paragraph commenttext = new Paragraph();
            commenttext.setAlignment(Element.ALIGN_LEFT);
            if (comment.getComment() != null) {
                commenttext.add(new Paragraph(comment.getComment(), COURIER_SMALL_FOOTER));
                sb.append("<span>" + comment.getComment() + "</span><br>");
            }
            document.add(commenttext);
            //Comment by
            if (comment.getComment() != null) {
                Paragraph commentby = new Paragraph("                                                                                                                                            Comment By", COURIER_SMALL_FOOTER);
                commentby.setAlignment(Element.ALIGN_RIGHT);
                document.add(commentby);
                sb.append("<span style=\"    float: right;\"><b>Comment By</b></span><br>");
            }
            //MAnager
            if (comment.getSubmitter() != null) {
                if (comment.getSubmitter().getFullName() != null) {
                    Paragraph Manager = new Paragraph("                                                                                                                                           " + comment.getSubmitter().getFullName(), COURIER_SMALL_FOOTER);
                    Manager.setAlignment(Element.ALIGN_RIGHT);
                    document.add(Manager);
                    // sb.append("<br>" + comment.getSubmitter().getFullName());
                    sb.append("<span style=\"    float: right;\">" + comment.getSubmitter().getFullName() + "</span><br>");

                }
                if (comment.getSubmitter().getDesignation() != null) {
                    String Designation = workFlowProcessMasterValueService.find(context, comment.getSubmitter().getDesignation().getID()).getPrimaryvalue();
                    Paragraph Manager = new Paragraph("                                                                                                                                           " + Designation, COURIER_SMALL_FOOTER);
                    Manager.setAlignment(Element.ALIGN_RIGHT);
                    document.add(Manager);
                    // sb.append("<br>" + Designation);
                    sb.append("<span style=\"    float: right;\">" + Designation + "</span><br>");

                }
            }
            //
            if (comment.getWorkFlowProcessHistory() != null && comment.getWorkFlowProcessHistory().toString() != null) {
                Paragraph date = new Paragraph(DateFormate(comment.getWorkFlowProcessHistory().getActionDate()), COURIER_SMALL_FOOTER);
                date.setAlignment(Element.ALIGN_RIGHT);
                document.add(date);
                //  sb.append("<br>" + DateFormate(comment.getWorkFlowProcessHistory().getActionDate()));
                sb.append("<span style=\"    float: right;\">" + DateFormate(comment.getWorkFlowProcessHistory().getActionDate()) + "</span><br>");

            }
            sb.append("</div> <br>\n" +
                    "<br>\n" +
                    "<br>\n" +
                    "<br>");

            i++;
        }
        sb.append("</body></html>");
        if (isTextEditorFlow) {
            System.out.println("::::::::::IN isTextEditorFlow :::::::::");
            FileOutputStream files = new FileOutputStream(new File(tempFile1html.getAbsolutePath()));
            System.out.println("HTML:::"+sb.toString());
            int result=PdfUtils.HtmlconvertToPdf(sb.toString(), files);
            if(result==0){
                document.close();
            }
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

    private static void leaveEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private static String DateFormate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        return formatter.format(date);
    }
}
