/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dspace.eperson.EPerson;

import java.util.Date;

public class WorkFlowProcessHistoryRest extends  DSpaceObjectRest{
    public static final String NAME = "workflowprocesshistorie";
    public static final String PLURAL_NAME = "workflowprocesshistories";
    public static final String CATEGORY = RestAddressableModel.WORKFLOWPROCESSHISTORY;

    public static final String GROUPS = "groups";

    private Integer legacyId;

    private EPerson epersonid;
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public EPerson getEpersonid() {
        return epersonid;
    }

    public void setEpersonid(EPerson epersonid) {
        this.epersonid = epersonid;
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


}
