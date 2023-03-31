/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.enums;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.WorkFlowProcessHistory;
import org.dspace.content.WorkFlowProcessMaster;
import org.dspace.content.WorkFlowProcessMasterValue;
import org.dspace.content.service.WorkFlowProcessHistoryService;
import org.dspace.content.service.WorkFlowProcessMasterService;
import org.dspace.content.service.WorkFlowProcessMasterValueService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Date;
import java.util.EnumSet;

public enum WorkFlowAction {
    MASTER("Action") {

    },
    CREATE("Create"),
    FORWARD("Forward"),
    BACKWARD("Backward"),
    REFER("Refer"),
    HOLD("Hold"),
    UNHOLD("UnHold");
    private String action;
    private WorkFlowProcessHistoryService workFlowProcessHistoryService;
    private WorkFlowProcessMasterValueService workFlowProcessMasterValueService;
    private WorkFlowProcessMasterService workFlowProcessMasterService;
    @Component
    public static class ServiceInjector {
        @Autowired
        private WorkFlowProcessHistoryService workFlowProcessHistoryService;
        @Autowired
        private WorkFlowProcessMasterValueService workFlowProcessMasterValueService;
        @Autowired
        private WorkFlowProcessMasterService workFlowProcessMasterService;

        @PostConstruct
        public void postConstruct() {
            for (WorkFlowAction rt : EnumSet.allOf(WorkFlowAction.class)) {
                rt.setWorkFlowProcessHistoryService(workFlowProcessHistoryService);
                rt.setWorkFlowProcessMasterValueService(workFlowProcessMasterValueService);
                rt.setWorkFlowProcessMasterService(workFlowProcessMasterService);
            }
        }
    }
    WorkFlowAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
    public WorkFlowProcessHistory perfomeAction(Context context, WorkFlowProcessHistory workFlowAction) throws SQLException, AuthorizeException {
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
}
