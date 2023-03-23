/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.WorkFlowProcessInwardDetailsRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.WorkFlowProcessInwardDetails;
import org.springframework.stereotype.Component;

@Component
public class WorkFlowProcessInwardDetailsConverter extends DSpaceObjectConverter<WorkFlowProcessInwardDetails, WorkFlowProcessInwardDetailsRest> {

    @Override
    public Class<WorkFlowProcessInwardDetails> getModelClass() {
        return WorkFlowProcessInwardDetails.class;
    }

    @Override
    protected WorkFlowProcessInwardDetailsRest newInstance() {
        return new WorkFlowProcessInwardDetailsRest();
    }


    @Override
    public WorkFlowProcessInwardDetailsRest convert(WorkFlowProcessInwardDetails obj, Projection projection) {
        WorkFlowProcessInwardDetailsRest rest = new WorkFlowProcessInwardDetailsRest();
        rest.setUuid(obj.getID().toString());
        rest.setInwardNumber(obj.getInwardNumber());
        rest.setInwardDate(obj.getInwardDate());
        rest.setReceivedDate(obj.getReceivedDate());
        return rest;
    }

    public WorkFlowProcessInwardDetails convert(WorkFlowProcessInwardDetails obj, WorkFlowProcessInwardDetailsRest rest) {
        obj.setInwardNumber(rest.getInwardNumber());
        obj.setInwardDate(rest.getInwardDate());
        obj.setReceivedDate(rest.getReceivedDate());
        return obj;
    }

}
