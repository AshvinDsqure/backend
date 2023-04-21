/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.enums;

import com.google.gson.Gson;
import org.dspace.app.rest.jbpm.JbpmServerImpl;
import org.dspace.app.rest.jbpm.models.JBPMResponse;
import org.dspace.app.rest.jbpm.models.JBPMResponse_;
import org.dspace.app.rest.model.WorkFlowProcessRest;
import org.dspace.app.rest.model.WorkflowProcessEpersonRest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.*;
import org.dspace.content.service.WorkFlowProcessHistoryService;
import org.dspace.content.service.WorkFlowProcessMasterService;
import org.dspace.content.service.WorkFlowProcessMasterValueService;
import org.dspace.content.service.WorkflowProcessEpersonService;
import org.dspace.core.Context;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public enum WorkFlowAction {
    MASTER("Action"),
    CREATE("Create") {
        @Override
        public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess, WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
            List<String> usersUuid = this.removeInitiatorgetUserList(workFlowProcessRest);
            if (usersUuid.size() != 0) {
                String jbpmResponce = this.getJbpmServer().startProcess(workFlowProcessRest, usersUuid);
                JBPMResponse_ jbpmResponse = new Gson().fromJson(jbpmResponce, JBPMResponse_.class);
                System.out.println("jbpm responce create" + new Gson().toJson(jbpmResponse));
                WorkflowProcessEperson currentOwner =  this.changeOwnership(context,jbpmResponse,workflowProcess);
                WorkFlowProcessHistory workFlowActionInit = this.storeWorkFlowHistory(context, workflowProcess, currentOwner);
                WorkFlowProcessHistory workFlowActionForward = FORWARD.storeWorkFlowHistory(context, workflowProcess, currentOwner);
                this.getWorkFlowProcessHistoryService().create(context, workFlowActionInit);
                return this.getWorkFlowProcessHistoryService().create(context, workFlowActionForward);
            } else {
                throw new RuntimeException("initiator not  found.....");
            }
        }
    },
    FORWARD("Forward") {
        @Override
        public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess, WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
            List<String> usersUuid = this.removeInitiatorgetUserList(workFlowProcessRest);
            String forwardResponce = this.getJbpmServer().forwardTask(workFlowProcessRest,usersUuid);
            System.out.println("forward jbpm responce create" + forwardResponce);
            JBPMResponse_ jbpmResponse = new Gson().fromJson(forwardResponce, JBPMResponse_.class);
            WorkflowProcessEperson currentOwner =  this.changeOwnership(context,jbpmResponse,workflowProcess);
            WorkFlowProcessHistory workFlowAction = this.storeWorkFlowHistory(context, workflowProcess, currentOwner);
            return this.getWorkFlowProcessHistoryService().create(context, workFlowAction);
        }

    },
    BACKWARD("Backward") {
        @Override
        public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess, WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
            String forwardResponce = this.getJbpmServer().backwardTask(workFlowProcessRest);
            JBPMResponse_ jbpmResponse = new Gson().fromJson(forwardResponce, JBPMResponse_.class);
            System.out.println("jbpmResponse:: Backward"+new Gson().toJson(jbpmResponse));
            WorkflowProcessEperson currentOwner =  this.changeOwnership(context,jbpmResponse,workflowProcess);
            WorkFlowProcessHistory workFlowAction = this.storeWorkFlowHistory(context, workflowProcess, currentOwner);
            return this.getWorkFlowProcessHistoryService().create(context, workFlowAction);
        }
    },
    REFER("Refer"),
    HOLD("Hold"),
    UNHOLD("UnHold"),
    COMPLETE("Complete"){
        @Override
        public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess, WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
            String forwardResponce = this.getJbpmServer().completeTask(workFlowProcessRest,new ArrayList<>());
            JBPMResponse_ jbpmResponse = new Gson().fromJson(forwardResponce, JBPMResponse_.class);
            System.out.println("jbpmResponse:: Backward"+new Gson().toJson(jbpmResponse));
            WorkflowProcessEperson currentOwner =  this.changeOwnership(context,jbpmResponse,workflowProcess);
            WorkFlowProcessHistory workFlowAction = this.storeWorkFlowHistory(context, workflowProcess, currentOwner);
            workFlowAction.setComment(this.getComment());
            return this.getWorkFlowProcessHistoryService().create(context, workFlowAction);
        }
    },
    DISPATCH("Dispatch Ready"){
        @Override
        public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess, WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
            String forwardResponce = this.getJbpmServer().completeTask(workFlowProcessRest,new ArrayList<>());
            JBPMResponse_ jbpmResponse = new Gson().fromJson(forwardResponce, JBPMResponse_.class);
            System.out.println("jbpmResponse:: Backward"+new Gson().toJson(jbpmResponse));
            WorkflowProcessEperson currentOwner =  this.changeOwnership(context,jbpmResponse,workflowProcess);
            WorkFlowProcessHistory workFlowAction = this.storeWorkFlowHistory(context, workflowProcess, currentOwner);
            workFlowAction.setComment(this.getComment());
            return this.getWorkFlowProcessHistoryService().create(context, workFlowAction);
        }
    };

    private String action;
    private String comment;
    private WorkFlowProcessHistoryService workFlowProcessHistoryService;
    private WorkFlowProcessMasterValueService workFlowProcessMasterValueService;
    private WorkFlowProcessMasterService workFlowProcessMasterService;
    private WorkflowProcessEpersonService workflowProcessEpersonService;
    private JbpmServerImpl jbpmServer;
    ModelMapper modelMapper;

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
        @Autowired
        ModelMapper modelMapper;
        @Autowired
        private WorkflowProcessEpersonService workflowProcessEpersonService;

        @PostConstruct
        public void postConstruct() {
            for (WorkFlowAction rt : EnumSet.allOf(WorkFlowAction.class)) {
                rt.setWorkFlowProcessHistoryService(workFlowProcessHistoryService);
                rt.setWorkFlowProcessMasterValueService(workFlowProcessMasterValueService);
                rt.setWorkFlowProcessMasterService(workFlowProcessMasterService);
                rt.setJbpmServer(jbpmServer);
                rt.setModelMapper(modelMapper);
                rt.setWorkflowProcessEpersonService(workflowProcessEpersonService);
            }
        }
    }

    WorkFlowAction(String action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAction() {
        return action;
    }

    public List<String> removeInitiatorgetUserList(WorkFlowProcessRest workFlowProcessRest) {
        return workFlowProcessRest.getWorkflowProcessEpersonRests().stream()
                .filter(wei -> !wei.getUserType().getPrimaryvalue().equals(WorkFlowUserType.INITIATOR.getAction()))
                .sorted(Comparator.comparing(WorkflowProcessEpersonRest::getIndex)).map(d -> d.getUuid()).collect(Collectors.toList());
    }

    public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess, WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
        WorkFlowProcessHistory workFlowAction = new WorkFlowProcessHistory();
        System.out.println("Action::::" + this.getAction() + this.getWorkFlowProcessMasterService());
        WorkFlowProcessMaster workFlowProcessMaster = MASTER.getMaster(context);
        System.out.println("workFlowProcessMaster Master Name::" + workFlowProcessMaster.getMastername());
        WorkFlowProcessMasterValue workFlowProcessMasterValue = this.getWorkFlowProcessMasterValueService().findByName(context, this.getAction(), workFlowProcessMaster);
        workFlowAction.setActionDate(new Date());
        workFlowAction.setAction(workFlowProcessMasterValue);
        workFlowAction.setComment(this.getComment());
        return this.getWorkFlowProcessHistoryService().create(context, workFlowAction);
    }

    public WorkFlowProcessHistory storeWorkFlowHistory(Context context, WorkflowProcess workflowProcess, WorkflowProcessEperson workflowProcessEperson) throws SQLException {
        WorkFlowProcessHistory workFlowAction = new WorkFlowProcessHistory();
        WorkFlowProcessMaster workFlowProcessMaster = MASTER.getMaster(context);
        workFlowAction.setWorkflowProcessEpeople(workflowProcessEperson);
        WorkFlowProcessMasterValue workFlowProcessMasterValue = this.getWorkFlowProcessMasterValueService().findByName(context, this.getAction(), workFlowProcessMaster);
        workFlowAction.setActionDate(new Date());
        workFlowAction.setAction(workFlowProcessMasterValue);
        workFlowAction.setWorkflowProcess(workflowProcess);
        System.out.println("comment::::::" + this.getComment());
        if(this.getComment()!=null){
            workFlowAction.setComment(this.getComment());
        }
        return workFlowAction;

    }
    public WorkflowProcessEperson changeOwnership(Context context,JBPMResponse_ jbpmResponse,WorkflowProcess workflowProcess) throws SQLException, AuthorizeException {
        WorkflowProcessEperson currentOwner =null;
        if(jbpmResponse.getPerformed_by() != null) {
            currentOwner = workflowProcess.getWorkflowProcessEpeople().stream().filter(we -> we.getID().equals(UUID.fromString(jbpmResponse.getPerformed_by()))).findFirst().get();
            currentOwner.setOwner(false);
            currentOwner.setSender(true);
            this.getWorkflowProcessEpersonService().update(context, currentOwner);
        }
        if(jbpmResponse.getNext_user() != null) {
            WorkflowProcessEperson workflowProcessEpersonOwner = workflowProcess.getWorkflowProcessEpeople().stream().filter(we -> we.getID().equals(UUID.fromString(jbpmResponse.getNext_user()))).findFirst().get();
            workflowProcessEpersonOwner.setOwner(true);
            workflowProcessEpersonOwner.setSender(false);
            this.getWorkflowProcessEpersonService().update(context, workflowProcessEpersonOwner);
        }
        return  currentOwner;

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

    public WorkFlowProcessMasterService getWorkFlowProcessMasterService() {
        return workFlowProcessMasterService;
    }

    public void setWorkFlowProcessMasterService(WorkFlowProcessMasterService workFlowProcessMasterService) {
        this.workFlowProcessMasterService = workFlowProcessMasterService;
    }

    public JbpmServerImpl getJbpmServer() {
        return jbpmServer;
    }

    public void setJbpmServer(JbpmServerImpl jbpmServer) {
        this.jbpmServer = jbpmServer;
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }

    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public WorkflowProcessEpersonService getWorkflowProcessEpersonService() {
        return workflowProcessEpersonService;
    }

    public void setWorkflowProcessEpersonService(WorkflowProcessEpersonService workflowProcessEpersonService) {
        this.workflowProcessEpersonService = workflowProcessEpersonService;
    }
}
