/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Item REST Resource
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@LinksRest(links = {

})
public class WorkflowProcessReferenceDocRest extends DSpaceObjectRest {
    public static final String NAME = "workflowprocessreferencedoc";
    public static final String PLURAL_NAME = "workflowprocessreferencedoc";
    public static final String CATEGORY = RestAddressableModel.CORE;
    @JsonProperty
    private BitstreamRest bitstreamRest;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date InitDate = new Date();
    @JsonProperty
    private WorkFlowProcessRest workFlowProcessRest;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String entityType = null;
    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getType() {
        return NAME;
    }

    public BitstreamRest getBitstreamRest() {
        return bitstreamRest;
    }

    public void setBitstreamRest(BitstreamRest bitstreamRest) {
        this.bitstreamRest = bitstreamRest;
    }

    public Date getInitDate() {
        return InitDate;
    }

    public void setInitDate(Date initDate) {
        InitDate = initDate;
    }

    public WorkFlowProcessRest getWorkFlowProcessRest() {
        return workFlowProcessRest;
    }

    public void setWorkFlowProcessRest(WorkFlowProcessRest workFlowProcessRest) {
        this.workFlowProcessRest = workFlowProcessRest;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}
