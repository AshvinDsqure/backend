/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.dao.impl;

import org.apache.logging.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.*;
import org.dspace.content.dao.ItemDAO;
import org.dspace.content.dao.WorkflowProcessDAO;
import org.dspace.core.AbstractHibernateDSODAO;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.type.StandardBasicTypes;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.SQLException;
import java.util.*;

/**
 * Hibernate implementation of the Database Access Object interface class for the Item object.
 * This class is responsible for all database calls for the Item object and is autowired by spring
 * This class should never be accessed directly.
 *
 * @author kevinvandevelde at atmire.com
 */
public class WorkflowProcessDAOImpl extends AbstractHibernateDSODAO<WorkflowProcess> implements WorkflowProcessDAO {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(WorkflowProcessDAOImpl.class);

    protected WorkflowProcessDAOImpl() {
        super();
    }

    @Override
    public List<WorkflowProcess> findByWorkFlowProcessIds(Context context, List<String> WorkFlowProcessIds, Integer offset, Integer limit) throws SQLException {

        try {
            String s = WorkFlowProcessIds.toString().replace("[", "");
            String id = s.replace("]", "");
            StringBuilder queryBuilder = new StringBuilder("SELECT s from  WorkflowProcess as s where id in (:WorkFlowProcessIds)");
            Query query = createQuery(context, queryBuilder.toString());
            query.setParameter("WorkFlowProcessIds", id);
            if (0 <= offset) {
                query.setFirstResult(offset);
            }
            if (0 <= limit) {
                query.setMaxResults(limit);
            }

            return query.getResultList();
        } catch (Exception e) {
            System.out.println("in error " + e.getMessage());
            return null;
        }
    }
}
