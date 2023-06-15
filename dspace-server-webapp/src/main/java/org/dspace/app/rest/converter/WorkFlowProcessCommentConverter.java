/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.WorkFlowProcessCommentRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.WorkFlowProcessComment;
import org.dspace.content.WorkFlowProcessMasterValue;
import org.dspace.content.service.WorkFlowProcessHistoryService;
import org.dspace.core.Context;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.UUID;

@Component
public class WorkFlowProcessCommentConverter extends DSpaceObjectConverter<WorkFlowProcessComment, WorkFlowProcessCommentRest> {
    @Autowired
    WorkFlowProcessHistoryConverter workFlowProcessHistoryConverter;

    @Autowired
    EPersonConverter ePersonConverter;

    @Autowired
    WorkFlowProcessHistoryService workFlowProcessHistoryService;
    @Autowired
    WorkflowProcessReferenceDocConverter workflowProcessReferenceDocConverter;

    @Autowired
    WorkFlowProcessConverter workFlowProcessConverter;


    @Override
    public Class<WorkFlowProcessComment> getModelClass() {
        return WorkFlowProcessComment.class;
    }
    @Override
    protected WorkFlowProcessCommentRest newInstance() {
        return new WorkFlowProcessCommentRest();
    }
    @Override
    public WorkFlowProcessCommentRest convert(WorkFlowProcessComment obj, Projection projection) {
        WorkFlowProcessCommentRest rest = new WorkFlowProcessCommentRest();
        if (obj.getWorkFlowProcessHistory() != null) {
            rest.setWorkFlowProcessHistoryRest(workFlowProcessHistoryConverter.convert(obj.getWorkFlowProcessHistory(), projection));
        }
        if (obj.getWorkflowProcessReferenceDoc() != null) {
            rest.setWorkflowProcessReferenceDocRest(workflowProcessReferenceDocConverter.convert(obj.getWorkflowProcessReferenceDoc(), projection));
        }
        if (obj.getSubmitter() != null) {
            rest.setSubmitterRest(ePersonConverter.convert(obj.getSubmitter(), projection));
        }
        if(obj.getWorkFlowProcess()!=null){
            rest.setWorkflowProcessRest(workFlowProcessConverter.convert(obj.getWorkFlowProcess(),projection));
        }
        if(obj.getComment()!=null){
            rest.setComment(obj.getComment());
        }

        rest.setUuid(obj.getID().toString());
        return rest;
    }
    public WorkFlowProcessComment convert(Context context, WorkFlowProcessCommentRest rest) throws Exception {
        WorkFlowProcessComment obj = new WorkFlowProcessComment();
        obj.setComment(rest.getComment());
        if (rest.getWorkFlowProcessHistoryRest() != null) {
            obj.setWorkFlowProcessHistory(workFlowProcessHistoryService.find(context,UUID.fromString(rest.getWorkFlowProcessHistoryRest().getId())));
        }
        if (rest.getWorkflowProcessReferenceDocRest() != null) {
            obj.setWorkflowProcessReferenceDoc(workflowProcessReferenceDocConverter.convertByService(context,rest.getWorkflowProcessReferenceDocRest()));
        }
        if (rest.getSubmitterRest() != null) {
            obj.setSubmitter(ePersonConverter.convert(context, rest.getSubmitterRest()));
        }
        if(rest.getWorkflowProcessRest()!=null){
            obj.setWorkFlowProcess(workFlowProcessConverter.convert(rest.getWorkflowProcessRest(),context));
        }
        return obj;
    }
}
