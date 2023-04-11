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

public enum WorkFlowAction {
    MASTER("Action"),
    CREATE("Create") {
        @Override
        public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess, WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
            String jbpmResponce = this.getJbpmServer().startProcess(workFlowProcessRest);
            JBPMResponse jbpmResponse = this.getModelMapper().map(jbpmResponce, JBPMResponse.class);
            WorkFlowProcessHistory workFlowAction = new WorkFlowProcessHistory();
            Optional<WorkflowProcessEperson> workflowProcessEpersonOptional = workflowProcess.getWorkflowProcessEpeople().stream().filter(d -> d.getUsertype() != null)
                    .filter(s -> s.getUsertype().getPrimaryvalue()
                    .equals(WorkFlowUserType.INITIATOR.getAction())).findFirst();
            if (!workflowProcessEpersonOptional.isPresent()) {
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
    FORWARD("Forward") {
        @Override
        public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess, WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
            String forwardResponce = this.getJbpmServer().forwardTask(workFlowProcessRest);
            System.out.println("jbpmResponce create" + forwardResponce);
            JBPMResponse_ jbpmResponse = new Gson().fromJson(forwardResponce, JBPMResponse_.class);
            System.out.println("jbpmResponse in GSON::"+new Gson().toJson(jbpmResponse));
            WorkFlowProcessHistory workFlowAction = new WorkFlowProcessHistory();
            Optional<WorkflowProcessEperson> workflowProcessEpersonOptional = workflowProcess.getWorkflowProcessEpeople()
                    .stream()
                    .peek(d-> System.out.println("responce::"+jbpmResponse.getPerformed_by() + "d.getID().toString() "+d.getID().toString()))
                    .filter(d -> d.getID().toString()
                    .equals(jbpmResponse.getPerformed_by())).findFirst();
            if (!workflowProcessEpersonOptional.isPresent()) {
                throw new RuntimeException("user not  found");
            }
            WorkflowProcessEperson currentOwner = workflowProcess.getWorkflowProcessEpeople().stream().filter(we->we.getID().equals(UUID.fromString(jbpmResponse.getPerformed_by()))).findFirst().get();
            currentOwner.setOwner(false);
            WorkflowProcessEperson workflowProcessEpersonOwner= workflowProcess.getWorkflowProcessEpeople().stream().filter(we->we.getID().equals(UUID.fromString(jbpmResponse.getNext_user()))).findFirst().get();
            workflowProcessEpersonOwner.setOwner(true);
            getWorkflowProcessEpersonService().update(context, workflowProcessEpersonOwner);
            getWorkflowProcessEpersonService().update(context, currentOwner);
            WorkFlowProcessMaster workFlowProcessMaster = MASTER.getMaster(context);
            WorkFlowProcessMasterValue workFlowProcessMasterValue = this.getWorkFlowProcessMasterValueService().findByName(context, this.getAction(), workFlowProcessMaster);
            workFlowAction.setActionDate(new Date());
            workFlowAction.setAction(workFlowProcessMasterValue);
            workFlowAction.setWorkflowProcess(workflowProcess);
            return this.getWorkFlowProcessHistoryService().create(context, workFlowAction);
        }
    },
    BACKWARD("Backward") {
        @Override
        public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess, WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
            String forwardResponce = this.getJbpmServer().backwardTask(workFlowProcessRest);
            System.out.println("jbpmResponce create" + forwardResponce);
            JBPMResponse jbpmResponse = getModelMapper().map(forwardResponce, JBPMResponse.class);
            WorkFlowProcessHistory workFlowAction = new WorkFlowProcessHistory();
            Optional<WorkflowProcessEperson> workflowProcessEpersonOptional = workflowProcess.getWorkflowProcessEpeople().stream().filter(d -> d.getID().toString().equals(jbpmResponse.getPerformed_by())).findFirst();
            if (!workflowProcessEpersonOptional.isPresent()) {
                throw new RuntimeException("user not  found");
            }
            WorkflowProcessEperson currentOwner = workflowProcess.getWorkflowProcessEpeople().stream().filter(we->we.getID().equals(UUID.fromString(jbpmResponse.getPerformed_by()))).findFirst().get();
            currentOwner.setOwner(false);
            WorkflowProcessEperson workflowProcessEpersonOwner= workflowProcess.getWorkflowProcessEpeople().stream().filter(we->we.getID().equals(UUID.fromString(jbpmResponse.getNext_user()))).findFirst().get();
            workflowProcessEpersonOwner.setOwner(true);
            getWorkflowProcessEpersonService().update(context, workflowProcessEpersonOwner);
            getWorkflowProcessEpersonService().update(context, currentOwner);
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

    public String getAction() {
        return action;
    }

    public WorkFlowProcessHistory perfomeAction(Context context, WorkflowProcess workflowProcess, WorkFlowProcessRest workFlowProcessRest) throws SQLException, AuthorizeException {
        WorkFlowProcessHistory workFlowAction = new WorkFlowProcessHistory();
        System.out.println("Action::::" + this.getAction() + this.getWorkFlowProcessMasterService());
        WorkFlowProcessMaster workFlowProcessMaster = MASTER.getMaster(context);
        System.out.println("workFlowProcessMaster Master Name::" + workFlowProcessMaster.getMastername());
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
