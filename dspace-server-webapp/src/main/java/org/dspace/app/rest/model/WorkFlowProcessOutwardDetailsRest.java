/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dspace.app.rest.model.helper.MyDateConverter;
import org.dspace.content.WorkFlowProcessMasterValue;
import org.dspace.eperson.Group;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

public class WorkFlowProcessOutwardDetailsRest extends  DSpaceObjectRest{
    public static final String NAME = "workflowprocessoutwarddetail";
    public static final String PLURAL_NAME = "workflowprocessoutwarddetails";
    public static final String CATEGORY = RestAddressableModel.WORKFLOWPROCESSOUTWARDDETAIL;

    public static final String GROUPS = "groups";

    private Integer legacyId;
    private String outwardNumber;
    @JsonDeserialize(converter = MyDateConverter.class)
    private Date outwardDate;
    private WorkFlowProcessMasterValue outwardmediumRest;
    private Group outwardEpersonGroupRest;
    private WorkFlowProcessMasterValue outwardmodeRest;

    public String getOutwardNumber() {
        return outwardNumber;
    }

    public void setOutwardNumber(String outwardNumber) {
        this.outwardNumber = outwardNumber;
    }

    public Date getOutwardDate() {
        return outwardDate;
    }

    public void setOutwardDate(Date outwardDate) {
        this.outwardDate = outwardDate;
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


    public WorkFlowProcessMasterValue getOutwardmediumRest() {
        return outwardmediumRest;
    }

    public void setOutwardmediumRest(WorkFlowProcessMasterValue outwardmediumRest) {
        this.outwardmediumRest = outwardmediumRest;
    }

    public Group getOutwardEpersonGroupRest() {
        return outwardEpersonGroupRest;
    }

    public void setOutwardEpersonGroupRest(Group outwardEpersonGroupRest) {
        this.outwardEpersonGroupRest = outwardEpersonGroupRest;
    }

    public WorkFlowProcessMasterValue getOutwardmodeRest() {
        return outwardmodeRest;
    }

    public void setOutwardmodeRest(WorkFlowProcessMasterValue outwardmodeRest) {
        this.outwardmodeRest = outwardmodeRest;
    }
}
