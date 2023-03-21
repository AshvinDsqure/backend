/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dspace.app.rest.RestResourceController;

import java.util.List;

/**
 * The rest resource used for workflow definitions
 *
 * @author Maria Verdonck (Atmire) on 11/12/2019
 */
@LinksRest(links = {

})
public class WorkflowProcessNoteRest extends DSpaceObjectRest{

    public static final String CATEGORY = "config";
    public static final String NAME = "workflownote";
    public static final String NAME_PLURAL = "workflownots";

    public static final String COLLECTIONS_MAPPED_TO = "collections";
    public static final String STEPS = "steps";

    private String name;
    private boolean isDefault;
    private List<WorkflowStepRest> steps;

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public Class getController() {
        return RestResourceController.class;
    }

    @Override
    public String getType() {
        return NAME;
    }

    @Override
    @JsonIgnore
    public String getId() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @JsonIgnore
    public List<WorkflowStepRest> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowStepRest> steps) {
        this.steps = steps;
    }
}
