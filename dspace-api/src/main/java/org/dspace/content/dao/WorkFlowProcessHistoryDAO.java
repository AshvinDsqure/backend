/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.content.dao;

import org.dspace.content.WorkFlowProcessHistory;
import org.dspace.core.Context;

import java.sql.SQLException;

public interface WorkFlowProcessHistoryDAO extends DSpaceObjectLegacySupportDAO<WorkFlowProcessHistory>{
    int countRows(Context context) throws SQLException;
}
