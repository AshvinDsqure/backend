/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.WorkFlowProcessDraftDetailsRest;
import org.dspace.app.rest.model.WorkFlowProcessInwardDetailsRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.WorkFlowProcessDraftDetails;
import org.dspace.content.WorkFlowProcessInwardDetails;
import org.dspace.content.WorkFlowProcessMasterValue;
import org.dspace.core.Context;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class WorkFlowProcessDraftDetailsConverter extends DSpaceObjectConverter<WorkFlowProcessDraftDetails, WorkFlowProcessDraftDetailsRest> {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EPersonConverter ePersonConverter;
    @Autowired
    WorkFlowProcessMasterValueConverter workFlowProcessMasterValueConverter;
    @Override
    public Class<WorkFlowProcessDraftDetails> getModelClass() {
        return WorkFlowProcessDraftDetails.class;
    }
    @Override
    protected WorkFlowProcessDraftDetailsRest newInstance() {
        return new WorkFlowProcessDraftDetailsRest();
    }
    @Override
    public WorkFlowProcessDraftDetailsRest convert(WorkFlowProcessDraftDetails obj, Projection projection) {
        WorkFlowProcessDraftDetailsRest rest = new WorkFlowProcessDraftDetailsRest();
        if (obj.getDrafttype() != null) {
            rest.setDrafttypeRest(workFlowProcessMasterValueConverter.convert(obj.getDrafttype(), projection));
        }
        if (obj.getDocumentsignator() != null) {
            rest.setDocumentsignatorRest(ePersonConverter.convert(obj.getDocumentsignator(), projection));
        }
        rest.setDraftdate(obj.getDraftdate());
        rest.setUuid(obj.getID().toString());
        return rest;
    }
    public WorkFlowProcessDraftDetails convert(Context context, WorkFlowProcessDraftDetailsRest rest) throws SQLException {
        WorkFlowProcessDraftDetails obj = new WorkFlowProcessDraftDetails();
        if (rest.getDrafttypeRest() != null) {
            obj.setDrafttype(workFlowProcessMasterValueConverter.convert(context, rest.getDrafttypeRest()));
        }
        if (rest.getDocumentsignatorRest() != null && rest.getDocumentsignatorRest().getUuid()!=null && !rest.getDocumentsignatorRest().getUuid().toString().isEmpty()) {
            obj.setDocumentsignator(ePersonConverter.convert(context, rest.getDocumentsignatorRest()));
        }
        obj.setDraftdate(rest.getDraftdate());
        return obj;
    }
    public WorkFlowProcessDraftDetails convert(WorkFlowProcessDraftDetailsRest rest) {
        if(rest!= null) {
            return modelMapper.map(rest, WorkFlowProcessDraftDetails.class);
        }
        return  null;
    }
}
