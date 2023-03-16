/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.dao.impl;

import org.apache.logging.log4j.Logger;
import org.dspace.content.LatterCategory;
import org.dspace.content.MetadataField;
import org.dspace.content.dao.LatterCategoryDAO;
import org.dspace.content.dao.MetadataFieldDAO;
import org.dspace.core.AbstractHibernateDAO;
import org.dspace.core.Context;

import java.sql.SQLException;

public class LatterCategoryDAOImpl  extends AbstractHibernateDAO<LatterCategory> implements LatterCategoryDAO {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(WorkflowProcessNoteDAOImpl.class);
    protected LatterCategoryDAOImpl() {
        super();
    }
    @Override
    public LatterCategory findByLegacyId(Context context, int legacyId, Class<LatterCategory> clazz) throws SQLException {
        return null;
    }
}
