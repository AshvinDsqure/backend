/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.jbpm;

import com.google.gson.Gson;
import org.dspace.content.WorkflowProcess;
import org.dspace.app.rest.jbpm.constant.JBPM;
import org.dspace.app.rest.jbpm.models.JBPMProcess;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.util.Arrays;

@Component
public class JbpmServerImpl {

    @Autowired
    public RestTemplate restTemplate;
    @Autowired
    private ConfigurationService configurationService;

    public String startProcess(WorkflowProcess workflowProcess){
        try{
            String baseurl=configurationService.getProperty("jbpm.server");
            JBPMProcess jbpmProcess=new JBPMProcess(workflowProcess);
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<JBPMProcess> entity = new HttpEntity<JBPMProcess>(jbpmProcess,headers);
            return restTemplate.exchange(baseurl+ JBPM.CREATEPROCESS, HttpMethod.POST, entity, String.class).getBody();
        }catch (Exception e){
            e.printStackTrace();
             throw  new RuntimeException("somethisng went with JBPM",e);
        }

    }
}
