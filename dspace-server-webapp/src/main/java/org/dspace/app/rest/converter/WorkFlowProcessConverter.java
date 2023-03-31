/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import com.google.gson.Gson;
import org.dspace.app.rest.model.EPersonRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.model.WorkFlowProcessRest;
import org.dspace.app.rest.model.WorkflowProcessEpersonRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.*;
import org.dspace.content.enums.Dispatch;
import org.dspace.content.enums.Priority;
import org.dspace.content.enums.WorkFlowProcessReferenceDocType;
import org.dspace.content.service.BitstreamService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
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
    EPersonConverter ePersonConverter;
    @Autowired
    BitstreamService bitstreamService;
    @Autowired
    WorkflowProcessSenderDiaryConverter workflowProcessSenderDiaryConverter;
    @Autowired
    WorkFlowProcessInwardDetailsConverter workFlowProcessInwardDetailsConverter;
    @Autowired
    WorkFlowProcessMasterValueConverter workFlowProcessMasterValueConverter;
    @Autowired
    WorkflowProcessReferenceDocConverter workflowProcessReferenceDocConverter;
    @Autowired
    WorkFlowProcessEpersonConverter workFlowProcessEpersonConverter;

    @Override
    public WorkFlowProcessRest convert(WorkflowProcess obj, Projection projection) {
        WorkFlowProcessRest workFlowProcessRest = super.convert(obj, projection);
        if (obj.getWorkflowProcessSenderDiary() != null) {
            workFlowProcessRest.setWorkflowProcessSenderDiaryRest(workflowProcessSenderDiaryConverter.convert(obj.getWorkflowProcessSenderDiary(), projection));
        }
        if (obj.getWorkFlowProcessInwardDetails() != null) {
            workFlowProcessRest.setWorkFlowProcessInwardDetailsRest(workFlowProcessInwardDetailsConverter.convert(obj.getWorkFlowProcessInwardDetails(), projection));
        }
        if (obj.getDispatchmode() != null) {
            workFlowProcessRest.setDispatchModeRest(workFlowProcessMasterValueConverter.convert(obj.getDispatchmode(), projection));
            System.out.println("Dispath mode::" + workFlowProcessRest.getDispatchModeRest().getPrimaryvalue());
        }
        if (obj.getEligibleForFiling() != null) {
            workFlowProcessRest.setEligibleForFilingRest(workFlowProcessMasterValueConverter.convert(obj.getEligibleForFiling(), projection));
        }
        if (obj.getEligibleForFiling() != null) {
            workFlowProcessRest.setItemRest(itemConverter.convertNameOnly(obj.getItem(), projection));
        }
        if (obj.getWorkflowProcessReferenceDocs() != null) {
            workFlowProcessRest.setWorkflowProcessReferenceDocRests(obj.getWorkflowProcessReferenceDocs().stream().map(d -> {
                return workflowProcessReferenceDocConverter.convert(d, projection);
            }).collect(Collectors.toList()));
        }
        workFlowProcessRest.setSubject(obj.getSubject());
        if (obj.getWorkflowProcessEpeople() != null) {
            workFlowProcessRest.setWorkflowProcessEpersonRests(obj.getWorkflowProcessEpeople().stream().map(we -> {
                return workFlowProcessEpersonConverter.convert(we, projection);
            }).collect(Collectors.toList()));
        }
        workFlowProcessRest.setInitDate(obj.getInitDate());
        if (obj.getPriority() != null) {
            workFlowProcessRest.setPriority(obj.getPriority().getPriorityName());
        }
        if (obj.getDispatchmode() != null)
           workFlowProcessRest.setDispatchModeRest(workFlowProcessMasterValueConverter.convert(obj.getDispatchmode(), projection));

        return workFlowProcessRest;
    }

    public WorkflowProcess convert(WorkFlowProcessRest obj, Context context) throws Exception {
        WorkflowProcess workflowProcess = new WorkflowProcess();
        workflowProcess.setWorkflowProcessSenderDiary(workflowProcessSenderDiaryConverter.convert(obj.getWorkflowProcessSenderDiaryRest()));
        workflowProcess.setWorkFlowProcessInwardDetails(workFlowProcessInwardDetailsConverter.convert(obj.getWorkFlowProcessInwardDetailsRest()));
        workflowProcess.setDispatchmode(workFlowProcessMasterValueConverter.convert(context, obj.getDispatchModeRest()));
        workflowProcess.setEligibleForFiling(workFlowProcessMasterValueConverter.convert(context, obj.getEligibleForFilingRest()));
        workflowProcess.setItem(itemConverter.convert(context, obj.getItemRest()));
        workflowProcess.setWorkflowProcessReferenceDocs(obj.getWorkflowProcessReferenceDocRests().stream().map(d -> {
            try {
                WorkflowProcessReferenceDoc workflowProcessReferenceDoc=workflowProcessReferenceDocConverter.convert(context, d);
                workflowProcessReferenceDoc.setWorkflowProcess(workflowProcess);
                return workflowProcessReferenceDoc;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        workflowProcess.setSubject(obj.getSubject());
        // set submitor...
        if(context.getCurrentUser() != null){
            WorkflowProcessEpersonRest workflowProcessEpersonSubmitor=new WorkflowProcessEpersonRest();
            EPersonRest ePersonRest=new EPersonRest();
            ePersonRest.setUuid(context.getCurrentUser().getID().toString());
            workflowProcessEpersonSubmitor.setIndex(0);
            workflowProcessEpersonSubmitor.setePersonRest(ePersonRest);
            obj.getWorkflowProcessEpersonRests().add(workflowProcessEpersonSubmitor);
        }
        workflowProcess.setWorkflowProcessEpeople(obj.getWorkflowProcessEpersonRests().stream().map(we -> {
            try {
                WorkflowProcessEperson workflowProcessEperson=workFlowProcessEpersonConverter.convert(context, we);
                workflowProcessEperson.setWorkflowProcess(workflowProcess);
                return workflowProcessEperson;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        workflowProcess.setInitDate(obj.getInitDate());
        if (obj.getPriority() != null) {
            workflowProcess.setPriority(Priority.valueOf(obj.getPriority()));
        }
        if (obj.getDispatchModeRest() != null) {
            System.out.println("obj.getWorkflowProcessReferenceDocRests()::" + obj.getDispatchModeRest().getId());
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
