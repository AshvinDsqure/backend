/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.WorkFlowProcessMasterRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.WorkFlowProcessMaster;
import org.springframework.stereotype.Component;

@Component
public class WorkFlowProcessMasterConverter extends DSpaceObjectConverter<WorkFlowProcessMaster, WorkFlowProcessMasterRest> {

    @Override
    public Class<WorkFlowProcessMaster> getModelClass() {
        return WorkFlowProcessMaster.class;
    }

    @Override
    protected WorkFlowProcessMasterRest newInstance() {
        return new WorkFlowProcessMasterRest();
    }


    @Override
    public WorkFlowProcessMasterRest convert(WorkFlowProcessMaster obj, Projection projection) {
        WorkFlowProcessMasterRest rest = new WorkFlowProcessMasterRest();
        rest.setMastername(obj.getMastername());
        rest.setUuid(obj.getID().toString());
        return rest;
    }

    public WorkFlowProcessMaster convert(WorkFlowProcessMaster workFlowProcessMaster, WorkFlowProcessMasterRest workFlowProcessMasterRest) {
        workFlowProcessMaster.setMastername(workFlowProcessMasterRest.getMastername());
        return workFlowProcessMaster;
    }
}
