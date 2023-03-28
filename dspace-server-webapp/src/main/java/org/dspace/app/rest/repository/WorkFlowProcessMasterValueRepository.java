/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.dspace.app.rest.Parameter;
import org.dspace.app.rest.SearchRestMethod;
import org.dspace.app.rest.converter.WorkFlowProcessMasterValueConverter;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.WorkFlowProcessMasterRest;
import org.dspace.app.rest.model.WorkFlowProcessMasterValueRest;
import org.dspace.app.rest.model.WorkspaceItemRest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.WorkFlowProcessMasterValue;
import org.dspace.content.service.WorkFlowProcessMasterValueService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * This is the repository responsible to manage Item Rest object
 *
 * @author ashvinmajethiya
 */
@Component(WorkFlowProcessMasterValueRest.CATEGORY + "." + WorkFlowProcessMasterValueRest.NAME)
public class WorkFlowProcessMasterValueRepository extends DSpaceObjectRestRepository<WorkFlowProcessMasterValue, WorkFlowProcessMasterValueRest> {

    @Autowired
    private WorkFlowProcessMasterValueService workFlowProcessMasterValueService;

    @Autowired
    WorkFlowProcessMasterValueConverter workFlowProcessMasterValueConverter;

    public WorkFlowProcessMasterValueRepository(WorkFlowProcessMasterValueService dso) {
        super(dso);
    }
    @Override
    protected WorkFlowProcessMasterValueRest createAndReturn(Context context)
            throws AuthorizeException {
        // this need to be revisited we should receive an EPersonRest as input
        HttpServletRequest req = getRequestService().getCurrentRequest().getHttpServletRequest();
        ObjectMapper mapper = new ObjectMapper();
        WorkFlowProcessMasterValueRest workFlowProcessMasterValueRest = null;
        WorkFlowProcessMasterValue workFlowProcessMasterValue = null;
        try {

            System.out.println("::::::::::::::::req.getInputStream()::::::::"+req.getInputStream().toString());
            workFlowProcessMasterValueRest = mapper.readValue(req.getInputStream(), WorkFlowProcessMasterValueRest.class);
            System.out.println("::::::::::::::::req.getWorkflowprocessmasterid()::::::::"+workFlowProcessMasterValueRest.getWorkFlowProcessMaster().getID());

            workFlowProcessMasterValue = createWorkFlowProcessMasterFromRestObject(context, workFlowProcessMasterValueRest);

        } catch (Exception e1) {
            e1.printStackTrace();
            throw new UnprocessableEntityException("error parsing the body... maybe this is not the right error code");
        }
        return converter.toRest(workFlowProcessMasterValue, utils.obtainProjection());
    }


    private WorkFlowProcessMasterValue createWorkFlowProcessMasterFromRestObject(Context context, WorkFlowProcessMasterValueRest workFlowProcessMasterValueRest) throws AuthorizeException {
        WorkFlowProcessMasterValue workFlowProcessMasterValue = new WorkFlowProcessMasterValue();
        try {
            workFlowProcessMasterValue=workFlowProcessMasterValueConverter.convert(workFlowProcessMasterValue,workFlowProcessMasterValueRest);
            workFlowProcessMasterValueService.create(context, workFlowProcessMasterValue);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return workFlowProcessMasterValue;
    }

    @Override
    protected WorkFlowProcessMasterValueRest put(Context context, HttpServletRequest request, String apiCategory, String model, UUID id,
                                            JsonNode jsonNode) throws SQLException, AuthorizeException {
        WorkFlowProcessMasterValueRest workFlowProcessMasterValueRest = new Gson().fromJson(jsonNode.toString(), WorkFlowProcessMasterValueRest.class);

        WorkFlowProcessMasterValue workFlowProcessMasterValue = workFlowProcessMasterValueService.find(context, id);
        if (workFlowProcessMasterValue == null) {
            System.out.println("WorkFlowProcessMasterRest id ::: is Null  WorkFlowProcessMasterRest tye null"+id);
            throw new ResourceNotFoundException("WorkFlowProcessMasterRest  field with id: " + id + " not found");
        }
        workFlowProcessMasterValue=workFlowProcessMasterValueConverter.convert(workFlowProcessMasterValue,workFlowProcessMasterValueRest);
        workFlowProcessMasterValueService.update(context, workFlowProcessMasterValue);
        context.commit();
        return converter.toRest(workFlowProcessMasterValue, utils.obtainProjection());
    }


    @Override
    public WorkFlowProcessMasterValueRest findOne(Context context, UUID uuid) {

        System.out.println("in findOne "+uuid);

        WorkFlowProcessMasterValueRest workFlowProcessMasterRest=null;
        try {
            Optional<WorkFlowProcessMasterValue> workFlowProcessMasterValue = Optional.ofNullable(workFlowProcessMasterValueService.find(context, uuid));
            if (workFlowProcessMasterValue.isPresent()) {
                System.out.println("in findOne present name"+workFlowProcessMasterValue.get().getName());
                workFlowProcessMasterRest = converter.toRest(workFlowProcessMasterValue.get(), utils.obtainProjection());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workFlowProcessMasterRest;
    }

    @Override
    public Page<WorkFlowProcessMasterValueRest> findAll(Context context, Pageable pageable) throws SQLException {
        int total = workFlowProcessMasterValueService.countRows(context);
        List<WorkFlowProcessMasterValue> workFlowProcessMasters = workFlowProcessMasterValueService.findAll(context);
        return converter.toRestPage(workFlowProcessMasters, pageable, total, utils.obtainProjection());

    }

    protected void delete(Context context, UUID id) throws AuthorizeException {

        WorkFlowProcessMasterValue workFlowProcessMasterValue = null;
        try {
            workFlowProcessMasterValue = workFlowProcessMasterValueService.find(context, id);
            if (workFlowProcessMasterValue == null) {
                throw new ResourceNotFoundException(WorkFlowProcessMasterRest.CATEGORY + "." + WorkFlowProcessMasterRest.NAME +
                        " with id: " + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            workFlowProcessMasterValueService.delete(context, workFlowProcessMasterValue);
            context.commit();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    @SearchRestMethod(name = "findByType")
    public Page<WorkFlowProcessMasterValueRest> findByStartDateAndEndDate(
            @Parameter(value = "type", required = true) String type,
            Pageable pageable)
    {
        try {
            Context context = obtainContext();
            System.out.println("in Repo "+type);
            List<WorkFlowProcessMasterValue> workFlowProcessMasterValueRests = workFlowProcessMasterValueService.findByType(context,type);
            return converter.toRestPage(workFlowProcessMasterValueRests, pageable, 50, utils.obtainProjection());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Class<WorkFlowProcessMasterValueRest> getDomainClass() {
        return null;
    }
}
