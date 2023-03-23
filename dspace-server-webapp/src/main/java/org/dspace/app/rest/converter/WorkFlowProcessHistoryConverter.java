/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.WorkFlowProcessHistoryRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.WorkFlowProcessHistory;
import org.dspace.content.enums.WorkFlowAction;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class WorkFlowProcessHistoryConverter extends DSpaceObjectConverter<WorkFlowProcessHistory, WorkFlowProcessHistoryRest> {

    @Override
    public Class<WorkFlowProcessHistory> getModelClass() {
        return WorkFlowProcessHistory.class;
    }

    @Override
    protected WorkFlowProcessHistoryRest newInstance() {
        return new WorkFlowProcessHistoryRest();
    }


    @Override
    public WorkFlowProcessHistoryRest convert(WorkFlowProcessHistory obj, Projection projection) {
        WorkFlowProcessHistoryRest rest = new WorkFlowProcessHistoryRest();
         rest.setAction(obj.getAction().toString());
        rest.setUuid(obj.getID().toString());
        rest.setEpersonid(obj.getEpersonid());
        return rest;
    }

    public WorkFlowProcessHistory convert(WorkFlowProcessHistory obj, WorkFlowProcessHistoryRest rest) {
        obj.setAction(WorkFlowAction.valueOf(rest.getAction()));
        obj.setEpersonid(rest.getEpersonid());
        return obj;
    }

}
