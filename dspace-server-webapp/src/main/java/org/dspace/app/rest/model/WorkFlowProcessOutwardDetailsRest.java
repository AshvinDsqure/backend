/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import java.util.Date;
import java.util.UUID;

public class WorkFlowProcessOutwardDetailsRest extends  DSpaceObjectRest{
    public static final String NAME = "workflowprocessoutwarddetail";
    public static final String PLURAL_NAME = "workflowprocessoutwarddetails";
    public static final String CATEGORY = RestAddressableModel.WORKFLOWPROCESSOUTWARDDETAIL;

    public static final String GROUPS = "groups";

    private Integer legacyId;

    private String outwardNumber;
    private Date outwardDate;
    private UUID outwardmediumid;
    private UUID outwarddepartmentid;

    private UUID outwardmodeid;

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

    public UUID getOutwardmediumid() {
        return outwardmediumid;
    }

    public void setOutwardmediumid(UUID outwardmediumid) {
        this.outwardmediumid = outwardmediumid;
    }

    public UUID getOutwarddepartmentid() {
        return outwarddepartmentid;
    }

    public void setOutwarddepartmentid(UUID outwarddepartmentid) {
        this.outwarddepartmentid = outwarddepartmentid;
    }

    public UUID getOutwardmodeid() {
        return outwardmodeid;
    }

    public void setOutwardmodeid(UUID outwardmodeid) {
        this.outwardmodeid = outwardmodeid;
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
