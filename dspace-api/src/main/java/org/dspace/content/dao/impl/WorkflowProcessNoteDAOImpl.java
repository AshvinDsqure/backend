/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.dao.impl;
import org.apache.logging.log4j.Logger;
import org.dspace.content.WorkflowProcess;
import org.dspace.content.WorkflowProcessNote;
import org.dspace.content.WorkflowProcessReferenceDoc;
import org.dspace.content.dao.WorkflowProcessDAO;
import org.dspace.content.dao.WorkflowProcessNoteDAO;
import org.dspace.core.AbstractHibernateDSODAO;
import org.dspace.core.Context;

import javax.persistence.Query;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
/**
 * Hibernate implementation of the Database Access Object interface class for the Item object.
 * This class is responsible for all database calls for the Item object and is autowired by spring
 * This class should never be accessed directly.
 *
 * @author kevinvandevelde at atmire.com
 */
public class WorkflowProcessNoteDAOImpl extends AbstractHibernateDSODAO<WorkflowProcessNote> implements WorkflowProcessNoteDAO {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(WorkflowProcessNoteDAOImpl.class);
    protected WorkflowProcessNoteDAOImpl() {
        super();
    }

    @Override
    public int countRows(Context context) throws SQLException {
        return 0;
    }
    @Override
    public int countDocumentByItemid(Context context, UUID drafttypeid, UUID itemid) throws SQLException {
        Query query = createQuery(context, "SELECT count(n) FROM WorkflowProcessReferenceDoc as d join d.workflowprocessnote as n  join d.drafttype as df join d.workflowProcess as wp join wp.item as i where df.id=:drafttypeid and i.id=:itemid ");
        query.setParameter("drafttypeid",drafttypeid);
        query.setParameter("itemid",itemid);
        return count(query);
    }
    @Override
    public List<WorkflowProcessNote> getDocumentByItemid(Context context, UUID drafttypeid, UUID itemid, Integer offset, Integer limit) throws SQLException {
        //df.id=:drafttypeid and
        Query query = createQuery(context, "SELECT distinct n FROM WorkflowProcessReferenceDoc as d join d.workflowprocessnote as n  join d.drafttype as df join d.workflowProcess as wp join wp.item as i where i.id=:itemid order by n.InitDate desc");
       // query.setParameter("drafttypeid",drafttypeid);
        query.setParameter("itemid",itemid);
        if (0 <= offset) {
            query.setFirstResult(offset);
        }if (0 <= limit) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}
