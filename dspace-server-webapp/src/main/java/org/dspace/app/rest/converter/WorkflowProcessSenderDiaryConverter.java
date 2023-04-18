/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import com.google.gson.Gson;
import org.dspace.app.rest.model.WorkFlowProcessDefinitionRest;
import org.dspace.app.rest.model.WorkflowItemRest;
import org.dspace.app.rest.model.WorkflowProcessSenderDiaryRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.WorkflowProcessDefinition;
import org.dspace.content.WorkflowProcessSenderDiary;
import org.dspace.xmlworkflow.storedcomponents.XmlWorkflowItem;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class WorkflowProcessSenderDiaryConverter extends DSpaceObjectConverter<WorkflowProcessSenderDiary, WorkflowProcessSenderDiaryRest> {

    @Override
    public Class<WorkflowProcessSenderDiary> getModelClass() {

        return WorkflowProcessSenderDiary.class;
    }

    @Autowired
    ModelMapper modelMapper;


    @Override
    protected WorkflowProcessSenderDiaryRest newInstance() {
        return new WorkflowProcessSenderDiaryRest();
    }

    @Override
    public WorkflowProcessSenderDiaryRest convert(WorkflowProcessSenderDiary obj, Projection projection) {
        WorkflowProcessSenderDiaryRest rest = new WorkflowProcessSenderDiaryRest();
        rest = modelMapper.map(obj, WorkflowProcessSenderDiaryRest.class);
        rest.setUuid(obj.getID().toString());
        return rest;
    }
    public WorkflowProcessSenderDiary convert(WorkflowProcessSenderDiary obj, WorkflowProcessSenderDiaryRest rest) {
        obj = modelMapper.map(rest, WorkflowProcessSenderDiary.class);
        return obj;
    }
    public WorkflowProcessSenderDiary convert(WorkflowProcessSenderDiaryRest rest) {
        WorkflowProcessSenderDiary obj =null;
        if(rest != null) {
            obj = new WorkflowProcessSenderDiary();
            obj = modelMapper.map(rest, WorkflowProcessSenderDiary.class);
        }
        return obj;
    }

}
