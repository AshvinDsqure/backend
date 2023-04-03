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
import org.dspace.app.rest.converter.WorkflowProcessSenderDiaryConverter;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.DocumentTypeRest;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.model.LatterCategoryRest;
import org.dspace.app.rest.model.WorkflowProcessSenderDiaryRest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.LatterCategory;
import org.dspace.content.WorkflowProcessSenderDiary;
import org.dspace.content.service.WorkflowProcessSenderDiaryService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Component(WorkflowProcessSenderDiaryRest.CATEGORY + "." + WorkflowProcessSenderDiaryRest.NAME)
public class WorkflowProcessSenderDiaryRepository extends DSpaceObjectRestRepository<WorkflowProcessSenderDiary, WorkflowProcessSenderDiaryRest> {


   public  WorkflowProcessSenderDiaryRepository(WorkflowProcessSenderDiaryService s) {
        super(s);
    }
    @Autowired
    WorkflowProcessSenderDiaryService workflowProcessSenderDiaryService;

   @Autowired
   WorkflowProcessSenderDiaryConverter workflowProcessSenderDiaryConverter;

    @Override
    public WorkflowProcessSenderDiaryRest findOne(Context context, UUID uuid) {
        WorkflowProcessSenderDiaryRest workflowProcessSenderDiaryRest=null;
        try {
            System.out.println("uuid::"+uuid);
            Optional<WorkflowProcessSenderDiary> workflowProcessSenderDiary = Optional.ofNullable(workflowProcessSenderDiaryService.find(context, uuid));
            System.out.println("OPtion is present ::"+workflowProcessSenderDiary.isPresent());
            if(workflowProcessSenderDiary.isPresent()){
                    workflowProcessSenderDiaryRest =converter.toRest(workflowProcessSenderDiary.get(),utils.obtainProjection());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return workflowProcessSenderDiaryRest;
    }
    @Override
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public Page<WorkflowProcessSenderDiaryRest> findAll(Context context, Pageable pageable) throws SQLException {

        int total=workflowProcessSenderDiaryService.countRows(context);
        List<WorkflowProcessSenderDiary> workflowProcessSenderDiaries= workflowProcessSenderDiaryService.findAll(context);
      return   converter.toRestPage(workflowProcessSenderDiaries,pageable, total, utils.obtainProjection());
    }
    @Override
    protected WorkflowProcessSenderDiaryRest createAndReturn(Context context)
            throws AuthorizeException {
        // this need to be revisited we should receive an EPersonRest as input
        HttpServletRequest req = getRequestService().getCurrentRequest().getHttpServletRequest();
        ObjectMapper mapper = new ObjectMapper();
        WorkflowProcessSenderDiaryRest workflowProcessSenderDiaryRest = null;
        WorkflowProcessSenderDiary workflowProcessSenderDiary=null;
        try {
            workflowProcessSenderDiaryRest = mapper.readValue(req.getInputStream(), WorkflowProcessSenderDiaryRest.class);
            workflowProcessSenderDiary= createworkflowProcessFromRestObject(context,workflowProcessSenderDiaryRest);

        } catch (Exception e1) {
            e1.printStackTrace();
            throw new UnprocessableEntityException("error parsing the body... maybe this is not the right error code");
        }
        return converter.toRest(workflowProcessSenderDiary, utils.obtainProjection());
    }

    private WorkflowProcessSenderDiary createworkflowProcessFromRestObject(Context context, WorkflowProcessSenderDiaryRest workflowProcessSenderDiaryRest) throws AuthorizeException {
        WorkflowProcessSenderDiary workflowProcessSenderDiary =new WorkflowProcessSenderDiary();

        try {
            workflowProcessSenderDiary=workflowProcessSenderDiaryConverter.convert(workflowProcessSenderDiary,workflowProcessSenderDiaryRest );
            workflowProcessSenderDiaryService.create(context,workflowProcessSenderDiary);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return workflowProcessSenderDiary;
    }

    protected void delete(Context context, UUID id) throws AuthorizeException {

        WorkflowProcessSenderDiary WorkflowProcessSenderDiary = null;
        try {
            WorkflowProcessSenderDiary = workflowProcessSenderDiaryService.find(context, id);
            if (WorkflowProcessSenderDiary == null) {
                throw new ResourceNotFoundException(DocumentTypeRest.CATEGORY + "." + DocumentTypeRest.NAME +
                        " with id: " + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            workflowProcessSenderDiaryService.delete(context, WorkflowProcessSenderDiary);
            context.commit();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    @Override
    protected WorkflowProcessSenderDiaryRest put(Context context, HttpServletRequest request, String apiCategory, String model, UUID id,
                                     JsonNode jsonNode) throws SQLException, AuthorizeException {

        WorkflowProcessSenderDiaryRest workflowProcessSenderDiaryRest=null;
        ObjectMapper jsonObjectMapper = new ObjectMapper();
        //= new Gson().fromJson(jsonNode.toString(), WorkflowProcessSenderDiaryRest.class);
try {
     workflowProcessSenderDiaryRest = jsonObjectMapper.readValue(jsonNode.toString(), WorkflowProcessSenderDiaryRest.class);
}catch (Exception e){

}
        WorkflowProcessSenderDiary workflowProcessSenderDiary = workflowProcessSenderDiaryService.find(context, id);


        System.out.println("WorkflowProcessSenderDiary a"+id);
        if (workflowProcessSenderDiary == null) {
            System.out.println("workflowProcessSenderDiary id ::: is Null  LatterCategoryRest tye null"+id);
            throw new ResourceNotFoundException("workflowProcessSenderDiary  field with id: " + id + " not found");
        }
        workflowProcessSenderDiary=workflowProcessSenderDiaryConverter.convert(workflowProcessSenderDiary,workflowProcessSenderDiaryRest);
        workflowProcessSenderDiary.setCity(workflowProcessSenderDiaryRest.getCity());
        workflowProcessSenderDiary.setCountry(workflowProcessSenderDiaryRest.getCountry());
        workflowProcessSenderDiary.setOrganization(workflowProcessSenderDiaryRest.getOrganization());
        workflowProcessSenderDiary.setSendername(workflowProcessSenderDiaryRest.getSendername());
        workflowProcessSenderDiary.setDesignation(workflowProcessSenderDiaryRest.getDesignation());
        workflowProcessSenderDiary.setContactNumber(workflowProcessSenderDiaryRest.getContactNumber());
        workflowProcessSenderDiary.setEmail(workflowProcessSenderDiaryRest.getEmail());
        workflowProcessSenderDiary.setAddress(workflowProcessSenderDiaryRest.getAddress());
        workflowProcessSenderDiaryService.update(context, workflowProcessSenderDiary);
        context.commit();
        System.out.println("WorkflowProcessSenderDiary update finished");
        return converter.toRest(workflowProcessSenderDiary, utils.obtainProjection());

    }
    @SearchRestMethod(name = "search")
    public WorkflowProcessSenderDiaryRest search(
            @Parameter(value = "search", required = true) String search,
            Pageable pageable) {
        WorkflowProcessSenderDiaryRest workflowProcessSenderDiaryRest=null;
        Context context = obtainContext();
        try {
            Optional<WorkflowProcessSenderDiary> workflowProcessSenderDiary = Optional.ofNullable(workflowProcessSenderDiaryService.findByEmailID(context, search));
            if(workflowProcessSenderDiary.isPresent()){
                workflowProcessSenderDiaryRest =converter.toRest(workflowProcessSenderDiary.get(),utils.obtainProjection());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return workflowProcessSenderDiaryRest;
    }

    @Override
    public Class<WorkflowProcessSenderDiaryRest> getDomainClass() {
        return null;
    }
}
