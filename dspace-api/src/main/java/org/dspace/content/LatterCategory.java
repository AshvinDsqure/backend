/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "lattercategory")
public class LatterCategory extends DSpaceObject implements DSpaceObjectLegacySupport {
    /**
     * Wild card for Dublin Core metadata qualifiers/languages
     */
    public static final String ANY = "*";

    @Column(name = "lattercategory_id", insertable = false, updatable = false)
    private Integer legacyId;

    @Column(name = "lattercategoryenglishname")
    private String LatterCategoryEnglishName;
    @Column(name = "lattercategoryhindiname")
    private String LatterCategoryHindiName;


    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getName() {
        return "lattercategory";
    }

    @Override
    public Integer getLegacyId() {
        return this.legacyId;
    }

    public void setLegacyId(Integer legacyId) {
        this.legacyId = legacyId;
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
