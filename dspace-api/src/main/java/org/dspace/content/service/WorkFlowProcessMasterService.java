/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.service;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.WorkFlowProcessMaster;
import org.dspace.core.Context;
import java.sql.SQLException;
import java.util.List;
/**
 * Service interface class for the LatterCategory object.
 * The implementation of this class is responsible for all business logic calls for the Item object and is autowired
 * by spring
 *
 * @author ashvinmajethiya
 */


public interface WorkFlowProcessMasterService extends DSpaceObjectService<WorkFlowProcessMaster>,DSpaceObjectLegacySupportService<WorkFlowProcessMaster> {
    public List<WorkFlowProcessMaster> findAll(Context context) throws SQLException;

    public WorkFlowProcessMaster create(Context context, WorkFlowProcessMaster workFlowProcessMaster) throws SQLException, AuthorizeException;
    /**
     * Get All WorkflowProcess based on limit and offset
     * set are included. The order of the list is indeterminate.
     *
     * @param context DSpace context object
     * @param limit   limit
     * @param offset  offset
     * @return an iterator over the items in the archive.
     * @throws SQLException if database error
     */
    public List<WorkFlowProcessMaster> findAll(Context context, Integer limit, Integer offset) throws SQLException;
    public int countRows(Context context) throws SQLException;
    WorkFlowProcessMaster findByName(Context context, String name) throws  SQLException;

}
