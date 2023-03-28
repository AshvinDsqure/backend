/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.EPersonRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.model.WorkFlowProcessRest;
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
        return super.convert(obj, projection);
    }
    public WorkflowProcess convert(WorkFlowProcessRest obj, Context context) throws  Exception {
        WorkflowProcess workflowProcess=new WorkflowProcess();
        workflowProcess.setWorkflowProcessSenderDiary(workflowProcessSenderDiaryConverter.convert(obj.getWorkflowProcessSenderDiaryRest()));
        workflowProcess.setWorkFlowProcessInwardDetails(workFlowProcessInwardDetailsConverter.convert(obj.getWorkFlowProcessInwardDetailsRest()));
        workflowProcess.setDispatchMode(workFlowProcessMasterValueConverter.convert(obj.getDispatchModeRest()));
        workflowProcess.setEligibleForFiling(workFlowProcessMasterValueConverter.convert(obj.getEligibleForFilingRest()));
        workflowProcess.setItem(itemConverter.convert(obj.getItemRest()));
        workflowProcess.setWorkflowProcessReferenceDocs(obj.getWorkflowProcessReferenceDocRests().stream().map(d->{
            return  workflowProcessReferenceDocConverter.convert(d);
        }).collect(Collectors.toList()));
        workflowProcess.setSubject(obj.getSubject());
        workflowProcess.setDepartment(workFlowProcessMasterValueConverter.convert(obj.getDepartmentRest()));
        workflowProcess.setOffice(workFlowProcessMasterValueConverter.convert(obj.getOfficeRest()));
        workflowProcess.setWorkflowProcessEpeople( obj.getWorkflowProcessEpersonRests().stream().map(we->{
           return  workFlowProcessEpersonConverter.convert(we);
        }).collect(Collectors.toList()));
        workflowProcess.setInitDate(obj.getInitDate());
        if(obj.getPriority()!= null) {
            workflowProcess.setPriority(Priority.valueOf(obj.getPriority()));
        }
        if(obj.getDispatchModeRest() != null)
        workflowProcess.setDispatchmode(Dispatch.valueOf(obj.getDispatchModeRest().getPrimaryvalue()));
        System.out.println("obj.getWorkflowProcessReferenceDocRests()::"+obj.getWorkflowProcessReferenceDocRests().size());
        workflowProcess.setSubmitter( workFlowProcessEpersonConverter.convert(obj.getSubmitter()));
        return  workflowProcess;

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
