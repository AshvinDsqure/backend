/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.dao.impl;

import org.apache.logging.log4j.Logger;
import org.dspace.content.WorkFlowProcessOutwardDetails;
import org.dspace.content.dao.WorkFlowProcessOutwardDetailsDAO;
import org.dspace.core.AbstractHibernateDAO;
import org.dspace.core.Context;

import java.sql.SQLException;

public class WorkFlowProcessOutwardDetailsDAOImpl extends AbstractHibernateDAO<WorkFlowProcessOutwardDetails> implements WorkFlowProcessOutwardDetailsDAO {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(WorkFlowProcessOutwardDetailsDAOImpl.class);
    protected WorkFlowProcessOutwardDetailsDAOImpl() {
        super();
    }
    @Override
    public WorkFlowProcessOutwardDetails findByLegacyId(Context context, int legacyId, Class<WorkFlowProcessOutwardDetails> clazz) throws SQLException {
        return null;
    }

    @Override
    public int countRows(Context context) throws SQLException {
        return count(createQuery(context, "SELECT count(*) FROM WorkFlowProcessOutwardDetails"));
    }
}
