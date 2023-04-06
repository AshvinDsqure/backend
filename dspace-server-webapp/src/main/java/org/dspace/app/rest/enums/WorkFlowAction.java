/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.enums;

import org.dspace.app.rest.jbpm.JbpmServerImpl;
import org.dspace.app.rest.model.WorkFlowProcessRest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.*;
import org.dspace.content.service.WorkFlowProcessHistoryService;
import org.dspace.content.service.WorkFlowProcessMasterService;
import org.dspace.content.service.WorkFlowProcessMasterValueService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.Optional;

public enum WorkFlowAction {
    MASTER("Action"),
    CREATE("Create"){
        @Override
        public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess, WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
            String jbpmResponce = this.getJbpmServer().startProcess(workFlowProcessRest);
            System.out.println("jbpmResponce:::"+jbpmResponce);
            WorkFlowProcessHistory workFlowAction=new WorkFlowProcessHistory();
            Optional<WorkflowProcessEperson> workflowProcessEpersonOptional= workflowProcess.getWorkflowProcessEpeople().stream().filter(s->s.getUsertype().getPrimaryvalue().equals(WorkFlowUserType.INITIATOR)).findFirst();
            if(!workflowProcessEpersonOptional.isPresent()){
                throw new RuntimeException("initiator not  found.....");
            }
            WorkFlowProcessMaster workFlowProcessMaster = MASTER.getMaster(context);
            WorkFlowProcessMasterValue workFlowProcessMasterValue = this.getWorkFlowProcessMasterValueService().findByName(context, this.getAction(), workFlowProcessMaster);
            workFlowAction.setActionDate(new Date());
            workFlowAction.setAction(workFlowProcessMasterValue);
            workFlowAction.setWorkflowProcess(workflowProcess);
            return this.getWorkFlowProcessHistoryService().create(context, workFlowAction);
        }
    },
    FORWARD("Forward"){
        @Override
        public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess,WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
            String forwardResponce=this.getJbpmServer().forwardTask(workFlowProcessRest);
            System.out.println("jbpmResponce create"+forwardResponce);
            WorkFlowProcessHistory workFlowAction=new WorkFlowProcessHistory();
            Optional<WorkflowProcessEperson> workflowProcessEpersonOptional= workflowProcess.getWorkflowProcessEpeople().stream().max(Comparator.comparing(WorkflowProcessEperson::getIndex));
            if(!workflowProcessEpersonOptional.isPresent()){
                throw new RuntimeException("user not  found");
            }
            WorkFlowProcessMaster workFlowProcessMaster = MASTER.getMaster(context);
            WorkFlowProcessMasterValue workFlowProcessMasterValue = this.getWorkFlowProcessMasterValueService().findByName(context, this.getAction(), workFlowProcessMaster);
            workFlowAction.setActionDate(new Date());
            workFlowAction.setAction(workFlowProcessMasterValue);
            workFlowAction.setWorkflowProcess(workflowProcess);
            return this.getWorkFlowProcessHistoryService().create(context, workFlowAction);
        }
    },
    BACKWARD("Backward"){
        @Override
        public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess,WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
            String forwardResponce=this.getJbpmServer().backwardTask(workFlowProcessRest);
            System.out.println("jbpmResponce create"+forwardResponce);
            WorkFlowProcessHistory workFlowAction=new WorkFlowProcessHistory();
            Optional<WorkflowProcessEperson> workflowProcessEpersonOptional= workflowProcess.getWorkflowProcessEpeople().stream().max(Comparator.comparing(WorkflowProcessEperson::getIndex));
            if(!workflowProcessEpersonOptional.isPresent()){
                throw new RuntimeException("user not  found");
            }
            WorkFlowProcessMaster workFlowProcessMaster = MASTER.getMaster(context);
            WorkFlowProcessMasterValue workFlowProcessMasterValue = this.getWorkFlowProcessMasterValueService().findByName(context, this.getAction(), workFlowProcessMaster);
            workFlowAction.setActionDate(new Date());
            workFlowAction.setAction(workFlowProcessMasterValue);
            workFlowAction.setWorkflowProcess(workflowProcess);
            return this.getWorkFlowProcessHistoryService().create(context, workFlowAction);
        }
    },
    REFER("Refer"),
    HOLD("Hold"),
    UNHOLD("UnHold");
    private String action;
    private WorkFlowProcessHistoryService workFlowProcessHistoryService;
    private WorkFlowProcessMasterValueService workFlowProcessMasterValueService;
    private WorkFlowProcessMasterService workFlowProcessMasterService;
    private  JbpmServerImpl jbpmServer;
    @Component
    public static class ServiceInjector {
        @Autowired
        private WorkFlowProcessHistoryService workFlowProcessHistoryService;
        @Autowired
        private WorkFlowProcessMasterValueService workFlowProcessMasterValueService;
        @Autowired
        private WorkFlowProcessMasterService workFlowProcessMasterService;
        @Autowired
        JbpmServerImpl jbpmServer;

        @PostConstruct
        public void postConstruct() {
            for (WorkFlowAction rt : EnumSet.allOf(WorkFlowAction.class)) {
                rt.setWorkFlowProcessHistoryService(workFlowProcessHistoryService);
                rt.setWorkFlowProcessMasterValueService(workFlowProcessMasterValueService);
                rt.setWorkFlowProcessMasterService(workFlowProcessMasterService);
                rt.setJbpmServer(jbpmServer);
            }
        }
    }
    WorkFlowAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
    public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess,WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
        WorkFlowProcessHistory workFlowAction=new WorkFlowProcessHistory();
        System.out.println("Action::::" + this.getAction() +this.getWorkFlowProcessMasterService());
        WorkFlowProcessMaster workFlowProcessMaster = MASTER.getMaster(context);
        System.out.println("workFlowProcessMaster Master Name::"+workFlowProcessMaster.getMastername());
        WorkFlowProcessMasterValue workFlowProcessMasterValue = this.getWorkFlowProcessMasterValueService().findByName(context, this.getAction(), workFlowProcessMaster);
        workFlowAction.setActionDate(new Date());
        workFlowAction.setAction(workFlowProcessMasterValue);
       return this.getWorkFlowProcessHistoryService().create(context, workFlowAction);
    }

    public WorkFlowProcessMaster getMaster(Context context) throws SQLException {
        System.out.println("Mastyer::::" + this.getAction());
        return this.getWorkFlowProcessMasterService().findByName(context, this.getAction());
    }

    public void setAction(String action) {
        this.action = action;
    }

    public WorkFlowProcessHistoryService getWorkFlowProcessHistoryService() {
        return workFlowProcessHistoryService;
    }

    public void setWorkFlowProcessHistoryService(WorkFlowProcessHistoryService workFlowProcessHistoryService) {
        this.workFlowProcessHistoryService = workFlowProcessHistoryService;
    }

    public WorkFlowProcessMasterValueService getWorkFlowProcessMasterValueService() {
        return workFlowProcessMasterValueService;
    }

    public void setWorkFlowProcessMasterValueService(WorkFlowProcessMasterValueService workFlowProcessMasterValueService) {
        this.workFlowProcessMasterValueService = workFlowProcessMasterValueService;
    }

    public  WorkFlowProcessMasterService getWorkFlowProcessMasterService() {
        return workFlowProcessMasterService;
    }

    public  void setWorkFlowProcessMasterService(WorkFlowProcessMasterService workFlowProcessMasterService) {
        this.workFlowProcessMasterService = workFlowProcessMasterService;
    }

    public JbpmServerImpl getJbpmServer() {
        return jbpmServer;
    }

    public void setJbpmServer(JbpmServerImpl jbpmServer) {
        this.jbpmServer = jbpmServer;
    }
}
