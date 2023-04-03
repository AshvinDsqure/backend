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
import org.dspace.content.WorkFlowProcessMasterValue;
import org.dspace.content.WorkFlowProcessMasterValue_;
import org.dspace.content.dao.WorkFlowProcessMasterValueDAO;
import org.dspace.core.AbstractHibernateDAO;
import org.dspace.core.Context;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

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

    @Override
    public List<WorkFlowProcessMasterValue> findByType(Context context, String mastername,Integer offset,Integer limit) throws SQLException{

        try {
            System.out.println("in DAO impl " + mastername);
            Query query = createQuery(context, " SELECT mv from  WorkFlowProcessMasterValue as mv inner join mv.workflowprocessmaster as  mm where mm.mastername=:mastername");
            query.setParameter("mastername", mastername);
            if (offset != null) {
                query.setFirstResult(offset);
            }
            if (limit != null) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        }catch (Exception e){
            System.out.println("in error " + e.getMessage());
            return null;
        }

    }
    @Override
    public WorkFlowProcessMasterValue findByName(Context context, String name, WorkFlowProcessMaster workFlowProcessMaster)throws SQLException{
     CriteriaBuilder criteriaBuilder= getCriteriaBuilder(context);
     CriteriaQuery<WorkFlowProcessMasterValue> criteriaQuery =getCriteriaQuery(criteriaBuilder,WorkFlowProcessMasterValue.class);
     Root<WorkFlowProcessMasterValue> workFlowProcessMasterValueRoot=criteriaQuery.from(WorkFlowProcessMasterValue.class);
     Join<WorkFlowProcessMasterValue,WorkFlowProcessMaster> workFlowProcessMasterValueWorkFlowProcessMasterJoin = workFlowProcessMasterValueRoot.join(WorkFlowProcessMasterValue_.workflowprocessmaster);
     criteriaQuery.select(workFlowProcessMasterValueRoot).where(criteriaBuilder.and(
        criteriaBuilder.equal(workFlowProcessMasterValueRoot.get("primaryvalue"),name),
        criteriaBuilder.equal(workFlowProcessMasterValueWorkFlowProcessMasterJoin.get("id"),workFlowProcessMaster.getID())
     ));
     return singleResult(context,criteriaQuery);

    }


    @Override
    public int countfindByType(Context context, String type) throws SQLException {
        try {
            System.out.println("in DAO impl " + type);
            Query query = createQuery(context, " SELECT count(mv) from  WorkFlowProcessMasterValue as mv inner join mv.workflowprocessmaster as  mm where mm.mastername=:mastername");
            query.setParameter("mastername", type);

            return count(query);
        }catch (Exception e){
            System.out.println("in error " + e.getMessage());
            return 0;
        }
    }

}
