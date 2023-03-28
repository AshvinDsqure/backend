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
import org.dspace.content.dao.WorkFlowProcessMasterValueDAO;
import org.dspace.content.service.WorkFlowProcessMasterService;
import org.dspace.content.service.WorkFlowProcessMasterValueService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorkFlowProcessMasterValueServiceImpl extends DSpaceObjectServiceImpl<WorkFlowProcessMasterValue> implements WorkFlowProcessMasterValueService{


    /**
     * log4j category
     */
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(Item.class);

    @Autowired
    public WorkFlowProcessMasterService workFlowProcessMasterService;
    @Autowired(required = true)
    protected WorkFlowProcessMasterValueDAO workFlowProcessMasterValueDAO;

    protected WorkFlowProcessMasterValueServiceImpl() {
        super();
    }

    @Override
    public WorkFlowProcessMasterValue findByIdOrLegacyId(Context context, String id) throws SQLException {
        return null;
    }

    @Override
    public WorkFlowProcessMasterValue findByLegacyId(Context context, int id) throws SQLException {
        return null;
    }



    @Override
    public void updateLastModified(Context context, WorkFlowProcessMasterValue dso) throws SQLException, AuthorizeException {

    }

    @Override
    public void delete(Context context, WorkFlowProcessMasterValue dso) throws SQLException, AuthorizeException, IOException {
        workFlowProcessMasterValueDAO.delete(context,dso);
    }

    @Override
    public int getSupportsTypeConstant() {
        return 0;
    }

    @Override
    public List<WorkFlowProcessMasterValue> findAll(Context context) throws SQLException {
        return Optional.ofNullable(workFlowProcessMasterValueDAO.findAll(context,WorkFlowProcessMasterValue.class)).orElse(new ArrayList<>());

    }

    @Override
    public WorkFlowProcessMasterValue create(Context context, WorkFlowProcessMasterValue workFlowProcessMaster) throws SQLException, AuthorizeException {
        workFlowProcessMaster= workFlowProcessMasterValueDAO.create(context,workFlowProcessMaster);
        return workFlowProcessMaster;
    }

    @Override
    public List<WorkFlowProcessMasterValue> findAll(Context context, Integer limit, Integer offset) throws SQLException {
        return  Optional.ofNullable(workFlowProcessMasterValueDAO.findAll(context,WorkFlowProcessMasterValue.class,limit,
                offset)).orElse(new ArrayList<>());
    }

    @Override
    public int countRows(Context context) throws SQLException {
        return workFlowProcessMasterValueDAO.countRows(context);
    }

    @Override
    public List<WorkFlowProcessMasterValue> findByType(Context context,String type) throws SQLException {
        System.out.println("in service impl "+type);
       return workFlowProcessMasterValueDAO.findByType(context,type);
    }

    @Override
    public WorkFlowProcessMasterValue find(Context context, UUID uuid) throws SQLException {
        return workFlowProcessMasterValueDAO.findByID(context,WorkFlowProcessMasterValue.class,uuid);
    }

    public void update(Context context, WorkFlowProcessMasterValue workFlowProcessMaster) throws SQLException, AuthorizeException {

        this.workFlowProcessMasterValueDAO.save(context, workFlowProcessMaster);
    }
}