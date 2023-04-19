/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.converter.ConverterService;
import org.dspace.app.rest.converter.WorkFlowProcessConverter;
import org.dspace.app.rest.converter.WorkflowProcessReferenceDocConverter;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.*;
import org.dspace.app.rest.model.hateoas.BitstreamResource;
import org.dspace.app.rest.repository.BundleRestRepository;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.app.rest.utils.Utils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.WorkflowProcessReferenceDoc;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.BundleService;
import org.dspace.content.service.WorkflowProcessReferenceDocService;
import org.dspace.content.service.WorkflowProcessService;
import org.dspace.core.Context;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ControllerUtils;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

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
@RequestMapping("/api/" + WorkflowProcessReferenceDocRest.CATEGORY)
public class WorkflowProcessReferenceDocController implements InitializingBean {

    private static final Logger log = LogManager.getLogger();
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    protected Utils utils;
    @Autowired
    private BundleRestRepository bundleRestRepository;
    @Autowired
    private WorkflowProcessReferenceDocConverter workflowProcessReferenceDocConverter;

    @Autowired
    private WorkflowProcessService workflowProcessService;
    @Autowired
    private WorkflowProcessReferenceDocService workflowProcessReferenceDocService;
    @Autowired
    private DiscoverableEndpointsService discoverableEndpointsService;
    @Autowired
    private BundleService bundleService;

    /**
     * Method to upload a Bitstream to a Bundle with the given UUID in the URL. This will create a Bitstream with the
     * file provided in the request and attach this to the Item that matches the UUID in the URL.
     * This will only work for uploading one file, any extra files will silently be ignored
     *
     * @return The created BitstreamResource
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        discoverableEndpointsService
                .register(this, Arrays.asList(Link.of("/api/" + WorkflowProcessReferenceDocRest.CATEGORY, WorkflowProcessReferenceDocRest.CATEGORY)));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}, value = "/bitstream")
    @PreAuthorize("hasPermission(#uuid, 'BUNDLE', 'ADD') && hasPermission(#uuid, 'BUNDLE', 'WRITE')")
    public WorkflowProcessReferenceDocRest uploadBitstream(
            HttpServletRequest request,
            MultipartFile file, String workflowProcessReferenceDocRestStr) throws SQLException, AuthorizeException, IOException {
        WorkflowProcessReferenceDoc workflowProcessReferenceDoc = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            ObjectMapper mapper = new ObjectMapper();
            InputStream fileInputStream = null;
            WorkflowProcessReferenceDocRest workflowProcessReferenceDocRest = mapper.readValue(workflowProcessReferenceDocRestStr, WorkflowProcessReferenceDocRest.class);
            workflowProcessReferenceDoc = workflowProcessReferenceDocConverter.convert(workflowProcessReferenceDocRest, context);
            System.out.println("workflowProcessReferenceDocRest::" + workflowProcessReferenceDocRest.getReferenceNumber());
            if (workflowProcessReferenceDocRest.getWorkFlowProcessRest()!= null && workflowProcessReferenceDocRest.getWorkFlowProcessRest().getUuid()!= null) {
                workflowProcessReferenceDoc.setWorkflowProcess(workflowProcessService.find(context, UUID.fromString(workflowProcessReferenceDocRest.getWorkFlowProcessRest().getUuid())));
            }

            try {
                fileInputStream = file.getInputStream();
            } catch (IOException e) {
                log.error("Something went wrong when trying to read the inputstream from the given file in the request",
                        e);
                throw new UnprocessableEntityException("The InputStream from the file couldn't be read", e);
            }
            Bitstream bitstream = bundleRestRepository.processBitstreamCreationWithoutBundle(
                    context, fileInputStream, "", file.getOriginalFilename());
            System.out.println("bitstream::" + bitstream.getName());
            workflowProcessReferenceDoc.setBitstream(bitstream);
            workflowProcessReferenceDoc = workflowProcessReferenceDocService.create(context, workflowProcessReferenceDoc);
            context.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workflowProcessReferenceDocConverter.convert(workflowProcessReferenceDoc, utils.obtainProjection());

    }
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}, value = "/outward")
    @PreAuthorize("hasPermission(#uuid, 'BUNDLE', 'ADD') && hasPermission(#uuid, 'BUNDLE', 'WRITE')")
    public WorkflowProcessReferenceDocRest uploadBitstreamoutward(
            HttpServletRequest request,
           @Valid WorkflowProcessReferenceDocRest workflowProcessReferenceDocRest) throws SQLException, AuthorizeException, IOException {
        WorkflowProcessReferenceDoc workflowProcessReferenceDoc = null;
        try {
            Context context = ContextUtil.obtainContext(request);
            workflowProcessReferenceDoc = workflowProcessReferenceDocConverter.convert(workflowProcessReferenceDocRest, context);
            System.out.println("workflowProcessReferenceDocRest::" + workflowProcessReferenceDocRest.getReferenceNumber());
            if (workflowProcessReferenceDocRest.getWorkFlowProcessRest()!= null && workflowProcessReferenceDocRest.getWorkFlowProcessRest().getUuid()!= null) {
                workflowProcessReferenceDoc.setWorkflowProcess(workflowProcessService.find(context, UUID.fromString(workflowProcessReferenceDocRest.getWorkFlowProcessRest().getUuid())));
            }
            workflowProcessReferenceDoc = workflowProcessReferenceDocService.create(context, workflowProcessReferenceDoc);
            context.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workflowProcessReferenceDocConverter.convert(workflowProcessReferenceDoc, utils.obtainProjection());

    }

    @RequestMapping(method = RequestMethod.GET, value = "/test")
    public String test(
            HttpServletRequest request
    ) {

        Context context = ContextUtil.obtainContext(request);
        try {
            System.out.println("Demo::");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Done";
    }
}
