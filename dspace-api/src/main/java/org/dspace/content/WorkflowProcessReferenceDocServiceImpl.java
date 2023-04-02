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
import org.dspace.content.dao.WorkflowProcessDAO;
import org.dspace.content.dao.WorkflowProcessReferenceDocDAO;
import org.dspace.content.service.WorkflowProcessReferenceDocService;
import org.dspace.content.service.WorkflowProcessService;
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
public class WorkflowProcessReferenceDocServiceImpl extends DSpaceObjectServiceImpl<WorkflowProcessReferenceDoc> implements WorkflowProcessReferenceDocService {

    /**
     * log4j category
     */
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(Item.class);

    @Autowired(required = true)
    protected WorkflowProcessReferenceDocDAO workflowProcessReferenceDocDAO;

    protected WorkflowProcessReferenceDocServiceImpl() {
        super();
    }

    @Override
    public WorkflowProcessReferenceDoc findByIdOrLegacyId(Context context, String id) throws SQLException {
        return null;
    }

    @Override
    public WorkflowProcessReferenceDoc findByLegacyId(Context context, int id) throws SQLException {
        return null;
    }

    @Override
    public WorkflowProcessReferenceDoc find(Context context, UUID uuid) throws SQLException {
        return workflowProcessReferenceDocDAO.findByID(context,WorkflowProcessReferenceDoc.class,uuid);
    }

    @Override
    public void updateLastModified(Context context, WorkflowProcessReferenceDoc dso) throws SQLException, AuthorizeException {

    }

    @Override
    public void delete(Context context, WorkflowProcessReferenceDoc dso) throws SQLException, AuthorizeException, IOException {
         workflowProcessReferenceDocDAO.delete(context,dso);
    }

    @Override
    public int getSupportsTypeConstant() {
        return 0;
    }

    @Override
    public WorkflowProcessReferenceDoc create(Context context, WorkflowProcessReferenceDoc workflowProcessReferenceDoc) throws SQLException, AuthorizeException {
        return  workflowProcessReferenceDocDAO.create(context,workflowProcessReferenceDoc);

    }

    @Override
    public List<WorkflowProcessReferenceDoc> findAll(Context context) throws SQLException {
        return Optional.ofNullable(workflowProcessReferenceDocDAO.findAll(context,WorkflowProcessReferenceDoc.class)).orElse(new ArrayList<>());
    }

    @Override
    public List<WorkflowProcessReferenceDoc> findAll(Context context, Integer limit, Integer offset) throws SQLException {
        return  Optional.ofNullable(workflowProcessReferenceDocDAO.findAll(context,WorkflowProcessReferenceDoc.class,limit,
                offset)).orElse(new ArrayList<>());
    }
}
