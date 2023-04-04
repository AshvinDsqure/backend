/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.*;

/**
 * Class representing an item in DSpace.
 * <P>
 * This class holds in memory the item Dublin Core metadata, the bundles in the
 * item, and the bitstreams in those bundles. When modifying the item, if you
 * modify the Dublin Core or the "in archive" flag, you must call
 * <code>update</code> for the changes to be written to the database.
 * Creating, adding or removing bundles or bitstreams has immediate effect in
 * the database.
 *
 * @author Robert Tansley
 * @author Martin Hald
 */
@Entity
@Table(name = "workflowprocessoutwarddetails")
public class WorkFlowProcessOutwardDetails extends DSpaceObject implements DSpaceObjectLegacySupport {
    /**
     * Wild card for Dublin Core metadata qualifiers/languages
     */
    public static final String ANY = "*";
    @Column(name = "workflowprocessoutwarddetails_id", insertable = false, updatable = false)
    private Integer legacyId;

    @Column(name = "outwardnumber")
    private String outwardNumber;
    @Column(name = "outwarddate", columnDefinition = "timestamp with time zone")
    @Temporal(TemporalType.TIMESTAMP)
    private Date outwardDate;
    @Column(name = "outwardmedium_id")
    private UUID outwardmediumid;
    @Column(name = "outwarddepartment_id")
    private UUID outwarddepartmentid;

    @Column(name = "outwardmode_id")
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

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getName() {
        return "workflowprocessoutwarddetails";
    }

    @Override
    public Integer getLegacyId() {
        return this.legacyId;
    }
    public void setLegacyId(Integer legacyId) {
        this.legacyId = legacyId;
    }
}
