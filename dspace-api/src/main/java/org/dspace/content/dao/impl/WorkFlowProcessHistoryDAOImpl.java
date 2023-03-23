/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.dao.impl;

import org.apache.logging.log4j.Logger;
import org.dspace.content.WorkFlowProcessHistory;
import org.dspace.content.dao.WorkFlowProcessHistoryDAO;
import org.dspace.core.AbstractHibernateDAO;
import org.dspace.core.Context;

import java.sql.SQLException;

public class WorkFlowProcessHistoryDAOImpl  extends AbstractHibernateDAO<WorkFlowProcessHistory> implements WorkFlowProcessHistoryDAO {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(WorkFlowProcessHistoryDAOImpl.class);
    protected WorkFlowProcessHistoryDAOImpl() {
        super();
    }
    @Override
    public WorkFlowProcessHistory findByLegacyId(Context context, int legacyId, Class<WorkFlowProcessHistory> clazz) throws SQLException {
        return null;
    }

    @Override
    public int countRows(Context context) throws SQLException {
        return count(createQuery(context, "SELECT count(*) FROM WorkFlowProcessHistory"));
    }
}
