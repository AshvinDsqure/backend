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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dspace.app.rest.enums.WorkFlowUserType;
import org.dspace.app.rest.model.helper.MyDateConverter;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * The Item REST Resource
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@LinksRest(links = {

})
public class WorkflowProcessEpersonRest extends DSpaceObjectRest {
    public static final String NAME = "WorkflowProcessDefinitionEpersonRest";
    public static final String PLURAL_NAME = "WorkflowProcessDefinitionEpersonRest";
    public static final String CATEGORY = RestAddressableModel.CORE;
    @JsonProperty
    private Integer index;
    @JsonProperty()
    @NotBlank
    private EPersonRest ePersonRest = null;
    @JsonProperty()
    private WorkFlowProcessDefinitionRest workflowProcessReferenceDocRest;
    @JsonProperty
    @NotBlank
    private WorkFlowProcessMasterValueRest departmentRest = null;
    @JsonProperty
    @NotBlank
    private WorkFlowProcessMasterValueRest officeRest = null;
    @JsonProperty
    private WorkFlowProcessMasterValueRest userType = null;

    @JsonProperty
    private ItemRest itemRest = null;
    @JsonProperty
    private String comment;
    @JsonProperty
    private  Boolean initiator=false;
    @Transient
    private WorkFlowUserType workFlowUserType;
    @JsonProperty
    @JsonDeserialize(converter = MyDateConverter.class)
    private Date assignDate =null;

    @Override
    public String getCategory() {
        return CATEGORY;
    }
    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getType() {
        return NAME;
    }

    public Integer getIndex() {
        return index;
    }


    public Date getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(Date assignDate) {
        this.assignDate = assignDate;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public EPersonRest getePersonRest() {
        return ePersonRest;
    }

    public void setePersonRest(EPersonRest ePersonRest) {
        this.ePersonRest = ePersonRest;
    }

    public WorkFlowProcessDefinitionRest getWorkflowProcessReferenceDocRest() {
        return workflowProcessReferenceDocRest;
    }

    public void setWorkflowProcessReferenceDocRest(WorkFlowProcessDefinitionRest workflowProcessReferenceDocRest) {
        this.workflowProcessReferenceDocRest = workflowProcessReferenceDocRest;
    }

    public WorkFlowProcessMasterValueRest getDepartmentRest() {
        return departmentRest;
    }

    public void setDepartmentRest(WorkFlowProcessMasterValueRest departmentRest) {
        this.departmentRest = departmentRest;
    }

    public WorkFlowProcessMasterValueRest getOfficeRest() {
        return officeRest;
    }

    public void setOfficeRest(WorkFlowProcessMasterValueRest officeRest) {
        this.officeRest = officeRest;
    }

    public WorkFlowProcessMasterValueRest getUserType() {
        return userType;
    }

    public void setUserType(WorkFlowProcessMasterValueRest userType) {
        this.userType = userType;
    }

    public WorkFlowUserType getWorkFlowUserType() {
        return workFlowUserType;
    }

    public void setWorkFlowUserType(WorkFlowUserType workFlowUserType) {
        this.workFlowUserType = workFlowUserType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getInitiator() {
        return initiator;
    }

    public void setInitiator(Boolean initiator) {
        this.initiator = initiator;
    }

    public ItemRest getItemRest() {
        return itemRest;
    }

    public void setItemRest(ItemRest itemRest) {
        this.itemRest = itemRest;
    }
}
