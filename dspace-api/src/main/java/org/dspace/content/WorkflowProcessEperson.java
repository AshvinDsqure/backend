/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import org.dspace.eperson.EPerson;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
@Table(name = "workflowprocesseperson")
public class WorkflowProcessEperson extends DSpaceObject implements DSpaceObjectLegacySupport {
    /**
     * Wild card for Dublin Core metadata qualifiers/languages
     */
    public static final String ANY = "*";
    @Column(name = "workflowprocessdefinitioneperson_id", insertable = false, updatable = false)
    private Integer legacyId;
    @Column(name = "index")
    private Integer index;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eperson")
    private EPerson ePerson = null;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflowprocessdefinition")
    private WorkflowProcessDefinition workflowProcessDefinition;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflowprocess_id")
    private WorkflowProcess workflowProcess;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private WorkFlowProcessMasterValue department = null;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usetype_id")
    private WorkFlowProcessMasterValue usertype = null;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    private WorkFlowProcessMasterValue office = null;

    @Column(name = "assign_date", columnDefinition = "timestamp with time zone")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignDate = new Date();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workflowProcessEpeople", cascade = {CascadeType.ALL})
    private List<WorkFlowProcessHistory> workFlowProcessHistories = new ArrayList<>();

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getName() {
        return "workflowprocessdefinition";
    }

    @Override
    public Integer getLegacyId() {
        return this.legacyId;
    }

    public void setLegacyId(Integer legacyId) {
        this.legacyId = legacyId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public EPerson getePerson() {
        return ePerson;
    }

    public void setePerson(EPerson ePerson) {
        this.ePerson = ePerson;
    }

    public WorkflowProcessDefinition getWorkflowProcessDefinition() {
        return workflowProcessDefinition;
    }

    public void setWorkflowProcessDefinition(WorkflowProcessDefinition workflowProcessDefinition) {
        this.workflowProcessDefinition = workflowProcessDefinition;
    }

    public WorkflowProcess getWorkflowProcess() {
        return workflowProcess;
    }

    public void setWorkflowProcess(WorkflowProcess workflowProcess) {
        this.workflowProcess = workflowProcess;
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

    public Date getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(Date assignDate) {
        this.assignDate = assignDate;
    }

    public WorkFlowProcessMasterValue getUsertype() {
        return usertype;
    }

    public void setUsertype(WorkFlowProcessMasterValue usertype) {
        this.usertype = usertype;
    }

    public List<WorkFlowProcessHistory> getWorkFlowProcessHistories() {
        return workFlowProcessHistories;
    }

    public void setWorkFlowProcessHistories(List<WorkFlowProcessHistory> workFlowProcessHistories) {
        this.workFlowProcessHistories = workFlowProcessHistories;
    }
}
