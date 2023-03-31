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
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.Parameter;
import org.dspace.app.rest.SearchRestMethod;
import org.dspace.app.rest.converter.WorkFlowProcessHistoryConverter;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.WorkFlowProcessHistoryRest;
import org.dspace.app.rest.model.WorkspaceItemRest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.WorkFlowProcessHistory;
import org.dspace.content.service.WorkFlowProcessHistoryService;
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

@Component(WorkFlowProcessHistoryRest.CATEGORY + "." + WorkFlowProcessHistoryRest.NAME)

public class WorkFlowProcessHistoryRepository extends DSpaceObjectRestRepository<WorkFlowProcessHistory, WorkFlowProcessHistoryRest> {


    private static final Logger log = org.apache.logging.log4j.LogManager
            .getLogger(WorkFlowProcessHistoryRepository.class);
    @Autowired
    WorkFlowProcessHistoryService workFlowProcessHistoryService;

    @Autowired
    WorkFlowProcessHistoryConverter workFlowProcessHistoryConverter;

    public WorkFlowProcessHistoryRepository(WorkFlowProcessHistoryService dsoService) {
        super(dsoService);
    }

    @Override
    protected WorkFlowProcessHistoryRest createAndReturn(Context context)
            throws AuthorizeException {
        // this need to be revisited we should receive an EPersonRest as input
        HttpServletRequest req = getRequestService().getCurrentRequest().getHttpServletRequest();
        ObjectMapper mapper = new ObjectMapper();
        WorkFlowProcessHistoryRest workFlowProcessHistoryRest = null;
        WorkFlowProcessHistory workFlowProcessHistory = null;
        try {
            workFlowProcessHistoryRest = mapper.readValue(req.getInputStream(), WorkFlowProcessHistoryRest.class);
            workFlowProcessHistory = createWorkFlowProcessHistoryFromRestObject(context, workFlowProcessHistoryRest);

        } catch (Exception e1) {
            e1.printStackTrace();
            throw new UnprocessableEntityException("error parsing the body... maybe this is not the right error code");
        }

        return converter.toRest(workFlowProcessHistory, utils.obtainProjection());
    }
    private WorkFlowProcessHistory createWorkFlowProcessHistoryFromRestObject(Context context, WorkFlowProcessHistoryRest workFlowProcessHistoryRest) throws AuthorizeException {
        WorkFlowProcessHistory workFlowProcessHistory = new WorkFlowProcessHistory();
        try {
            workFlowProcessHistory=workFlowProcessHistoryConverter.convert(context,workFlowProcessHistoryRest );
            workFlowProcessHistoryService.create(context, workFlowProcessHistory);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return workFlowProcessHistory;
    }

    @Override
    protected WorkFlowProcessHistoryRest put(Context context, HttpServletRequest request, String apiCategory, String model, UUID id,
                                     JsonNode jsonNode) throws SQLException, AuthorizeException {
        log.info("::::::start::::put::::::::::");
        WorkFlowProcessHistoryRest workFlowProcessHistoryRest  = new Gson().fromJson(jsonNode.toString(), WorkFlowProcessHistoryRest.class);

        WorkFlowProcessHistory workFlowProcessHistory = workFlowProcessHistoryService.find(context, id);
        if (workFlowProcessHistory == null) {
            System.out.println("workFlowProcessHistoryrest id ::: is Null  workFlowProcessHistoryrest tye null"+id);
            throw new ResourceNotFoundException("workFlowProcessHistoryrest  field with id: " + id + " not found");
        }
        workFlowProcessHistory=workFlowProcessHistoryConverter.convert(context,workFlowProcessHistoryRest);
       workFlowProcessHistoryService.update(context, workFlowProcessHistory);
        context.commit();
        log.info("::::::End::::put::::::::::");

        return converter.toRest(workFlowProcessHistory, utils.obtainProjection());
    }


    @Override
    public WorkFlowProcessHistoryRest findOne(Context context, UUID uuid) {
        WorkFlowProcessHistoryRest workFlowProcessHistoryRest =null;
        log.info("::::::start::::findOne::::::::::");

        try {
            Optional<WorkFlowProcessHistory> workFlowProcessHistory = Optional.ofNullable(workFlowProcessHistoryService.find(context, uuid));
            if (workFlowProcessHistory.isPresent()) {
                workFlowProcessHistoryRest = converter.toRest(workFlowProcessHistory.get(), utils.obtainProjection());
            }
        } catch (Exception e) {
            log.info("::::::error::::findOne::::::::::");
            e.printStackTrace();
        }
        log.info("::::::End::::findOne::::::::::");
        return workFlowProcessHistoryRest;
    }

    @Override
    public Page<WorkFlowProcessHistoryRest> findAll(Context context, Pageable pageable) throws SQLException {
        int total = workFlowProcessHistoryService.countRows(context);
        List<WorkFlowProcessHistory> workFlowProcessHistories = workFlowProcessHistoryService.findAll(context);
        return converter.toRestPage(workFlowProcessHistories, pageable, total, utils.obtainProjection());

    }

    protected void delete(Context context, UUID id) throws AuthorizeException {
        log.info("::::::in::::delete::::::::::");
        WorkFlowProcessHistory workFlowProcessHistory = null;
        try {
            workFlowProcessHistory = workFlowProcessHistoryService.find(context, id);
            if (workFlowProcessHistory == null) {
                log.info("::::::id not found::::delete::::::::::");
                throw new ResourceNotFoundException(WorkFlowProcessHistoryRest.CATEGORY + "." + WorkFlowProcessHistoryRest.NAME +
                        " with id: " + id + " not found");

            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            workFlowProcessHistoryService.delete(context, workFlowProcessHistory);
            context.commit();
            log.info(":::::completed:::delete::::::::::");
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @SearchRestMethod(name = "getHistory")
    public Page<WorkFlowProcessHistoryRest> getHistory(

            @Parameter(value = "epersionid", required = true) UUID epersionid,
            @Parameter(value = "workflowid", required = true) UUID workflowid,
            @Parameter(value = "startdate", required = true) String startdate,
            @Parameter(value = "enddate", required = true) String enddate,
            Pageable pageable) {
        try {
            Context context = obtainContext();
          //  long total = itemService.countTotal(context, startdate, enddate);
            List<WorkFlowProcessHistory> witems = workFlowProcessHistoryService.getHistory(
                    context,workflowid,epersionid, startdate, enddate);
            return converter.toRestPage(witems, pageable, 10, utils.obtainProjection());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }




    @Override
    public Class<WorkFlowProcessHistoryRest> getDomainClass() {
        return null;
    }
}
