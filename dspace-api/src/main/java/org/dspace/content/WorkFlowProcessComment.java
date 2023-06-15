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
@Table(name = "workflowprocesscomment")
public class WorkFlowProcessComment extends DSpaceObject implements DSpaceObjectLegacySupport {
    /**
     * Wild card for Dublin Core metadata qualifiers/languages
     */
    public static final String ANY = "*";
    @Column(name = "workflowprocesscomment_id", insertable = false, updatable = false)
    private Integer legacyId;
    @Column(name = "comment")
    private String comment;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflowprocessreferencedoc_idf")
    private WorkflowProcessReferenceDoc workflowProcessReferenceDoc;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflowprocesshistory_idf")
    private WorkFlowProcessHistory workFlowProcessHistory;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflowprocess_fid")
    private WorkflowProcess workFlowProcess;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitter_id")
    private EPerson submitter = null;
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
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public WorkflowProcessReferenceDoc getWorkflowProcessReferenceDoc() {
        return workflowProcessReferenceDoc;
    }
    public void setWorkflowProcessReferenceDoc(WorkflowProcessReferenceDoc workflowProcessReferenceDoc) {
        this.workflowProcessReferenceDoc = workflowProcessReferenceDoc;
    }
    public WorkFlowProcessHistory getWorkFlowProcessHistory() {
        return workFlowProcessHistory;
    }
    public void setWorkFlowProcessHistory(WorkFlowProcessHistory workFlowProcessHistory) {
        this.workFlowProcessHistory = workFlowProcessHistory;
    }
    public EPerson getSubmitter() {
        return submitter;
    }
    public void setSubmitter(EPerson submitter) {
        this.submitter = submitter;
    }
    public WorkflowProcess getWorkFlowProcess() {
        return workFlowProcess;
    }
    public void setWorkFlowProcess(WorkflowProcess workFlowProcess) {
        this.workFlowProcess = workFlowProcess;
    }
}
