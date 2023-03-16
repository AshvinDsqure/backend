/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.dao.WorkflowProcessDefinitionDAO;
import org.dspace.content.dao.WorkflowProcessNoteDAO;
import org.dspace.content.service.WorkflowProcessDefinitionService;
import org.dspace.content.service.WorkflowProcessNoteService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for the Item object.
 * This class is responsible for all business logic calls for the Item object and is autowired by spring.
 * This class should never be accessed directly.
 *
 * @author kevinvandevelde at atmire.com
 */
public class WorkflowProcessNoteServiceImpl extends DSpaceObjectServiceImpl<WorkflowProcessNote> implements WorkflowProcessNoteService {

    /**
     * log4j category
     */
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(WorkflowProcessNote.class);

    @Autowired(required = true)
    protected WorkflowProcessNoteDAO workflowProcessNoteDAO;

    protected WorkflowProcessNoteServiceImpl() {
        super();
    }

    @Override
    public WorkflowProcessNote findByIdOrLegacyId(Context context, String id) throws SQLException {
        return null;
    }

    @Override
    public WorkflowProcessNote findByLegacyId(Context context, int id) throws SQLException {
        return null;
    }

    @Override
    public WorkflowProcessNote find(Context context, UUID uuid) throws SQLException {
        return null;
    }

    @Override
    public void updateLastModified(Context context, WorkflowProcessNote dso) throws SQLException, AuthorizeException {

    }

    @Override
    public void delete(Context context, WorkflowProcessNote dso) throws SQLException, AuthorizeException, IOException {

    }

    @Override
    public int getSupportsTypeConstant() {
        return 0;
    }

    @Override
    public WorkflowProcessNote create(Context context, WorkflowProcessNote workflowProcessNote) throws SQLException, AuthorizeException {
        workflowProcessNote= workflowProcessNoteDAO.create(context,workflowProcessNote);
        return workflowProcessNote;
    }

    @Override
    public List<WorkflowProcessNote> findAll(Context context) throws SQLException {
        return Optional.ofNullable(workflowProcessNoteDAO.findAll(context,WorkflowProcessNote.class)).orElse(new ArrayList<>());
    }

    @Override
    public List<WorkflowProcessNote> findAll(Context context, Integer limit, Integer offset) throws SQLException {
        return  Optional.ofNullable(workflowProcessNoteDAO.findAll(context,WorkflowProcessNote.class,limit,
                offset)).orElse(new ArrayList<>());
    }
}
