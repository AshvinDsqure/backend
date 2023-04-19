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
    public List<WorkflowProcess> findNotCompletedByUser(Context context,UUID eperson,UUID statusid,Integer offset, Integer limit) throws SQLException {
    Query query = createQuery(context, "" +
                "SELECT wp FROM WorkflowProcess as wp " +
                "join wp.workflowProcessEpeople as ep " +
                "join ep.ePerson as p  " +
                "join wp.workflowStatus as st  where ep.isOwner=:isOwner and p.id=:eperson and st.id NOT IN(:statusid)");
        query.setParameter("isOwner",true);
        query.setParameter("eperson",eperson);
        query.setParameter("statusid",statusid);

        if (0 <= offset) {
            query.setFirstResult(offset);
        }if (0 <= limit) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public int countfindNotCompletedByUser(Context context, UUID eperson,UUID statusid) throws SQLException {
        Query query = createQuery(context, "" +
                "SELECT count(wp) FROM WorkflowProcess as wp " +
                "join wp.workflowProcessEpeople as ep " +
                "join ep.ePerson as p  " +
                "join wp.workflowStatus as st  where ep.isOwner=:isOwner and p.id=:eperson and st.id NOT IN(:statusid)");
        query.setParameter("isOwner",true);
        query.setParameter("eperson",eperson);
        query.setParameter("statusid",statusid);

        return count(query);
    }
}
