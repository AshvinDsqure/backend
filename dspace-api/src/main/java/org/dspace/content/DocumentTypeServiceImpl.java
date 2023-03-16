/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.dao.DocumentTypeDAO;
import org.dspace.content.service.DocumentTypeService;
import org.dspace.core.Context;
import org.dspace.core.LogHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for the Item object.
 * This class is responsible for all business logic calls for the Item object and is autowired by spring.
 * This class should never be accessed directly.
 *
 * @author kevinvandevelde at atmire.com
 */
public class DocumentTypeServiceImpl extends DSpaceObjectServiceImpl<DocumentType> implements DocumentTypeService {

    /**
     * log4j category
     */
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(Item.class);

    @Autowired(required = true)
    protected DocumentTypeDAO documentTypeDAO;
    @Autowired(required = true)
    protected AuthorizeService authorizeService;
    protected DocumentTypeServiceImpl() {
        super();
    }


    @Override
    public DocumentType findByIdOrLegacyId(Context context, String id) throws SQLException {
        return null;
    }

    @Override
    public DocumentType findByLegacyId(Context context, int id) throws SQLException {
        return null;
    }

    @Override
    public DocumentType find(Context context, UUID uuid) throws SQLException {
        return documentTypeDAO.findByID(context,DocumentType.class,uuid);
    }

    @Override
    public void updateLastModified(Context context, DocumentType dso) throws SQLException, AuthorizeException {

    }

    @Override
    public void delete(Context context, DocumentType dso) throws SQLException, AuthorizeException, IOException {
         documentTypeDAO.delete(context,dso);
    }

    @Override
    public int getSupportsTypeConstant() {
        return 0;
    }

    @Override
    public DocumentType create(Context context, DocumentType documentType) throws SQLException, AuthorizeException {
        return documentTypeDAO.create(context,documentType);
    }

    @Override
    public DocumentType create(Context context) throws SQLException, AuthorizeException {
        if (!this.authorizeService.isAdmin(context)) {
            throw new AuthorizeException("You must be an admin to create an EPerson");
        } else {
            DocumentType e = (DocumentType)this.documentTypeDAO.create(context, new DocumentType());
            this.log.info(LogHelper.getHeader(context, "create_DocumentType", "eperson_id=" + e.getID()));
            //context.addEvent(new Event(1, 7, e.getID(), (String)null, this.getIdentifiers(context, e)));
            return e;
        }
    }

    @Override
    public List<DocumentType> findAll(Context context) throws SQLException {
        return null;
    }

    @Override
    public List<DocumentType> findAll(Context context, Integer limit, Integer offset) throws SQLException {
        return documentTypeDAO.findAll(context,limit,offset);
    }

    public void update(Context context, DocumentType documentType) throws SQLException, AuthorizeException {
        log.info(LogHelper.getHeader(context, "update_bundle", "bundle_id=" + documentType.getID()));
        super.update(context, documentType);
        this.documentTypeDAO.save(context, documentType);
    }
    @Override
    public int countRows(Context context) throws SQLException{
       return this.documentTypeDAO.countRows(context);
    }
}
