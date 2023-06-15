/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import com.google.gson.Gson;
import com.itextpdf.text.log.SysoCounter;
import org.dspace.app.rest.enums.WorkFlowType;
import org.dspace.app.rest.enums.WorkFlowUserType;
import org.dspace.app.rest.model.*;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.*;
import org.dspace.content.enums.Dispatch;
import org.dspace.content.enums.Priority;
import org.dspace.content.enums.WorkFlowProcessReferenceDocType;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.WorkFlowProcessDraftDetailsService;
import org.dspace.core.Context;
import org.dspace.eperson.service.GroupService;
import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This is the converter from/to the EPerson in the DSpace API data model and the
 * REST data model
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@Component
public class WorkFlowProcessConverter extends DSpaceObjectConverter<WorkflowProcess, WorkFlowProcessRest> {
    @Autowired
    ItemConverter itemConverter;

    @Autowired
    GroupService groupService;
    @Autowired
    EPersonConverter ePersonConverter;
    @Autowired
    BitstreamService bitstreamService;
    @Autowired
    WorkflowProcessSenderDiaryConverter workflowProcessSenderDiaryConverter;
    @Autowired
    WorkFlowProcessInwardDetailsConverter workFlowProcessInwardDetailsConverter;
    @Autowired
    WorkFlowProcessOutwardDetailsConverter workFlowProcessOutwardDetailsConverter;
    @Autowired
    WorkFlowProcessMasterValueConverter workFlowProcessMasterValueConverter;

    @Autowired
    WorkFlowProcessDraftDetailsService workFlowProcessDraftDetailsService;
    @Autowired
    WorkflowProcessReferenceDocConverter workflowProcessReferenceDocConverter;
    @Autowired
    WorkFlowProcessEpersonConverter workFlowProcessEpersonConverter;
    @Autowired
    WorkFlowProcessDraftDetailsConverter workFlowProcessDraftDetailsConverter;

    @Autowired
    WorkflowProcessNoteConverter workflowProcessNoteConverter;

    @Override
    public WorkFlowProcessRest convert(WorkflowProcess obj, Projection projection) {
        WorkFlowProcessRest workFlowProcessRest = super.convert(obj, projection);
        if (obj.getWorkflowProcessSenderDiary() != null) {
            workFlowProcessRest.setWorkflowProcessSenderDiaryRest(workflowProcessSenderDiaryConverter.convert(obj.getWorkflowProcessSenderDiary(), projection));
        }
        if (obj.getWorkFlowProcessInwardDetails() != null) {
            workFlowProcessRest.setWorkFlowProcessInwardDetailsRest(workFlowProcessInwardDetailsConverter.convert(obj.getWorkFlowProcessInwardDetails(), projection));
        }
        if (obj.getWorkFlowProcessOutwardDetails() != null) {
            workFlowProcessRest.setWorkFlowProcessOutwardDetailsRest(workFlowProcessOutwardDetailsConverter.convert(obj.getWorkFlowProcessOutwardDetails(), projection));
        }
        if (obj.getWorkFlowProcessDraftDetails() != null) {
            workFlowProcessRest.setWorkFlowProcessDraftDetailsRest(workFlowProcessDraftDetailsConverter.convert(obj.getWorkFlowProcessDraftDetails(), projection));
        }
        if (obj.getWorkflowProcessNote() != null) {
            workFlowProcessRest.setWorkflowProcessNoteRest(workflowProcessNoteConverter.convert(obj.getWorkflowProcessNote(), projection));
        }
        if (obj.getDispatchmode() != null) {
            workFlowProcessRest.setDispatchModeRest(workFlowProcessMasterValueConverter.convert(obj.getDispatchmode(), projection));
        }
        if (obj.getEligibleForFiling() != null) {
            workFlowProcessRest.setEligibleForFilingRest(workFlowProcessMasterValueConverter.convert(obj.getEligibleForFiling(), projection));
        }
        if (obj.getWorkflowType() != null) {
            workFlowProcessRest.setWorkflowType(workFlowProcessMasterValueConverter.convert(obj.getWorkflowType(), projection));
        }
        if (obj.getWorkflowStatus() != null) {
            workFlowProcessRest.setWorkflowStatus(workFlowProcessMasterValueConverter.convert(obj.getWorkflowStatus(), projection));
        }

        if (obj.getItem() != null) {
            workFlowProcessRest.setItemRest(itemConverter.convertNameOnly(obj.getItem(), projection));
        }
        if (obj.getWorkflowProcessReferenceDocs() != null) {
            workFlowProcessRest.setWorkflowProcessReferenceDocRests(obj.getWorkflowProcessReferenceDocs().stream().map(d -> {
                WorkflowProcessReferenceDocRest rest= workflowProcessReferenceDocConverter.convert(d, projection);
                if(d.getWorkflowprocessnote()!=null){
                    rest.setWorkflowProcessNoteRest(workflowProcessNoteConverter.convert(d.getWorkflowprocessnote(),projection));
                }
                return  rest;
            }).collect(Collectors.toList()));
        }
        workFlowProcessRest.setSubject(obj.getSubject());
        if (obj.getWorkflowProcessEpeople() != null) {

            Comparator<WorkflowProcessEpersonRest> comparator = (s1, s2) -> s1.getIndex().compareTo(s2.getIndex());
            List<WorkflowProcessEpersonRest> list = obj.getWorkflowProcessEpeople().stream().map(we -> {
                return workFlowProcessEpersonConverter.convert(we, projection);
            }).collect(Collectors.toList());
            list.sort(comparator);
            workFlowProcessRest.setWorkflowProcessEpersonRests(list);
        }
        workFlowProcessRest.setInitDate(obj.getInitDate());
        if (obj.getPriority() != null) {
            workFlowProcessRest.setPriorityRest(workFlowProcessMasterValueConverter.convert(obj.getPriority(), projection));
        }
        if (obj.getDispatchmode() != null)
            workFlowProcessRest.setDispatchModeRest(workFlowProcessMasterValueConverter.convert(obj.getDispatchmode(), projection));
        Optional<WorkflowProcessEperson> ownerRest = obj.getWorkflowProcessEpeople().stream().filter(w -> w.getOwner() != null).filter(w -> w.getOwner()).findFirst();
        if (ownerRest.isPresent()) {
            workFlowProcessRest.setOwner(workFlowProcessEpersonConverter.convert(ownerRest.get(), projection));
        }
        Optional<WorkflowProcessEperson> senderRest = obj.getWorkflowProcessEpeople().stream().filter(wn -> wn.getSender() != null).filter(w -> w.getSender()).findFirst();
        if (senderRest.isPresent()) {
            workFlowProcessRest.setSender(workFlowProcessEpersonConverter.convert(senderRest.get(), projection));
        }
        workFlowProcessRest.setUuid(obj.getID().toString());
        return workFlowProcessRest;
    }

