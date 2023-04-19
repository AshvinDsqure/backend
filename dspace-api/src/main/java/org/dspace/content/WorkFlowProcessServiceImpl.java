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
import org.dspace.content.service.*;
import org.dspace.core.Constants;
import org.dspace.core.Context;

import org.dspace.event.Event;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Service implementation for the Item object.
 * This class is responsible for all business logic calls for the Item object and is autowired by spring.
 * This class should never be accessed directly.
 *
 * @author kevinvandevelde at atmire.com
 */
public class WorkFlowProcessServiceImpl extends DSpaceObjectServiceImpl<WorkflowProcess> implements WorkflowProcessService {

    /**
     * log4j category
     */
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(Item.class);

    @Autowired(required = true)
    protected WorkflowProcessDAO workflowProcessDAO;

    protected WorkFlowProcessMasterValueService workFlowProcessMasterValueService;

    protected WorkFlowProcessMasterService workFlowProcessMasterServicee;

    protected WorkFlowProcessServiceImpl() {
        super();
    }

    @Override
    public WorkflowProcess findByIdOrLegacyId(Context context, String id) throws SQLException {
        return null;
    }

    @Override
    public WorkflowProcess findByLegacyId(Context context, int id) throws SQLException {
        return null;
    }

    @Override
    public WorkflowProcess find(Context context, UUID uuid) throws SQLException {
        return workflowProcessDAO.findByID(context,WorkflowProcess.class,uuid);
    }

    @Override
    public void updateLastModified(Context context, WorkflowProcess dso) throws SQLException, AuthorizeException {
        update(context, dso);
        //Also fire a modified event since the item HAS been modified
        context.addEvent(new org.dspace.event.Event(Event.MODIFY, Constants.ITEM, dso.getID(), null, getIdentifiers(context, dso)));
    }

    @Override
    public void delete(Context context, WorkflowProcess dso) throws SQLException, AuthorizeException, IOException {

    }

    @Override
    public int getSupportsTypeConstant() {
        return 0;
    }

    @Override
    public WorkflowProcess create(Context context, WorkflowProcess workflowProcess) throws SQLException, AuthorizeException {
        workflowProcess= workflowProcessDAO.create(context,workflowProcess);
        return workflowProcess;
    }

    @Override
    public List<WorkflowProcess> findAll(Context context) throws SQLException {
        return Optional.ofNullable(workflowProcessDAO.findAll(context,WorkflowProcess.class)).orElse(new ArrayList<>());
    }

    @Override
    public List<WorkflowProcess> findAll(Context context, Integer limit, Integer offset) throws SQLException {
        return  Optional.ofNullable(workflowProcessDAO.findAll(context,WorkflowProcess.class,limit,
                offset)).orElse(new ArrayList<>());
    }

    @Override
    public List<WorkflowProcess> findNotCompletedByUser(Context context, UUID eperson,UUID statusid, Integer offset, Integer limit) throws SQLException {
       return workflowProcessDAO.findNotCompletedByUser(context,eperson,statusid,offset,limit);
    }

    @Override
    public int countfindNotCompletedByUser(Context context, UUID eperson,UUID statusid) throws SQLException {
        return workflowProcessDAO.countfindNotCompletedByUser(context,eperson,statusid);
    }
}
