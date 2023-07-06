/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.converter.ConverterService;
import org.dspace.app.rest.converter.EPersonConverter;
import org.dspace.app.rest.converter.WorkFlowProcessConverter;
import org.dspace.app.rest.enums.WorkFlowStatus;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.*;
import org.dspace.app.rest.model.hateoas.BitstreamResource;
import org.dspace.app.rest.repository.AbstractDSpaceRestRepository;
import org.dspace.app.rest.repository.BundleRestRepository;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.app.rest.utils.ExcelHelper;
import org.dspace.app.rest.utils.Utils;
import org.dspace.content.Bundle;
import org.dspace.content.Item;
import org.dspace.content.WorkflowProcess;
import org.dspace.content.WorkflowProcessReferenceDoc;
import org.dspace.content.service.BundleService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkflowProcessService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.service.EPersonService;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ControllerUtils;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.dspace.app.rest.utils.RegexUtils.REGEX_REQUESTMAPPING_IDENTIFIER_AS_UUID;

/**
 * Controller to upload bitstreams to a certain bundle, indicated by a uuid in the request
 * Usage: POST /api/core/bundles/{uuid}/bitstreams (with file and properties of file in request)
 * Example:
 * <pre>
 * {@code
 * curl https://<dspace.server.url>/api/core/bundles/d3599177-0408-403b-9f8d-d300edd79edb/bitstreams
 *  -XPOST -H 'Content-Type: multipart/form-data' \
 *  -H 'Authorization: Bearer eyJhbGciOiJI...' \
 *  -F "file=@Downloads/test.html" \
 *  -F 'properties={ "name": "test.html", "metadata": { "dc.description": [ { "value": "example file", "language": null,
 *          "authority": null, "confidence": -1, "place": 0 } ]}, "bundleName": "ORIGINAL" };type=application/json'
 * }
 * </pre>
 */
@RestController
@RequestMapping("/api/" + WorkFlowProcessFilterRest.NAME + "/" + WorkFlowProcessFilterRest.PLURAL_NAME
        + "/filter")
public class WorkflowProcessFilterController {

    @Autowired
    WorkflowProcessService workflowProcessService;

    @Autowired
    EPersonConverter ePersonConverter;
    @Autowired
    EPersonService ePersonService;


    @Autowired
    WorkFlowProcessConverter workFlowProcessConverter;

    private static final Logger log = LogManager.getLogger();

    @Autowired
    protected Utils utils;
    @Autowired
    protected ItemService itemService;

    /**
     * Method to upload a Bitstream to a Bundle with the given UUID in the URL. This will create a Bitstream with the
     * file provided in the request and attach this to the Item that matches the UUID in the URL.
     * This will only work for uploading one file, any extra files will silently be ignored
     *
     * @return The created BitstreamResource
     */
    @RequestMapping(method = RequestMethod.POST, value = "/filterbyData")
    public List<WorkFlowProcessRest> filter(HttpServletRequest request, @RequestBody WorkFlowProcessFilterRest rest) {
        try {
            Context context = ContextUtil.obtainContext(request);
            HashMap<String, String> map = new HashMap<>();
            if (rest.getPriorityRest() != null && rest.getPriorityRest().getUuid() != null) {
                map.put("priority", rest.getPriorityRest().getUuid());
            }
            if (rest.getWorkflowStatusRest() != null && rest.getWorkflowStatusRest().getUuid() != null) {
                map.put("status", rest.getWorkflowStatusRest().getUuid());
            }
            if (rest.getWorkflowTypeRest() != null && rest.getWorkflowTypeRest().getUuid() != null) {
                map.put("type", rest.getWorkflowTypeRest().getUuid());
            }
            if (rest.getDepartmentRest() != null && rest.getDepartmentRest().getUuid() != null) {
                map.put("department", rest.getWorkflowTypeRest().getUuid());
            }
            if (rest.getePersonRest() != null && rest.getePersonRest().getUuid() != null) {
                map.put("user", rest.getePersonRest().getUuid());
            }
            if (rest.getSubject() != null) {
                map.put("subject", rest.getSubject());
            }
            System.out.println(map);
            List<WorkflowProcess> list = workflowProcessService.Filter(context, map, 0, 199);
            List<WorkFlowProcessRest> rests = list.stream().map(d -> {
                return workFlowProcessConverter.convert(d, utils.obtainProjection());
            }).collect(Collectors.toList());
            return rests;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
