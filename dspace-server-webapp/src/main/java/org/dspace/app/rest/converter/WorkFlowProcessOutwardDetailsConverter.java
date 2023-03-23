/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.WorkFlowProcessOutwardDetailsRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.WorkFlowProcessOutwardDetails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkFlowProcessOutwardDetailsConverter extends DSpaceObjectConverter<WorkFlowProcessOutwardDetails, WorkFlowProcessOutwardDetailsRest> {


    @Autowired
    private  ModelMapper modelMapper;
    @Override
    public Class<WorkFlowProcessOutwardDetails> getModelClass() {
        return WorkFlowProcessOutwardDetails.class;
    }

    @Override
    protected WorkFlowProcessOutwardDetailsRest newInstance() {
        return new WorkFlowProcessOutwardDetailsRest();
    }


    @Override
    public WorkFlowProcessOutwardDetailsRest convert(WorkFlowProcessOutwardDetails obj, Projection projection) {
        WorkFlowProcessOutwardDetailsRest rest = new WorkFlowProcessOutwardDetailsRest();
        rest=modelMapper.map(obj,WorkFlowProcessOutwardDetailsRest.class);
        rest.setUuid(obj.getID().toString());
        return rest;
    }

    public WorkFlowProcessOutwardDetails convert(WorkFlowProcessOutwardDetails obj, WorkFlowProcessOutwardDetailsRest rest) {
        obj=modelMapper.map(rest,WorkFlowProcessOutwardDetails.class);
        return obj;
    }

}
