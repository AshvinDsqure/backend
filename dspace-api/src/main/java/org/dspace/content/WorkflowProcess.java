/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import org.dspace.content.enums.Dispatch;
import org.dspace.content.enums.Priority;
import org.dspace.eperson.EPerson;
import org.hibernate.annotations.Where;

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
@Table(name = "workflowprocess")
public class WorkflowProcess extends DSpaceObject implements DSpaceObjectLegacySupport {
    /**
     * Wild card for Dublin Core metadata qualifiers/languages
     */
    public static final String ANY = "*";

        @Column(name = "workflow_id", insertable = false, updatable = false)
    private Integer legacyId;
    @Column(name = "subject")
    private String Subject;

    @Column(name = "init_date", columnDefinition = "timestamp with time zone")
    @Temporal(TemporalType.TIMESTAMP)
    private Date InitDate = new Date();
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "item")
    private Item item;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "workflowProcess",cascade = { CascadeType.ALL})
    private Set<WorkflowProcessReferenceDoc> workflowProcessReferenceDocs=new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitter_id")
    private EPerson submitter = null;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflowprocesssenderdiary")
    private WorkflowProcessSenderDiary workflowProcessSenderDiary = null;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflowprocesscorrespondence")
    private WorkflowProcessCorrespondence workflowProcessCorrespondence ;

    @Enumerated(EnumType.STRING)
    private Priority priority;
    @Enumerated(EnumType.STRING)
    @Column(name = "dispatchmode")
    private Dispatch dispatchmode;
    @Column(name = "assignduedate", columnDefinition = "timestamp with time zone")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignDueDate = new Date();


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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public EPerson getSubmitter() {
        return submitter;
    }

    public void setSubmitter(EPerson submitter) {
        this.submitter = submitter;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Date getAssignDueDate() {
        return assignDueDate;
    }

    public void setAssignDueDate(Date assignDueDate) {
        this.assignDueDate = assignDueDate;
    }

    public Set<WorkflowProcessReferenceDoc> getWorkflowProcessReferenceDocs() {
        return workflowProcessReferenceDocs;
    }

    public void setWorkflowProcessReferenceDocs(Set<WorkflowProcessReferenceDoc> workflowProcessReferenceDocs) {
        this.workflowProcessReferenceDocs = workflowProcessReferenceDocs;
    }

    public WorkflowProcessSenderDiary getWorkflowProcessSenderDiary() {
        return workflowProcessSenderDiary;
    }

    public void setWorkflowProcessSenderDiary(WorkflowProcessSenderDiary workflowProcessSenderDiary) {
        this.workflowProcessSenderDiary = workflowProcessSenderDiary;
    }

    public WorkflowProcessCorrespondence getWorkflowProcessCorrespondence() {
        return workflowProcessCorrespondence;
    }

    public void setWorkflowProcessCorrespondence(WorkflowProcessCorrespondence workflowProcessCorrespondence) {
        this.workflowProcessCorrespondence = workflowProcessCorrespondence;
    }

    public Dispatch getDispatchmode() {
        return dispatchmode;
    }

    public void setDispatchmode(Dispatch dispatchmode) {
        this.dispatchmode = dispatchmode;
    }
}
