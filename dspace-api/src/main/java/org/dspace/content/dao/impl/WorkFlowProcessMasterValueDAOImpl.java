/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.dao.impl;

import org.apache.logging.log4j.Logger;
import org.dspace.content.WorkFlowProcessMasterValue;
import org.dspace.content.dao.WorkFlowProcessMasterValueDAO;
import org.dspace.core.AbstractHibernateDAO;
import org.dspace.core.Context;

import java.sql.SQLException;

public class WorkFlowProcessMasterValueDAOImpl  extends AbstractHibernateDAO<WorkFlowProcessMasterValue> implements WorkFlowProcessMasterValueDAO {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(WorkFlowProcessMasterDAOImpl.class);
    protected WorkFlowProcessMasterValueDAOImpl() {
        super();
    }
    @Override
    public WorkFlowProcessMasterValue findByLegacyId(Context context, int legacyId, Class<WorkFlowProcessMasterValue> clazz) throws SQLException {
        return null;
    }
    @Override
    public int countRows(Context context) throws SQLException {
        return count(createQuery(context, "SELECT count(*) FROM WorkFlowProcessMasterValue"));
    }
}
