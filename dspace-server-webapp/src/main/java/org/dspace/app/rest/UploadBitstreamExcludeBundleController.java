/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.converter.BitstreamConverter;
import org.dspace.app.rest.converter.ConverterService;
import org.dspace.app.rest.converter.WorkflowProcessReferenceDocConverter;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.BitstreamRest;
import org.dspace.app.rest.model.BundleRest;
import org.dspace.app.rest.model.WorkflowProcessReferenceDocRest;
import org.dspace.app.rest.model.hateoas.BitstreamResource;
import org.dspace.app.rest.repository.BundleRestRepository;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.app.rest.utils.Utils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.*;
import org.dspace.content.service.BundleService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.WorkflowProcessService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ControllerUtils;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Optional;
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
@RequestMapping("/api/" + BundleRest.CATEGORY + "/" + BundleRest.PLURAL_NAME
        +  "/" + BitstreamRest.PLURAL_NAME)
public class UploadBitstreamExcludeBundleController {

    private static final Logger log = LogManager.getLogger();

    @Autowired
    protected Utils utils;

    @Autowired
    private BundleService bundleService;
    @Autowired
    private WorkflowProcessService workflowProcessService;

    @Autowired
    private BundleRestRepository bundleRestRepository;

    @Autowired
    private ConverterService converter;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BitstreamConverter bitstreamConverter;

    @Autowired
    private WorkflowProcessReferenceDocConverter workflowProcessReferenceDocConverter;


    /**
     * Method to upload a Bitstream to a Bundle with the given UUID in the URL. This will create a Bitstream with the
     * file provided in the request and attach this to the Item that matches the UUID in the URL.
     * This will only work for uploading one file, any extra files will silently be ignored
     *
     * @return The created BitstreamResource
     */
    @RequestMapping(method = RequestMethod.POST, headers = "content-type=multipart/form-data")
    @PreAuthorize("hasPermission(#uuid, 'BUNDLE', 'ADD') && hasPermission(#uuid, 'BUNDLE', 'WRITE')")
    public ResponseEntity<RepresentationModel<?>> uploadBitstream(
            HttpServletRequest request,
            @PathVariable UUID uuid,
            @RequestParam("file") MultipartFile uploadfile,
            @RequestParam(value = "properties", required = false) String properties, String workflowProcessReferenceDocRest) {

        Context context = ContextUtil.obtainContext(request);
        Bitstream bitstream =null;
        try {
            Optional<Item> itemOptional = Optional.ofNullable(itemService.find(context, uuid));
            if (!itemOptional.isPresent()) {
                throw new ResourceNotFoundException("Item not found");
            }
            ObjectMapper mapper = new ObjectMapper();
            WorkflowProcessReferenceDocRest workflowProcessReferenceDocRestobj = mapper.readValue(workflowProcessReferenceDocRest, WorkflowProcessReferenceDocRest.class);
            InputStream fileInputStream = null;
            fileInputStream = uploadfile.getInputStream();
            bitstream = bundleRestRepository.processBitstreamCreationWithoutBundle(
                    context, fileInputStream, properties, uploadfile.getOriginalFilename());
            WorkflowProcessReferenceDoc workflowProcessReferenceDoc = workflowProcessReferenceDocConverter.convert(context, workflowProcessReferenceDocRestobj);
            workflowProcessReferenceDoc.setBitstream(bitstream);
            workflowProcessService.storeWorkFlowMataDataTOBitsream(context, workflowProcessReferenceDoc, itemOptional.get());

        } catch (SQLException e) {
            log.error("Something went wrong trying to find the Bundle with uuid: " + uuid, e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (AuthorizeException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BitstreamRest bitstreamRest=bitstreamConverter.convert(bitstream,utils.obtainProjection());
        BitstreamResource bitstreamResource = converter.toResource(bitstreamRest);
        return ControllerUtils.toResponseEntity(HttpStatus.CREATED, new HttpHeaders(), bitstreamResource);
    }
}
