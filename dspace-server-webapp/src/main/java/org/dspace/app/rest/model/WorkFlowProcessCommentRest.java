/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dspace.app.rest.model.helper.MyDateConverter;
import org.dspace.content.WorkFlowProcessHistory;
import org.dspace.content.WorkFlowProcessMasterValue;
import org.dspace.content.WorkflowProcess;
import org.dspace.content.WorkflowProcessReferenceDoc;
import org.dspace.eperson.EPerson;

import javax.persistence.*;
import java.util.Date;

public class WorkFlowProcessCommentRest extends  DSpaceObjectRest{
    public static final String NAME = "workflowprocesscomment";
    public static final String PLURAL_NAME = "workflowprocesscomments";
    public static final String CATEGORY = RestAddressableModel.WORKFLOWPROCESSCOMMENT;
    public static final String GROUPS = "groups";
    private Integer legacyId;
    private String comment;
    private WorkflowProcessReferenceDocRest workflowProcessReferenceDocRest;
    private WorkFlowProcessHistoryRest workFlowProcessHistoryRest;

    private EPersonRest submitterRest = null;

    private WorkFlowProcessRest workflowProcessRest;
    public Integer getLegacyId() {
        return legacyId;
    }
    public void setLegacyId(Integer legacyId) {
        this.legacyId = legacyId;
    }
    @Override
    public String getCategory() {
        return CATEGORY;
    }
    @Override
    public String getType() {
        return NAME;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public WorkflowProcessReferenceDocRest getWorkflowProcessReferenceDocRest() {
        return workflowProcessReferenceDocRest;
    }
    public void setWorkflowProcessReferenceDocRest(WorkflowProcessReferenceDocRest workflowProcessReferenceDocRest) {
        this.workflowProcessReferenceDocRest = workflowProcessReferenceDocRest;
    }
    public WorkFlowProcessHistoryRest getWorkFlowProcessHistoryRest() {
        return workFlowProcessHistoryRest;
    }
    public void setWorkFlowProcessHistoryRest(WorkFlowProcessHistoryRest workFlowProcessHistoryRest) {
        this.workFlowProcessHistoryRest = workFlowProcessHistoryRest;
    }

    public EPersonRest getSubmitterRest() {
        return submitterRest;
    }

    public void setSubmitterRest(EPersonRest submitterRest) {
        this.submitterRest = submitterRest;
    }

    public WorkFlowProcessRest getWorkflowProcessRest() {
        return workflowProcessRest;
    }

    public void setWorkflowProcessRest(WorkFlowProcessRest workflowProcessRest) {
        this.workflowProcessRest = workflowProcessRest;
    }
}
