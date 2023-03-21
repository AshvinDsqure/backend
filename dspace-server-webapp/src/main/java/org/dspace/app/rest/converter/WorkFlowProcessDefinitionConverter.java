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
import org.dspace.content.WorkflowProcessDefinition;
import org.dspace.content.service.BitstreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This is the converter from/to the EPerson in the DSpace API data model and the
 * REST data model
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@Component
public class WorkFlowProcessDefinitionConverter extends DSpaceObjectConverter<WorkflowProcessDefinition, WorkFlowProcessDefinitionRest> {
    @Autowired
    WorkFlowProcessDefinitionEpersonConverter workFlowProcessDefinitionEpersonConverter;
    @Autowired
    BitstreamService bitstreamService;
    @Override
    public WorkFlowProcessDefinitionRest convert(WorkflowProcessDefinition obj, Projection projection) {
        WorkFlowProcessDefinitionRest workflowProcessDefinitionRest = super.convert(obj, projection);
        obj.getWorkflowProcessDefinitionEpeople().forEach(workflowProcessDefinitionEperson->{
            WorkflowProcessDefinitionEpersonRest workflowProcessDefinitionEpersonRest=workFlowProcessDefinitionEpersonConverter.convert(workflowProcessDefinitionEperson,projection);
            workflowProcessDefinitionRest.getWorkflowProcessDefinitionEpersonRests().add(workflowProcessDefinitionEpersonRest);
        });
        return workflowProcessDefinitionRest;
    }

    @Override
    protected WorkFlowProcessDefinitionRest newInstance() {
        return new WorkFlowProcessDefinitionRest();
    }

    @Override
    public Class<WorkflowProcessDefinition> getModelClass() {
        return WorkflowProcessDefinition.class;
    }

}
