/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dspace.content.Item;
import org.dspace.content.enums.Priority;
import org.dspace.eperson.EPerson;

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
    public static final String CATEGORY = RestAddressableModel.CORE;
    @JsonProperty
    private String Subject;
    @JsonProperty
    private Date InitDate = new Date();
    @JsonProperty
    private ItemRest item;
    @JsonProperty
    private EPersonRest submitter = null;
    @JsonProperty
    private String priority;
    @JsonProperty
    List<WorkflowProcessReferenceDocRest> workflowProcessReferenceDocRests=new ArrayList<>();
    private Date assignDueDate = new Date();
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

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public Date getInitDate() {
        return InitDate;
    }

    public void setInitDate(Date initDate) {
        InitDate = initDate;
    }

    public ItemRest getItem() {
        return item;
    }

    public void setItem(ItemRest item) {
        this.item = item;
    }

    public EPersonRest getSubmitter() {
        return submitter;
    }

    public void setSubmitter(EPersonRest submitter) {
        this.submitter = submitter;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getAssignDueDate() {
        return assignDueDate;
    }

    public void setAssignDueDate(Date assignDueDate) {
        this.assignDueDate = assignDueDate;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public List<WorkflowProcessReferenceDocRest> getWorkflowProcessReferenceDocRests() {
        return workflowProcessReferenceDocRests;
    }

    public void setWorkflowProcessReferenceDocRests(List<WorkflowProcessReferenceDocRest> workflowProcessReferenceDocRests) {
        this.workflowProcessReferenceDocRests = workflowProcessReferenceDocRests;
    }
}
