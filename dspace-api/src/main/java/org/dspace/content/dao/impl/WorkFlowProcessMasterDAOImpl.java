/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.dao.impl;

import org.apache.logging.log4j.Logger;
import org.dspace.content.WorkFlowProcessMaster;
import org.dspace.content.dao.WorkFlowProcessMasterDAO;
import org.dspace.core.AbstractHibernateDAO;
import org.dspace.core.Context;

import java.sql.SQLException;

public class WorkFlowProcessMasterDAOImpl  extends AbstractHibernateDAO<WorkFlowProcessMaster> implements WorkFlowProcessMasterDAO {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(WorkFlowProcessMasterDAOImpl.class);
    protected WorkFlowProcessMasterDAOImpl() {
        super();
    }
    @Override
    public WorkFlowProcessMaster findByLegacyId(Context context, int legacyId, Class<WorkFlowProcessMaster> clazz) throws SQLException {
        return null;
    }
    @Override
    public int countRows(Context context) throws SQLException {
        return count(createQuery(context, "SELECT count(*) FROM WorkFlowProcessMaster"));
    }
}
