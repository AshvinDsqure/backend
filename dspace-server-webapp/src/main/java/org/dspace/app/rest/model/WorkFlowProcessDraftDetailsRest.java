/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dspace.app.rest.model.helper.MyDateConverter;
import org.dspace.content.WorkFlowProcessMasterValue;

import javax.persistence.*;
import java.util.Date;

public class WorkFlowProcessDraftDetailsRest extends  DSpaceObjectRest{
    public static final String NAME = "workflowprocessdraftdetail";
    public static final String PLURAL_NAME = "workflowprocessdraftdetails";
    public static final String CATEGORY = RestAddressableModel.WORKFLOWPROCESSDRAFTDETAIL;
    public static final String GROUPS = "groups";
    private Integer legacyId;
    @JsonDeserialize(converter = MyDateConverter.class)
    private Date draftdate;
    //drafttype means notesheet or document
    private WorkFlowProcessMasterValueRest drafttypeRest;
    private EPersonRest documentsignatorRest;
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
    public Date getDraftdate() {
        return draftdate;
    }
    public void setDraftdate(Date draftdate) {
        this.draftdate = draftdate;
    }
    public WorkFlowProcessMasterValueRest getDrafttypeRest() {
        return drafttypeRest;
    }
    public void setDrafttypeRest(WorkFlowProcessMasterValueRest drafttypeRest) {
        this.drafttypeRest = drafttypeRest;
    }
    public EPersonRest getDocumentsignatorRest() {
        return documentsignatorRest;
    }
    public void setDocumentsignatorRest(EPersonRest documentsignatorRest) {
        this.documentsignatorRest = documentsignatorRest;
    }
}
