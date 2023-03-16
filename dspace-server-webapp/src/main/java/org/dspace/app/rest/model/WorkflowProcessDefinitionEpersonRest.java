/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Item REST Resource
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@LinksRest(links = {

})
public class WorkflowProcessDefinitionEpersonRest extends DSpaceObjectRest {
    public static final String NAME = "WorkflowProcessDefinitionEpersonRest";
    public static final String PLURAL_NAME = "WorkflowProcessDefinitionEpersonRest";
    public static final String CATEGORY = RestAddressableModel.CORE;
    @JsonProperty
    private Integer index;
    @JsonProperty()
    private EPersonRest ePersonRest = null;
    @JsonProperty()
    private WorkFlowProcessDefinitionRest workflowProcessReferenceDocRest;
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
}
