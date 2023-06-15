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
import org.dspace.content.WorkFlowProcessMasterValue;
import org.dspace.content.WorkflowProcessNote;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * The Item REST Resource
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@LinksRest(links = {

})
public class WorkflowProcessReferenceDocRest extends DSpaceObjectRest {
    public static final String NAME = "workflowprocessreferencedoc";
    public static final String PLURAL_NAME = "workflowprocessreferencedocs";
    public static final String CATEGORY = RestAddressableModel.WORKFLOWPROCESSREFERENCEDOC;
    @JsonProperty
    @NotNull
    private BitstreamRest bitstreamRest;
    @JsonProperty
    private String subject;
    @JsonProperty
    private String editortext;
    @JsonProperty
    private String referenceNumber;

    @JsonProperty
    private String description;
    @JsonProperty
    private WorkFlowProcessMasterValueRest workFlowProcessReferenceDocType;
    @JsonProperty
    private WorkFlowProcessMasterValueRest drafttypeRest;
    @JsonProperty
    private WorkFlowProcessMasterValueRest latterCategoryRest;
    @JsonProperty
    @JsonDeserialize(converter = MyDateConverter.class)
    private Date initdate = new Date();
    @JsonProperty
    private WorkFlowProcessRest workFlowProcessRest;
    @JsonProperty
    private WorkflowProcessNoteRest workflowProcessNoteRest;

    @JsonProperty
    private WorkflowProcessReferenceDocVersionRest workflowProcessReferenceDocVersionRest;


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

    public BitstreamRest getBitstreamRest() {
        return bitstreamRest;
    }

    public void setBitstreamRest(BitstreamRest bitstreamRest) {
        this.bitstreamRest = bitstreamRest;
    }

    public Date getInitdate() {
        return initdate;
    }

    public void setInitdate(Date initdate) {
        this.initdate = initdate;
    }

    public WorkFlowProcessRest getWorkFlowProcessRest() {
        return workFlowProcessRest;
    }
    public void setWorkFlowProcessRest(WorkFlowProcessRest workFlowProcessRest) {
        this.workFlowProcessRest = workFlowProcessRest;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public WorkFlowProcessMasterValueRest getLatterCategoryRest() {
        return latterCategoryRest;
    }

    public void setLatterCategoryRest(WorkFlowProcessMasterValueRest latterCategoryRest) {
        this.latterCategoryRest = latterCategoryRest;
    }

    public WorkFlowProcessMasterValueRest getWorkFlowProcessReferenceDocType() {
        return workFlowProcessReferenceDocType;
    }

    public void setWorkFlowProcessReferenceDocType(WorkFlowProcessMasterValueRest workFlowProcessReferenceDocType) {
        this.workFlowProcessReferenceDocType = workFlowProcessReferenceDocType;
    }

    public WorkFlowProcessMasterValueRest getDrafttypeRest() {
        return drafttypeRest;
    }

    public void setDrafttypeRest(WorkFlowProcessMasterValueRest drafttypeRest) {
        this.drafttypeRest = drafttypeRest;
    }

    public WorkflowProcessNoteRest getWorkflowProcessNoteRest() {
        return workflowProcessNoteRest;
    }

    public void setWorkflowProcessNoteRest(WorkflowProcessNoteRest workflowProcessNoteRest) {
        this.workflowProcessNoteRest = workflowProcessNoteRest;
    }
    public String getEditortext() {
        return editortext;
    }

    public void setEditortext(String editortext) {
        this.editortext = editortext;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkflowProcessReferenceDocVersionRest getWorkflowProcessReferenceDocVersionRest() {
        return workflowProcessReferenceDocVersionRest;
    }

    public void setWorkflowProcessReferenceDocVersionRest(WorkflowProcessReferenceDocVersionRest workflowProcessReferenceDocVersionRest) {
        this.workflowProcessReferenceDocVersionRest = workflowProcessReferenceDocVersionRest;
    }
}
