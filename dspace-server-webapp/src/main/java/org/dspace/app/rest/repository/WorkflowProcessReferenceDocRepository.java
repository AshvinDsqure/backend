/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.converter.WorkFlowProcessConverter;
import org.dspace.app.rest.converter.WorkFlowProcessMasterValueConverter;
import org.dspace.app.rest.enums.WorkFlowAction;
import org.dspace.app.rest.enums.WorkFlowUserType;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.jbpm.JbpmServerImpl;
import org.dspace.app.rest.model.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.*;
import org.dspace.content.service.WorkflowProcessReferenceDocService;
import org.dspace.content.service.WorkflowProcessSenderDiaryService;
import org.dspace.content.service.WorkflowProcessService;
import org.dspace.core.Context;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This is the repository responsible to manage Item Rest object
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */

@Component(WorkflowProcessReferenceDocRest.CATEGORY + "." + WorkflowProcessReferenceDocRest.NAME)
public class WorkflowProcessReferenceDocRepository extends DSpaceObjectRestRepository<WorkflowProcessReferenceDoc, WorkFlowProcessRest> {

    private static final Logger log = LogManager.getLogger(WorkflowProcessReferenceDocRepository.class);
    @Autowired
    private WorkFlowProcessMasterValueConverter workFlowProcessMasterValueConverter;
    @Autowired
    private WorkflowProcessReferenceDocService workflowProcessReferenceDocService;

    @Autowired
    ModelMapper modelMapper ;
    public WorkflowProcessReferenceDocRepository(WorkflowProcessReferenceDocService dsoService) {
        super(dsoService);
    }
    @Override
    @PreAuthorize("hasPermission(#id, 'WORKSPACEITEM', 'WRITE')")
    public WorkFlowProcessRest findOne(Context context, UUID id) throws SQLException {
        WorkflowProcessReferenceDoc workflowProcessReferenceDoc= workflowProcessReferenceDocService.find(context,id);
        return converter.toRest(workflowProcessReferenceDoc, utils.obtainProjection());
    }

    @Override
    @PreAuthorize("hasPermission(#id, 'WORKSPACEITEM', 'WRITE')")
    public Page<WorkFlowProcessRest> findAll(Context context, Pageable pageable) {
        try {
            List<WorkflowProcessReferenceDoc> workflowProcessReferenceDocs= workflowProcessReferenceDocService.findAll(context,pageable.getPageSize(),Math.toIntExact(pageable.getOffset()));
            return converter.toRestPage(workflowProcessReferenceDocs, pageable, 0, utils.obtainProjection());
        }catch (Exception e){
            throw  new RuntimeException(e.getMessage(),e);
        }
    }
    @Override
    public Class<WorkFlowProcessRest> getDomainClass() {
        return null;
    }
    @Override
    @PreAuthorize("hasPermission(#id, 'WORKSPACEITEM', 'WRITE')")
    protected WorkFlowProcessRest createAndReturn(Context context)
            throws AuthorizeException {
        return  null;
    }
    @Override
    @PreAuthorize("hasPermission(#id, 'WORKSPACEITEM', 'WRITE')")

    protected void delete(Context context, UUID id) throws AuthorizeException {

        WorkflowProcessReferenceDoc workflowProcessReferenceDoc = null;
        try {
            workflowProcessReferenceDoc = workflowProcessReferenceDocService.find(context, id);
            if (workflowProcessReferenceDoc == null) {
                throw new ResourceNotFoundException(WorkflowProcessReferenceDocRest.CATEGORY + "." + WorkflowProcessReferenceDocRest.NAME +
                        " with id: " + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            workflowProcessReferenceDocService.delete(context, workflowProcessReferenceDoc);
            context.commit();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
