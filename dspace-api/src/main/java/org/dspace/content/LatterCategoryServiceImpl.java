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
import org.dspace.content.dao.LatterCategoryDAO;
import org.dspace.content.service.LatterCategoryService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LatterCategoryServiceImpl extends DSpaceObjectServiceImpl<LatterCategory> implements LatterCategoryService{


    /**
     * log4j category
     */
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(Item.class);

    @Autowired(required = true)
    protected LatterCategoryDAO latterCategoryDAO;

    protected LatterCategoryServiceImpl() {
        super();
    }

    @Override
    public LatterCategory findByIdOrLegacyId(Context context, String id) throws SQLException {
        return null;
    }

    @Override
    public LatterCategory findByLegacyId(Context context, int id) throws SQLException {
        return null;
    }



    @Override
    public void updateLastModified(Context context, LatterCategory dso) throws SQLException, AuthorizeException {

    }

    @Override
    public void delete(Context context, LatterCategory dso) throws SQLException, AuthorizeException, IOException {
        latterCategoryDAO.delete(context,dso);
    }

    @Override
    public int getSupportsTypeConstant() {
        return 0;
    }

    @Override
    public List<LatterCategory> findAll(Context context) throws SQLException {
        return Optional.ofNullable(latterCategoryDAO.findAll(context,LatterCategory.class)).orElse(new ArrayList<>());

    }

    @Override
    public LatterCategory create(Context context, LatterCategory latterCategory) throws SQLException, AuthorizeException {
        latterCategory= latterCategoryDAO.create(context,latterCategory);
        return latterCategory;
    }

    @Override
    public List<LatterCategory> findAll(Context context, Integer limit, Integer offset) throws SQLException {
        return  Optional.ofNullable(latterCategoryDAO.findAll(context,LatterCategory.class,limit,
                offset)).orElse(new ArrayList<>());
    }

    @Override
    public int countRows(Context context) throws SQLException {
        return latterCategoryDAO.countRows(context);
    }

    @Override
    public LatterCategory find(Context context, UUID uuid) throws SQLException {
        return latterCategoryDAO.findByID(context,LatterCategory.class,uuid);
    }

    public void update(Context context, LatterCategory latterCategory) throws SQLException, AuthorizeException {

        this.latterCategoryDAO.save(context, latterCategory);
    }
}