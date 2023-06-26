/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.converter.ConverterService;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.*;
import org.dspace.app.rest.model.hateoas.BitstreamResource;
import org.dspace.app.rest.repository.BundleRestRepository;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.app.rest.utils.ExcelHelper;
import org.dspace.app.rest.utils.Utils;
import org.dspace.content.Bundle;
import org.dspace.content.Item;
import org.dspace.content.service.BundleService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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
import java.util.List;
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
@RequestMapping("/api/" + ItemRest.CATEGORY + "/" + ItemRest.PLURAL_NAME
        + "/report")
public class WorkflowProcessItemReportController {

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
    @RequestMapping(method = RequestMethod.GET,value = "/downloadItemReport")
    public  ResponseEntity<Resource> downloadItem(HttpServletRequest request,
            @Parameter(value = "startdate", required = true) String startdate,
            @Parameter(value = "enddate", required = true) String enddate) {
        try {
            Context context = ContextUtil.obtainContext(request);
            String filename = "ProductivityReport.xlsx";
            List<Item> list = itemService.getDataTwoDateRangeDownload(context, startdate, enddate);
            List<ExcelDTO> listDTo = list.stream().map(i -> {
                String title = itemService.getMetadataFirstValue(i, "dc", "title", null, null);
                String type = itemService.getMetadataFirstValue(i, "dc", "type", null, null);
                String issued = itemService.getMetadataFirstValue(i, "dc", "issued", null, null);
                type = (type != null) ? type : "-";
                title = (title != null) ? title : "-";
                issued = (issued != null) ? issued : "-";
                String caseDetail = type + "/" + title + "/" + issued;
                String uploaddate = itemService.getMetadataFirstValue(i, "dc", "date", "accessioned", null);
                 uploaddate=(uploaddate!=null)?uploaddate:"-";
                String uploadedby = i.getSubmitter().getEmail();
                String hierarchy = i.getOwningCollection().getName();
                String email =(context.getCurrentUser()!=null)?context.getCurrentUser().getEmail():"-";
                return new ExcelDTO(title, type, issued, caseDetail, uploaddate, uploadedby, hierarchy,email);
            }).collect(Collectors.toList());
            ByteArrayInputStream in = ExcelHelper.tutorialsToExcel(listDTo);
            InputStreamResource file = new InputStreamResource(in);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(file);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
