/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.jbpm.models;


import lombok.Data;
import org.dspace.app.rest.model.WorkFlowProcessRest;
import org.dspace.app.rest.model.WorkflowProcessEpersonRest;
import org.dspace.content.WorkflowProcess;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JBPMProcess implements Serializable {
    private String queueid;
    private String initiator;
    private List<String> users;

    private String dispatch;
    private Integer jbpmprocid;
    private Integer jbpmtaskid;
    private  String procstatus;
    public JBPMProcess(){

    }
    public JBPMProcess(WorkFlowProcessRest workflowProcess) {
        this.queueid = workflowProcess.getId();
        Optional<WorkflowProcessEpersonRest> optionalWorkflowProcessEpersonRest = workflowProcess.getWorkflowProcessEpersonRests().stream().filter(d -> d.getIndex() == 0).findFirst();
        if (optionalWorkflowProcessEpersonRest.isPresent()) {
            this.initiator =optionalWorkflowProcessEpersonRest.get().getUuid();
        }else{
            this.initiator="Dsquare";
        }
        this.dispatch = workflowProcess.getDispatchModeRest().getPrimaryvalue();
        this.users = workflowProcess.getWorkflowProcessEpersonRests().stream().map(w -> w.getUuid()).collect(Collectors.toList());
    }

    public String getQueueid() {
        return queueid;
    }

    public void setQueueid(String queueid) {
        this.queueid = queueid;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getDispatch() {
        return dispatch;
    }

    public void setDispatch(String dispatch) {
        this.dispatch = dispatch;
    }


    public Integer getJbpmprocid() {
        return jbpmprocid;
    }

    public void setJbpmprocid(Integer jbpmprocid) {
        this.jbpmprocid = jbpmprocid;
    }

    public Integer getJbpmtaskid() {
        return jbpmtaskid;
    }

    public void setJbpmtaskid(Integer jbpmtaskid) {
        this.jbpmtaskid = jbpmtaskid;
    }

    public String getProcstatus() {
        return procstatus;
    }

    public void setProcstatus(String procstatus) {
        this.procstatus = procstatus;
    }
}
