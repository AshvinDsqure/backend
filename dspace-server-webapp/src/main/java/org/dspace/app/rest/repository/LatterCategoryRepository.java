/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.repository;

import org.dspace.app.rest.exception.RepositoryMethodNotImplementedException;
import org.dspace.app.rest.model.LatterCategoryRest;
import org.dspace.content.LatterCategory;
import org.dspace.content.WorkflowProcessSenderDiary;
import org.dspace.content.service.DSpaceObjectService;
import org.dspace.content.service.LatterCategoryService;
import org.dspace.content.service.WorkflowProcessSenderDiaryService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component(LatterCategoryRest.CATEGORY + "." + LatterCategoryRest.NAME)

public class LatterCategoryRepository extends DSpaceObjectRestRepository<LatterCategory, LatterCategoryRest> {

    @Autowired
    LatterCategoryService latterCategoryService;

    public LatterCategoryRepository(LatterCategoryService dsoService) {
        super(dsoService);
    }

    @Override
    public LatterCategoryRest findOne(Context context, UUID uuid) {
        LatterCategoryRest latterCategoryRest = null;
        try {
            Optional<LatterCategory> LatterCategorys = Optional.ofNullable(latterCategoryService.find(context, uuid));
            if (LatterCategorys.isPresent()) {
                latterCategoryRest = converter.toRest(LatterCategorys.get(), utils.obtainProjection());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return converter.toRest(null, utils.obtainProjection());
    }

    @Override
    public Page<LatterCategoryRest> findAll(Context context, Pageable pageable) throws SQLException {
        int total=0;
        List<LatterCategory> LatterCategorys= latterCategoryService.findAll(context);
        return   converter.toRestPage(LatterCategorys,pageable, total, utils.obtainProjection());

    }

    @Override
    public Class<LatterCategoryRest> getDomainClass() {
        return null;
    }
}