    public WorkflowProcess convert(WorkFlowProcessRest obj, Context context) throws Exception {
        WorkflowProcess workflowProcess = new WorkflowProcess();
        if (obj.getWorkflowProcessSenderDiaryRest() != null) {
            workflowProcess.setWorkflowProcessSenderDiary(workflowProcessSenderDiaryConverter.convert(obj.getWorkflowProcessSenderDiaryRest()));
        }
        if (obj.getWorkFlowProcessInwardDetailsRest() != null) {
            workflowProcess.setWorkFlowProcessInwardDetails(workFlowProcessInwardDetailsConverter.convert(obj.getWorkFlowProcessInwardDetailsRest()));
        }
        if (obj.getWorkFlowProcessDraftDetailsRest() != null) {
            WorkFlowProcessDraftDetails draftDetails = workFlowProcessDraftDetailsService.create(context, workFlowProcessDraftDetailsConverter.convert(context, obj.getWorkFlowProcessDraftDetailsRest()));
            workflowProcess.setWorkFlowProcessDraftDetails(draftDetails);
        }
        if (obj.getWorkflowProcessNoteRest() != null) {
            workflowProcess.setWorkflowProcessNote(workflowProcessNoteConverter.convert(context, obj.getWorkflowProcessNoteRest()));
        }
        if (obj.getWorkFlowProcessOutwardDetailsRest() != null) {
            workflowProcess.setWorkFlowProcessOutwardDetails(workFlowProcessOutwardDetailsConverter.convert(context, obj.getWorkFlowProcessOutwardDetailsRest()));
        }
        if (obj.getDispatchModeRest() != null) {
            workflowProcess.setDispatchmode(workFlowProcessMasterValueConverter.convert(context, obj.getDispatchModeRest()));
        }
        if (obj.getEligibleForFilingRest() != null) {
            workflowProcess.setEligibleForFiling(workFlowProcessMasterValueConverter.convert(context, obj.getEligibleForFilingRest()));
        }
        if (obj.getItemRest() != null) {
            workflowProcess.setItem(itemConverter.convert(obj.getItemRest(), context));
        }
        WorkFlowType workFlowType = WorkFlowType.valueOf(obj.getWorkflowTypeStr());
        if (workFlowType != null) {
            Optional<WorkFlowProcessMasterValue> workFlowProcessMasterValue = workFlowType.getUserTypeFromMasterValue(context);
            if (workFlowProcessMasterValue.isPresent()) {
                workflowProcess.setWorkflowType(workFlowProcessMasterValue.get());
            }
        }
        workflowProcess.setSubject(obj.getSubject());
        // set submitor...
        AtomicInteger index = new AtomicInteger(0);
        workflowProcess.setWorkflowProcessEpeople(obj.getWorkflowProcessEpersonRests().stream().map(we -> {
            try {
                if (we.getUserType() == null) {
                    we.setIndex(index.incrementAndGet());
                }
                WorkflowProcessEperson workflowProcessEperson = workFlowProcessEpersonConverter.convert(context, we);
                Optional<WorkFlowProcessMasterValue> workFlowUserTypOptional = WorkFlowUserType.NORMAL.getUserTypeFromMasterValue(context);
                if (we.getUserType() == null) {
                    workflowProcessEperson.setUsertype(workFlowUserTypOptional.get());
                }
                workflowProcessEperson.setOwner(false);
                workflowProcessEperson.setSender(false);
                workflowProcessEperson.setWorkflowProcess(workflowProcess);

                return workflowProcessEperson;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        workflowProcess.setInitDate(obj.getInitDate());
        if (obj.getPriorityRest() != null) {
            workflowProcess.setPriority(workFlowProcessMasterValueConverter.convert(context, obj.getPriorityRest()));
        }
        if (obj.getDispatchModeRest() != null) {
            workflowProcess.setDispatchmode(workFlowProcessMasterValueConverter.convert(context, obj.getDispatchModeRest()));
        }
        return workflowProcess;
    }

    @Override
    protected WorkFlowProcessRest newInstance() {
        return new WorkFlowProcessRest();
    }

    @Override
    public Class<WorkflowProcess> getModelClass() {
        return WorkflowProcess.class;
    }
}
