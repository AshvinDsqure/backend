/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.WorkFlowProcessMasterValueRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.WorkFlowProcessMasterValue;
import org.springframework.stereotype.Component;

@Component
public class WorkFlowProcessMasterValueConverter extends DSpaceObjectConverter<WorkFlowProcessMasterValue, WorkFlowProcessMasterValueRest> {

    @Override
    public Class<WorkFlowProcessMasterValue> getModelClass() {
        return WorkFlowProcessMasterValue.class;
    }

    @Override
    protected WorkFlowProcessMasterValueRest newInstance() {
        return new WorkFlowProcessMasterValueRest();
    }


    @Override
    public WorkFlowProcessMasterValueRest convert(WorkFlowProcessMasterValue obj, Projection projection) {
        WorkFlowProcessMasterValueRest rest = new WorkFlowProcessMasterValueRest();
        rest.setPrimaryvalue(obj.getPrimaryvalue());
        rest.setSecondaryvalue(obj.getSecondaryvalue());
        rest.setLegacyId(obj.getLegacyId());
        rest.setUuid(obj.getID().toString());
        return rest;
    }

    public WorkFlowProcessMasterValue convert(WorkFlowProcessMasterValue obj, WorkFlowProcessMasterValueRest rest) {
        obj.setPrimaryvalue(rest.getPrimaryvalue());
        obj.setSecondaryvalue(rest.getSecondaryvalue());
        obj.setLegacyId(rest.getWorkFlowProcessMaster().getLegacyId());
        obj.setWorkflowprocessmaster(rest.getWorkFlowProcessMaster());
        return obj;
    }

}
