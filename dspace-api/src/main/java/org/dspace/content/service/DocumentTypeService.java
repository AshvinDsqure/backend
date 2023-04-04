/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.service;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DocumentType;
import org.dspace.core.Context;

import java.sql.SQLException;
import java.util.List;

/**
 * Service interface class for the Item object.
 * The implementation of this class is responsible for all business logic calls for the Item object and is autowired
 * by spring
 *
 * @author kevinvandevelde at atmire.com
 */
public interface DocumentTypeService extends DSpaceObjectService<DocumentType>, DSpaceObjectLegacySupportService<DocumentType> {



    /**
     * Create a new DocumentType, with a new internal ID. Authorization is done
     * inside of this method.
     *
     * @param context       DSpace context object
     * @param documentType  DocumentType
     * @return the newly created item
     * @throws SQLException       if database error
     * @throws AuthorizeException if authorization error
     */
    public DocumentType create(Context context, DocumentType documentType) throws SQLException, AuthorizeException;
    public DocumentType create(Context context) throws SQLException, AuthorizeException;

    /**
     * Get All DocumentType
     *
     * @param context DSpace context object
     * @return an iterator over the items in the archive.
     * @throws SQLException if database error
     */
    public List<DocumentType> findAll(Context context) throws SQLException;

    /**
     * get All DocumentType with limit and offset
     *
     * @param context DSpace context object
     * @param limit   limit
     * @param offset  offset
     * @return an iterator over the items in the archive.
     * @throws SQLException if database error
     */
    public List<DocumentType> findAll(Context context, Integer limit, Integer offset) throws SQLException;
    public int countRows(Context context) throws SQLException;

}
