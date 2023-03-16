/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.content.dao.impl;

import org.apache.logging.log4j.Logger;
import org.dspace.content.WorkflowProcessNote;
import org.dspace.content.WorkflowProcessSenderDiary;
import org.dspace.content.dao.WorkflowProcessNoteDAO;
import org.dspace.content.dao.WorkflowProcessSenderDiaryDAO;
import org.dspace.core.AbstractHibernateDSODAO;


/**
 * Hibernate implementation of the Database Access Object interface class for the WorkspaceItem object.
 * This class is responsible for all database calls for the WorkspaceItem object and is autowired by Spring.
 * This class should never be accessed directly.
 *
 * @author AshvinMajethiya at atmire.com
 */
public class WorkflowProcessSenderDiaryDAOImpl extends AbstractHibernateDSODAO<WorkflowProcessSenderDiary> implements WorkflowProcessSenderDiaryDAO {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(WorkflowProcessNoteDAOImpl.class);
    protected WorkflowProcessSenderDiaryDAOImpl() {
        super();
    }
}
