/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import lombok.Data;
import org.dspace.content.enums.Dispatch;
import org.dspace.content.enums.Priority;
import org.dspace.eperson.EPerson;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.*;

/**
 * Class representing an item in DSpace.
 * <p>
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
@Data
public class WorkflowProcess extends DSpaceObject implements DSpaceObjectLegacySupport {
    /**
     * Wild card for Dublin Core metadata qualifiers/languages
     */
    public static final String ANY = "*";
    @OneToOne(fetch = FetchType.LAZY,cascade = {CascadeType.ALL})
    @JoinColumn(name = "workflowprocessinwarddetails_id")
    private WorkFlowProcessInwardDetails workFlowProcessInwardDetails = null;
    @OneToOne(fetch = FetchType.LAZY,cascade = {CascadeType.ALL})
    @JoinColumn(name = "workflowprocesssenderdiary")
    private WorkflowProcessSenderDiary workflowProcessSenderDiary = null;
    /* Filling Details*/
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id")
    private WorkFlowProcessMasterValue dispatchMode = null;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eligible_for_filing_id")
    private WorkFlowProcessMasterValue eligibleForFiling = null;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    private Item item;
    /* Attechment  Details*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workflowProcess", cascade = {CascadeType.ALL})
    private List<WorkflowProcessReferenceDoc> workflowProcessReferenceDocs = new ArrayList<>();
    /* Office   Details*/
    @Column(name = "subject")
    private String Subject;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private WorkFlowProcessMasterValue department = null;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    private WorkFlowProcessMasterValue office = null;
    @Column(name = "workflow_id", insertable = false, updatable = false)
    private Integer legacyId;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workflowProcess", cascade = {CascadeType.ALL})
    private List<WorkflowProcessEperson> workflowProcessEpeople;
    @Column(name = "init_date", columnDefinition = "timestamp with time zone")
    @Temporal(TemporalType.TIMESTAMP)
    private Date InitDate = new Date();

    @Enumerated(EnumType.STRING)
    private Priority priority;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatchmode")
    private WorkFlowProcessMasterValue dispatchmode = null;
    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.ALL})
    @JoinColumn(name = "submitter_id")
    private WorkflowProcessEperson submitter = null;
    /*@Column(name = "date", columnDefinition = "timestamp with time zone")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignDueDate = new Date();*/


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

    public WorkFlowProcessInwardDetails getWorkFlowProcessInwardDetails() {
        return workFlowProcessInwardDetails;
    }

    public void setWorkFlowProcessInwardDetails(WorkFlowProcessInwardDetails workFlowProcessInwardDetails) {
        this.workFlowProcessInwardDetails = workFlowProcessInwardDetails;
    }

    public WorkflowProcessSenderDiary getWorkflowProcessSenderDiary() {
        return workflowProcessSenderDiary;
    }

    public void setWorkflowProcessSenderDiary(WorkflowProcessSenderDiary workflowProcessSenderDiary) {
        this.workflowProcessSenderDiary = workflowProcessSenderDiary;
    }

    public WorkFlowProcessMasterValue getDispatchMode() {
        return dispatchMode;
    }

    public void setDispatchMode(WorkFlowProcessMasterValue dispatchMode) {
        this.dispatchMode = dispatchMode;
    }

    public WorkFlowProcessMasterValue getEligibleForFiling() {
        return eligibleForFiling;
    }

    public void setEligibleForFiling(WorkFlowProcessMasterValue eligibleForFiling) {
        this.eligibleForFiling = eligibleForFiling;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<WorkflowProcessReferenceDoc> getWorkflowProcessReferenceDocs() {
        return workflowProcessReferenceDocs;
    }

    public void setWorkflowProcessReferenceDocs(List<WorkflowProcessReferenceDoc> workflowProcessReferenceDocs) {
        this.workflowProcessReferenceDocs = workflowProcessReferenceDocs;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public WorkFlowProcessMasterValue getDepartment() {
        return department;
    }

    public void setDepartment(WorkFlowProcessMasterValue department) {
        this.department = department;
    }

    public WorkFlowProcessMasterValue getOffice() {
        return office;
    }

    public void setOffice(WorkFlowProcessMasterValue office) {
        this.office = office;
    }

    public void setLegacyId(Integer legacyId) {
        this.legacyId = legacyId;
    }

    public List<WorkflowProcessEperson> getWorkflowProcessEpeople() {
        return workflowProcessEpeople;
    }

    public void setWorkflowProcessEpeople(List<WorkflowProcessEperson> workflowProcessEpeople) {
        this.workflowProcessEpeople = workflowProcessEpeople;
    }

    public Date getInitDate() {
        return InitDate;
    }

    public void setInitDate(Date initDate) {
        InitDate = initDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public WorkFlowProcessMasterValue getDispatchmode() {
        return dispatchmode;
    }

    public void setDispatchmode(WorkFlowProcessMasterValue dispatchmode) {
        this.dispatchmode = dispatchmode;
    }

    public WorkflowProcessEperson getSubmitter() {
        return submitter;
    }

    public void setSubmitter(WorkflowProcessEperson submitter) {
        this.submitter = submitter;
    }
}
