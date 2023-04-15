/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dspace.content.WorkFlowProcessHistory;
import org.dspace.content.WorkFlowProcessMasterValue;
import org.dspace.eperson.EPerson;

import java.util.Date;
@LinksRest(links = {
        @LinkRest(
                name = WorkflowItemRest.STEP,
                method = "getStep"
        )
})
public class WorkFlowProcessHistoryRest extends  DSpaceObjectRest{
    public static final String NAME = "workflowprocesshistorie";
    public static final String PLURAL_NAME = "workflowprocesshistories";
    public static final String CATEGORY = RestAddressableModel.WORKFLOWPROCESSHISTORY;

    public static final String GROUPS = "groups";

    private Integer legacyId;

    private WorkflowProcessEpersonRest workflowProcessEpersonRest;
    private WorkFlowProcessMasterValueRest action;

    private String comment;

    private Date actionDate;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public WorkFlowProcessMasterValueRest getAction() {
        return action;
    }

    public void setAction(WorkFlowProcessMasterValueRest action) {
        this.action = action;
    }

    public WorkflowProcessEpersonRest getWorkflowProcessEpersonRest() {
        return workflowProcessEpersonRest;
    }

    public void setWorkflowProcessEpersonRest(WorkflowProcessEpersonRest workflowProcessEpersonRest) {
        this.workflowProcessEpersonRest = workflowProcessEpersonRest;
    }

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


    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }
}
