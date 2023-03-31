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
import org.dspace.content.WorkFlowProcessMasterValue;
import org.dspace.content.WorkflowProcessDefinition;
import org.dspace.content.WorkflowProcessReferenceDoc;
import org.dspace.content.service.BitstreamService;
import org.dspace.core.Context;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.UUID;

/**
 * This is the converter from/to the EPerson in the DSpace API data model and the
 * REST data model
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@Component
public class WorkflowProcessReferenceDocConverter extends DSpaceObjectConverter<WorkflowProcessReferenceDoc, WorkflowProcessReferenceDocRest> {

    @Autowired
    BitstreamService bitstreamService;
    @Autowired
    BitstreamConverter bitstreamConverter;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public WorkflowProcessReferenceDocRest convert(WorkflowProcessReferenceDoc obj, Projection projection) {
        WorkflowProcessReferenceDocRest workflowProcessDefinitionRest = super.convert(obj, projection);
        return workflowProcessDefinitionRest;
    }
    public WorkflowProcessReferenceDocRest convertForWorkFlow(WorkflowProcessReferenceDoc obj, Projection projection) {
        WorkflowProcessReferenceDocRest workflowProcessDefinitionRest = super.convert(obj, projection);
        if(obj.getBitstream()!= null){
           BitstreamRest bitstreamRest= bitstreamConverter.convertFoWorkFLowRefDoc(obj.getBitstream(),projection);
            workflowProcessDefinitionRest.setBitstreamRest(bitstreamRest);
        }
        return workflowProcessDefinitionRest;
    }

    @Override
    protected WorkflowProcessReferenceDocRest newInstance() {
        return new WorkflowProcessReferenceDocRest();
    }

    @Override
    public Class<WorkflowProcessReferenceDoc> getModelClass() {
        return WorkflowProcessReferenceDoc.class;
    }
    public WorkflowProcessReferenceDoc convert(Context context, WorkflowProcessReferenceDocRest rest) throws SQLException {
        WorkflowProcessReferenceDoc workflowProcessReferenceDoc= modelMapper.map(rest,WorkflowProcessReferenceDoc.class) ;
        workflowProcessReferenceDoc.setBitstream(bitstreamService.find(context, UUID.fromString(rest.getBitstreamRest().getId())));
        return  workflowProcessReferenceDoc;
    }

}
