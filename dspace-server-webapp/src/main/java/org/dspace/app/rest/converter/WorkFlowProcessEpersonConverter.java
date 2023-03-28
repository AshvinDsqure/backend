/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.*;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.*;
import org.dspace.content.service.BitstreamService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This is the converter from/to the EPerson in the DSpace API data model and the
 * REST data model
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@Component
public class WorkFlowProcessEpersonConverter extends DSpaceObjectConverter<WorkflowProcessEperson, WorkflowProcessEpersonRest> {
    @Autowired
    EPersonConverter ePersonConverter;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public WorkflowProcessEpersonRest convert(WorkflowProcessEperson obj, Projection projection) {
        WorkflowProcessEpersonRest workflowProcessDefinitionEpersonRest = super.convert(obj, projection);
        if(obj.getePerson()!= null){
        workflowProcessDefinitionEpersonRest.setePersonRest(ePersonConverter.convert(obj.getePerson(),projection));
        }
        return workflowProcessDefinitionEpersonRest;
    }
    @Override
    protected WorkflowProcessEpersonRest newInstance() {
        return new WorkflowProcessEpersonRest();
    }

    @Override
    public Class<WorkflowProcessEperson> getModelClass() {
        return WorkflowProcessEperson.class;
    }
    public WorkflowProcessEperson convert(WorkflowProcessEpersonRest rest) {
        return modelMapper.map(rest,WorkflowProcessEperson.class) ;
    }

}