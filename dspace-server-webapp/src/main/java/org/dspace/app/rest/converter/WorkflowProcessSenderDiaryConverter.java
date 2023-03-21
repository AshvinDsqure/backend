/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.WorkFlowProcessDefinitionRest;
import org.dspace.app.rest.model.WorkflowItemRest;
import org.dspace.app.rest.model.WorkflowProcessSenderDiaryRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.WorkflowProcessDefinition;
import org.dspace.content.WorkflowProcessSenderDiary;
import org.dspace.xmlworkflow.storedcomponents.XmlWorkflowItem;


public class WorkflowProcessSenderDiaryConverter extends DSpaceObjectConverter<WorkflowProcessSenderDiary, WorkflowProcessSenderDiaryRest> {

    @Override
    public Class<WorkflowProcessSenderDiary> getModelClass() {

        return WorkflowProcessSenderDiary.class;
    }

    @Override
    protected WorkflowProcessSenderDiaryRest newInstance() {
        return new WorkflowProcessSenderDiaryRest();
    }

    @Override
    public WorkflowProcessSenderDiaryRest convert(WorkflowProcessSenderDiary obj, Projection projection) {
        WorkflowProcessSenderDiaryRest rest = new WorkflowProcessSenderDiaryRest();
        rest.setCity(obj.getCity());
        rest.setCountry(obj.getCountry());
        rest.setOrganization(obj.getOrganization());
        rest.setLegacyId(obj.getLegacyId());
        rest.setName(obj.getName());
        rest.setDesignation(obj.getDesignation());
        rest.setContactNumber(obj.getContactNumber());
        rest.setEmail(obj.getEmail());
        rest.setAddress(obj.getAddress());

        return rest;
    }


}
