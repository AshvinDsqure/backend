/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.jbpm.models;


import lombok.Data;
import org.dspace.content.WorkflowProcess;

import java.io.Serializable;
import java.util.List;

public  class JBPMProcess  implements Serializable  {
    private String  queueid;
    private String  initiator;
    private List<String> users;
    private String dispatch;
    private Integer jbpmprocid;
    private Integer jbpmtaskid;
    public JBPMProcess(WorkflowProcess workflowProcess){
       this.queueid=workflowProcess.getID().toString();
        this.initiator="";
        this.dispatch=workflowProcess.getSubject();
        this.users=List.of("vipul","abhi","jayesh");
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
}
