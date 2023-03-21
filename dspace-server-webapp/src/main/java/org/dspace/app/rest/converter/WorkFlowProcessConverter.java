/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.EPersonRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.model.WorkFlowProcessRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.Bitstream;
import org.dspace.content.WorkflowProcess;
import org.dspace.content.WorkflowProcessReferenceDoc;
import org.dspace.content.enums.Dispatch;
import org.dspace.content.enums.Priority;
import org.dspace.content.enums.WorkFlowProcessReferenceDocType;
import org.dspace.content.service.BitstreamService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
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
public class WorkFlowProcessConverter extends DSpaceObjectConverter<WorkflowProcess, WorkFlowProcessRest> {
    @Autowired
    ItemConverter itemConverter;
    @Autowired
    EPersonConverter ePersonConverter;
    @Autowired
    BitstreamService bitstreamService;
    @Override
    public WorkFlowProcessRest convert(WorkflowProcess obj, Projection projection) {
        WorkFlowProcessRest workFlowProcessRest = super.convert(obj, projection);
        if(obj.getPriority()!=null)
        workFlowProcessRest.setPriority(obj.getPriority().toString());
        workFlowProcessRest.setSubject(obj.getSubject());
        workFlowProcessRest.setInitDate(obj.getInitDate());
        if(obj.getItem() != null){
           ItemRest itemRest= itemConverter.convert(obj.getItem(),projection);
            workFlowProcessRest.setItem(itemRest);
        }
        if(obj.getSubmitter() != null) {
           EPersonRest ePersonRest= ePersonConverter.convert(obj.getSubmitter(),projection);
            workFlowProcessRest.setSubmitter(ePersonRest);
        }
        return workFlowProcessRest;
    }
    public WorkflowProcess convert(WorkFlowProcessRest obj, Context context) throws  Exception {
        WorkflowProcess workflowProcess=new WorkflowProcess();
        workflowProcess.setSubject(obj.getSubject());
        workflowProcess.setAssignDueDate(obj.getAssignDueDate());
        workflowProcess.setInitDate(obj.getInitDate());
        workflowProcess.setPriority(Priority.HIGH);
        workflowProcess.setDispatchmode(Dispatch.ELECTRIC);
        System.out.println("obj.getWorkflowProcessReferenceDocRests()::"+obj.getWorkflowProcessReferenceDocRests().size());
        obj.getWorkflowProcessReferenceDocRests().forEach(workflowProcessReferenceDocRest -> {
            WorkflowProcessReferenceDoc workflowProcessReferenceDoc=new WorkflowProcessReferenceDoc();
            try {
                workflowProcessReferenceDoc.setWorkflowProcess(workflowProcess);
              Bitstream bitstream= Optional.ofNullable(bitstreamService.find(context, UUID.fromString(workflowProcessReferenceDocRest.getBitstreamRest().getId()))).orElse(null);
              workflowProcessReferenceDoc.setBitstream(bitstream);
              workflowProcessReferenceDoc.setWorkFlowProcessReferenceDocType(WorkFlowProcessReferenceDocType.NOTE);
              workflowProcess.getWorkflowProcessReferenceDocs().add(workflowProcessReferenceDoc);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
        return  workflowProcess;

    }
    @Override
    protected WorkFlowProcessRest newInstance() {
        return new WorkFlowProcessRest();
    }

    @Override
    public Class<WorkflowProcess> getModelClass() {
        return WorkflowProcess.class;
    }

}
