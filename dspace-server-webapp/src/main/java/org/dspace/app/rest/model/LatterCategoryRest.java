/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import javax.persistence.Column;

public class LatterCategoryRest extends  DSpaceObjectRest{
    public static final String NAME = "lattercategorie";
    public static final String PLURAL_NAME = "lattercategories";
    public static final String CATEGORY = RestAddressableModel.LATTERCATOGORY;

    public static final String GROUPS = "groups";

    private Integer legacyId;

    private String LatterCategoryEnglishName;
    private String LatterCategoryHindiName;

    public Integer getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(Integer legacyId) {
        this.legacyId = legacyId;
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public String getType() {
        return NAME;
    }

    public String getLatterCategoryEnglishName() {
        return LatterCategoryEnglishName;
    }

    public void setLatterCategoryEnglishName(String latterCategoryEnglishName) {
        LatterCategoryEnglishName = latterCategoryEnglishName;
    }

    public String getLatterCategoryHindiName() {
        return LatterCategoryHindiName;
    }

    public void setLatterCategoryHindiName(String latterCategoryHindiName) {
        LatterCategoryHindiName = latterCategoryHindiName;
    }
}
