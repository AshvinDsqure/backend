/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.model.ItemRest;
import org.dspace.app.rest.utils.Utils;
import org.dspace.content.service.ItemService;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

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
    @Autowired
    ConfigurationService configurationService;

    /**
     * Method to upload a Bitstream to a Bundle with the given UUID in the URL. This will create a Bitstream with the
     * file provided in the request and attach this to the Item that matches the UUID in the URL.
     * This will only work for uploading one file, any extra files will silently be ignored
     *
     * @return The created BitstreamResource
     */
    @RequestMapping(method = RequestMethod.GET,value = "/getCounter")
    public Integer getConter(HttpServletRequest request) {
        Integer counter=336889;
        try {
           // String baseurl = configurationService.getProperty("dspace.server.url");
          //  String counterFile = configurationService.getProperty("dspace.dir") +"/webapps/jspui/counter.txt";
            File f; f=new File("D://counter.txt");
            if(!f.exists())
            { f.createNewFile(); }
            else
            {
                ObjectInputStream inp = new ObjectInputStream(new FileInputStream(f));
                if(inp.available()>0) { counter=inp.readInt(); }
                inp.close();
            }
            ObjectOutputStream oute = new ObjectOutputStream(new FileOutputStream(f));
            oute.writeInt(++counter);
            oute.flush();
            oute.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return counter;
    }
}
