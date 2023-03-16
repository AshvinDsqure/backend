
/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.repository;

import org.dspace.app.rest.converter.WorkFlowProcessConverter;
import org.dspace.app.rest.converter.WorkflowProcessSenderDiaryConverter;
import org.dspace.app.rest.exception.RepositoryMethodNotImplementedException;
import org.dspace.app.rest.model.*;
import org.dspace.content.WorkflowProcessDefinition;
import org.dspace.content.WorkflowProcessNote;
import org.dspace.content.WorkflowProcessSenderDiary;
import org.dspace.content.service.DSpaceObjectService;
import org.dspace.content.service.WorkflowProcessSenderDiaryService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

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

   public  WorkflowProcessSenderDiaryRepository(WorkflowProcessSenderDiaryService dsoService) {
        super(dsoService);
    }
    @Autowired
    WorkflowProcessSenderDiaryService workflowProcessSenderDiaryService;

    @Override
    public WorkflowProcessSenderDiaryRest findOne(Context context, UUID uuid) {
        WorkflowProcessSenderDiaryRest workflowProcessSenderDiaryRest=null;
        try {
            Optional<WorkflowProcessSenderDiary> workflowProcessSenderDiary = Optional.ofNullable(workflowProcessSenderDiaryService.find(context, uuid));
            if(workflowProcessSenderDiary.isPresent()){
                    workflowProcessSenderDiaryRest =converter.toRest(workflowProcessSenderDiary.get(),utils.obtainProjection());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return converter.toRest(null, utils.obtainProjection());
    }
    @Override
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public Page<WorkflowProcessSenderDiaryRest> findAll(Context context, Pageable pageable) throws SQLException {
        int total=0;
        List<WorkflowProcessSenderDiary> workflowProcessSenderDiaries= workflowProcessSenderDiaryService.findAll(context);
      return   converter.toRestPage(workflowProcessSenderDiaries,pageable, total, utils.obtainProjection());
    }
    @Override
    public Class<WorkflowProcessSenderDiaryRest> getDomainClass() {
        return null;
    }
}
