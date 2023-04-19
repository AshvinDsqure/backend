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
import lombok.Data;
import org.dspace.app.rest.model.helper.MyDateConverter;
import org.dspace.app.rest.validation.WorkflowProcessMasterValueValid;
import org.dspace.app.rest.validation.WorkflowProcessValid;
import org.dspace.content.WorkflowProcessEperson;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Item REST Resource
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@LinksRest(links = {

})
public class WorkFlowProcessRest extends DSpaceObjectRest {
    public static final String NAME = "workflowprocesse";
    public static final String PLURAL_NAME = "workflowprocess";
    public static final String CATEGORY = RestAddressableModel.WORKFLOWPROCESS;
    public static final String  CATEGORY_INWARD= "inward";
    public static final String  CATEGORY_OUTWARE= "outward";
    @JsonProperty
    private WorkFlowProcessInwardDetailsRest workFlowProcessInwardDetailsRest;
    @JsonProperty
    private WorkFlowProcessOutwardDetailsRest workFlowProcessOutwardDetailsRest;
    @JsonProperty
    @Valid
    private  WorkflowProcessSenderDiaryRest workflowProcessSenderDiaryRest;
    @JsonProperty
    @Valid
    @NotNull
    private WorkFlowProcessMasterValueRest dispatchModeRest = null;
    @JsonProperty
    private WorkFlowProcessMasterValueRest workflowStatus = null;
    @JsonProperty
    private WorkFlowProcessMasterValueRest workflowType = null;
    @JsonProperty
    private WorkFlowProcessMasterValueRest eligibleForFilingRest = null;
    @JsonProperty
    private WorkflowProcessEpersonRest owner = null;
    @JsonProperty
    private WorkflowProcessEpersonRest sender = null;
    @JsonProperty
    private ItemRest itemRest;
    @JsonProperty
    @NotBlank
    private String Subject;
    @JsonProperty
    private String workflowTypeStr;
    @JsonProperty
    private  Boolean isDraft=false;
    @JsonProperty
    private  String comment;
    @JsonProperty
    List<WorkflowProcessReferenceDocRest> workflowProcessReferenceDocRests=new ArrayList<>();

    @JsonProperty
    @NotEmpty(message = "Input WorkflowProcessEpersonRest list cannot be empty.")
    private List<WorkflowProcessEpersonRest> workflowProcessEpersonRests=new ArrayList<>();
    @JsonProperty
    @JsonDeserialize(converter = MyDateConverter.class)
    private Date InitDate = new Date();


    @JsonProperty
    private String priority;



    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String entityType = null;
    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getType() {
        return NAME;
    }

    public WorkFlowProcessInwardDetailsRest getWorkFlowProcessInwardDetailsRest() {
        return workFlowProcessInwardDetailsRest;
    }

    public void setWorkFlowProcessInwardDetailsRest(WorkFlowProcessInwardDetailsRest workFlowProcessInwardDetailsRest) {
        this.workFlowProcessInwardDetailsRest = workFlowProcessInwardDetailsRest;
    }

    public WorkflowProcessSenderDiaryRest getWorkflowProcessSenderDiaryRest() {
        return workflowProcessSenderDiaryRest;
    }

    public void setWorkflowProcessSenderDiaryRest(WorkflowProcessSenderDiaryRest workflowProcessSenderDiaryRest) {
        this.workflowProcessSenderDiaryRest = workflowProcessSenderDiaryRest;
    }

    public WorkFlowProcessMasterValueRest getDispatchModeRest() {
        return dispatchModeRest;
    }

    public void setDispatchModeRest(WorkFlowProcessMasterValueRest dispatchModeRest) {
        this.dispatchModeRest = dispatchModeRest;
    }

    public WorkFlowProcessMasterValueRest getEligibleForFilingRest() {
        return eligibleForFilingRest;
    }

    public void setEligibleForFilingRest(WorkFlowProcessMasterValueRest eligibleForFilingRest) {
        this.eligibleForFilingRest = eligibleForFilingRest;
    }

    public ItemRest getItemRest() {
        return itemRest;
    }

    public void setItemRest(ItemRest itemRest) {
        this.itemRest = itemRest;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public List<WorkflowProcessReferenceDocRest> getWorkflowProcessReferenceDocRests() {
        return workflowProcessReferenceDocRests;
    }

    public void setWorkflowProcessReferenceDocRests(List<WorkflowProcessReferenceDocRest> workflowProcessReferenceDocRests) {
        this.workflowProcessReferenceDocRests = workflowProcessReferenceDocRests;
    }
    public List<WorkflowProcessEpersonRest> getWorkflowProcessEpersonRests() {
        return workflowProcessEpersonRests;
    }

    public void setWorkflowProcessEpersonRests(List<WorkflowProcessEpersonRest> workflowProcessEpersonRests) {
        this.workflowProcessEpersonRests = workflowProcessEpersonRests;
    }

    public Date getInitDate() {
        return InitDate;
    }

    public void setInitDate(Date initDate) {
        InitDate = initDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public WorkFlowProcessMasterValueRest getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(WorkFlowProcessMasterValueRest workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public WorkFlowProcessMasterValueRest getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(WorkFlowProcessMasterValueRest workflowType) {
        this.workflowType = workflowType;
    }

    public Boolean getDraft() {
        return isDraft;
    }

    public void setDraft(Boolean draft) {
        isDraft = draft;
    }

    public String getWorkflowTypeStr() {
        return workflowTypeStr;
    }

    public void setWorkflowTypeStr(String workflowTypeStr) {
        this.workflowTypeStr = workflowTypeStr;
    }

    public WorkflowProcessEpersonRest getOwner() {
        return owner;
    }

    public void setOwner(WorkflowProcessEpersonRest owner) {
        this.owner = owner;
    }

    public WorkflowProcessEpersonRest getSender() {
        return sender;
    }

    public void setSender(WorkflowProcessEpersonRest sender) {
        this.sender = sender;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public WorkFlowProcessOutwardDetailsRest getWorkFlowProcessOutwardDetailsRest() {
        return workFlowProcessOutwardDetailsRest;
    }

    public void setWorkFlowProcessOutwardDetailsRest(WorkFlowProcessOutwardDetailsRest workFlowProcessOutwardDetailsRest) {
        this.workFlowProcessOutwardDetailsRest = workFlowProcessOutwardDetailsRest;
    }
}
