/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import org.dspace.content.enums.Priority;
import org.dspace.content.enums.WorkFlowProcessReferenceDocType;
import org.dspace.eperson.EPerson;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
@Table(name = "workflowprocessreferencedoc")
public class WorkflowProcessReferenceDoc extends DSpaceObject implements DSpaceObjectLegacySupport {
    /**
     * Wild card for Dublin Core metadata qualifiers/languages
     */
    public static final String ANY = "*";
    @Column(name = "workflowreference_id", insertable = false, updatable = false)
    private Integer legacyId;
    @Column(name = "subject")
    private String subject;
    @Column(name = "referencenumber")
    private String referenceNumber;
    @Column(name = "editortext")
    private String editortext;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "bitstream")
    private Bitstream bitstream;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "workflowprocess")
    private WorkflowProcess workflowProcess;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "workflowprocessnote")
    private WorkflowProcessNote workflowprocessnote;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documenttype")
    private WorkFlowProcessMasterValue workFlowProcessReferenceDocType;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draft_type_id")
    private WorkFlowProcessMasterValue drafttype;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lattercategory")
    private WorkFlowProcessMasterValue latterCategory;
    @Column(name = "initdate", columnDefinition = "timestamp with time zone")
    @Temporal(TemporalType.TIMESTAMP)
    private Date initdate = new Date();
    @Column(name = "description")
    private String description;
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "workflowProcessReferenceDoc",cascade = { CascadeType.ALL})
    private Set<WorkflowProcessReferenceDocVersion> workflowProcessReferenceDocVersion=new HashSet<>();
    @Override
    public int getType() {
        return 0;
    }
    @Override
    public String getName() {
        return "workflow";
    }
    @Override
    public Integer getLegacyId() {
        return this.legacyId;
    }
    public void setLegacyId(Integer legacyId) {
        this.legacyId = legacyId;
    }

    public Bitstream getBitstream() {
        return bitstream;
    }
    public void setBitstream(Bitstream bitstream) {
        this.bitstream = bitstream;
    }
    public WorkFlowProcessMasterValue getWorkFlowProcessReferenceDocType() {
        return workFlowProcessReferenceDocType;
    }

    public void setWorkFlowProcessReferenceDocType(WorkFlowProcessMasterValue workFlowProcessReferenceDocType) {
        this.workFlowProcessReferenceDocType = workFlowProcessReferenceDocType;
    }
    public WorkflowProcess getWorkflowProcess() {
        return workflowProcess;
    }
    public void setWorkflowProcess(WorkflowProcess workflowProcess) {
        this.workflowProcess = workflowProcess;
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
    public WorkFlowProcessMasterValue getLatterCategory() {
        return latterCategory;
    }
    public void setLatterCategory(WorkFlowProcessMasterValue latterCategory) {
        this.latterCategory = latterCategory;
    }
    public Date getInitdate() {
        return initdate;
    }

    public void setInitdate(Date initdate) {
        this.initdate = initdate;
    }
    public String getEditortext() {
        return editortext;
    }
    public void setEditortext(String editortext) {
        this.editortext = editortext;
    }
    public WorkflowProcessNote getWorkflowprocessnote() {
        return workflowprocessnote;
    }
    public void setWorkflowprocessnote(WorkflowProcessNote workflowprocessnote) {
        this.workflowprocessnote = workflowprocessnote;
    }

    public Set<WorkflowProcessReferenceDocVersion> getWorkflowProcessReferenceDocVersion() {
        return workflowProcessReferenceDocVersion;
    }

    public WorkFlowProcessMasterValue getDrafttype() {
        return drafttype;
    }

    public void setDrafttype(WorkFlowProcessMasterValue drafttype) {
        this.drafttype = drafttype;
    }

    public void setWorkflowProcessReferenceDocVersion(Set<WorkflowProcessReferenceDocVersion> workflowProcessReferenceDocVersion) {
        this.workflowProcessReferenceDocVersion = workflowProcessReferenceDocVersion;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
