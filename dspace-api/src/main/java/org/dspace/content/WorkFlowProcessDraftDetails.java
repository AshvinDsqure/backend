/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;

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
@Table(name = "workflowprocessdraftdetails")
public class WorkFlowProcessDraftDetails extends DSpaceObject implements DSpaceObjectLegacySupport {
    /**
     * Wild card for Dublin Core metadata qualifiers/languages
     */
    public static final String ANY = "*";
    @Column(name = "workflowprocessdraftdetails_id", insertable = false, updatable = false)
    private Integer legacyId;
    @Column(name = "draftdate", columnDefinition = "timestamp with time zone")
    @Temporal(TemporalType.TIMESTAMP)
    private Date draftdate;
    //drafttype means notesheet or document
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draft_type_id")
    private WorkFlowProcessMasterValue drafttype;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentsignator_id")
    private EPerson documentsignator;

    @Override
    public int getType() {
        return 0;
    }
    @Override
    public String getName() {
        return "workflowprocessdraftdetails";
    }
    @Override
    public Integer getLegacyId() {
        return this.legacyId;
    }
    public void setLegacyId(Integer legacyId) {
        this.legacyId = legacyId;
    }

    public Date getDraftdate() {
        return draftdate;
    }
    public void setDraftdate(Date draftdate) {
        this.draftdate = draftdate;
    }
    public WorkFlowProcessMasterValue getDrafttype() {
        return drafttype;
    }

    public void setDrafttype(WorkFlowProcessMasterValue drafttype) {
        this.drafttype = drafttype;
    }

    public EPerson getDocumentsignator() {
        return documentsignator;
    }

    public void setDocumentsignator(EPerson documentsignator) {
        this.documentsignator = documentsignator;
    }
}
