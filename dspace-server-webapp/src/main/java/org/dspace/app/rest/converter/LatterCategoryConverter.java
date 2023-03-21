/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.LatterCategoryRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.LatterCategory;

public class LatterCategoryConverter extends DSpaceObjectConverter<LatterCategory, LatterCategoryRest> {

    @Override
    public Class<LatterCategory> getModelClass() {
        return LatterCategory.class;
    }

    @Override
    protected LatterCategoryRest newInstance() {
        return new LatterCategoryRest();
    }


    @Override
    public LatterCategoryRest convert(LatterCategory obj, Projection projection) {
        LatterCategoryRest rest = new LatterCategoryRest();
        rest.setLatterCategoryEnglishName(obj.getLatterCategoryEnglishName());
        rest.setLatterCategoryHindiName(obj.getLatterCategoryHindiName());
        return rest;
    }

}
