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
import org.dspace.content.enums.Priority;
import org.dspace.content.enums.WorkFlowProcessReferenceDocType;
import org.dspace.content.service.BitstreamService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * This is the converter from/to the EPerson in the DSpace API data model and the
 * REST data model
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@Component
public class WorkFlowProcessDefinitionEpersonConverter extends DSpaceObjectConverter<WorkflowProcessDefinitionEperson, WorkflowProcessDefinitionEpersonRest> {
    @Autowired
    EPersonConverter ePersonConverter;
    @Autowired
    BitstreamService bitstreamService;
    @Override
    public WorkflowProcessDefinitionEpersonRest convert(WorkflowProcessDefinitionEperson obj, Projection projection) {
        WorkflowProcessDefinitionEpersonRest workflowProcessDefinitionEpersonRest = super.convert(obj, projection);
        if(obj.getePerson()!= null){
        workflowProcessDefinitionEpersonRest.setePersonRest(ePersonConverter.convert(obj.getePerson(),projection));
        }
        return workflowProcessDefinitionEpersonRest;
    }
    @Override
    protected WorkflowProcessDefinitionEpersonRest newInstance() {
        return new WorkflowProcessDefinitionEpersonRest();
    }

    @Override
    public Class<WorkflowProcessDefinitionEperson> getModelClass() {
        return WorkflowProcessDefinitionEperson.class;
    }

}
