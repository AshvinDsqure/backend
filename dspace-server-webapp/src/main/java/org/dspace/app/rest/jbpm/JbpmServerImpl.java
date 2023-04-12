/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.jbpm;

import com.google.gson.Gson;
import org.dspace.app.rest.enums.WorkFlowAction;
import org.dspace.app.rest.jbpm.models.JBPMResponse;
import org.dspace.app.rest.model.WorkFlowProcessRest;
import org.dspace.app.rest.model.WorkflowProcessEpersonRest;
import org.dspace.content.WorkflowProcess;
import org.dspace.app.rest.jbpm.constant.JBPM;
import org.dspace.app.rest.jbpm.models.JBPMProcess;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.util.*;
import java.util.stream.Collectors;

@Component
public class JbpmServerImpl {
    @Autowired
    public RestTemplate restTemplate;
    @Autowired
    private ConfigurationService configurationService;
    public String startProcess(WorkFlowProcessRest workflowProcessw,List<String> users) throws  RuntimeException{
        String baseurl=configurationService.getProperty("jbpm.server");
        JBPMProcess jbpmProcess=new JBPMProcess(workflowProcessw);
        jbpmProcess.setUsers(users);
        System.out.println("jbpm json::"+new Gson().toJson(jbpmProcess));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<JBPMProcess> entity = new HttpEntity<JBPMProcess>(jbpmProcess,headers);
        return restTemplate.exchange(baseurl+ JBPM.CREATEPROCESS, HttpMethod.POST, entity, String.class).getBody();

    }
    public String forwardTask(WorkFlowProcessRest workflowProcess,List<String> users) throws  RuntimeException{
        String baseurl=configurationService.getProperty("jbpm.server");
        JBPMProcess jbpmProcess=new JBPMProcess();
        jbpmProcess.setQueueid(workflowProcess.getId());
        jbpmProcess.setUsers(users);
        jbpmProcess.setProcstatus("inprogress");
        System.out.println("jbpm json::"+new Gson().toJson(jbpmProcess));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<JBPMProcess> entity = new HttpEntity<JBPMProcess>(jbpmProcess,headers);
        return restTemplate.exchange(baseurl+ JBPM.FORWARDPROCESS, HttpMethod.POST, entity, String.class).getBody();

    }
    public String completeTask(WorkFlowProcessRest workflowProcess,List<String> users) throws  RuntimeException{
        String baseurl=configurationService.getProperty("jbpm.server");
        JBPMProcess jbpmProcess=new JBPMProcess();
        jbpmProcess.setQueueid(workflowProcess.getId());
        jbpmProcess.setUsers(users);
        jbpmProcess.setProcstatus("completed");
        System.out.println("jbpm json::"+new Gson().toJson(jbpmProcess));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<JBPMProcess> entity = new HttpEntity<JBPMProcess>(jbpmProcess,headers);
        return restTemplate.exchange(baseurl+ JBPM.FORWARDPROCESS, HttpMethod.POST, entity, String.class).getBody();

    }
    public String backwardTask(WorkFlowProcessRest workflowProcess) throws  RuntimeException{
        String baseurl=configurationService.getProperty("jbpm.server");
        JBPMProcess jbpmProcess=new JBPMProcess();
        jbpmProcess.setQueueid(workflowProcess.getId());
        jbpmProcess.setUsers(new ArrayList<>());
        jbpmProcess.setProcstatus("inprogress");
        System.out.println("jbpm json::"+new Gson().toJson(jbpmProcess));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<JBPMProcess> entity = new HttpEntity<JBPMProcess>(jbpmProcess,headers);
        return restTemplate.exchange(baseurl+ JBPM.BACKWARDPROCESS, HttpMethod.POST, entity, String.class).getBody();
    }
    public String holdTask(WorkFlowProcessRest workflowProcess, WorkFlowAction workFlowAction) throws  RuntimeException{
        String baseurl=configurationService.getProperty("jbpm.server");
        JBPMProcess jbpmProcess=new JBPMProcess();
        jbpmProcess.setQueueid(workflowProcess.getId());
        if(workflowProcess.getWorkflowProcessEpersonRests()!= null) {
            jbpmProcess.setUsers(workflowProcess.getWorkflowProcessEpersonRests().stream().map(w -> w.getUuid()).collect(Collectors.toList()));
        }
        jbpmProcess.setProcstatus("inprogress");
        System.out.println("jbpm json::"+new Gson().toJson(jbpmProcess));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<JBPMProcess> entity = new HttpEntity<JBPMProcess>(jbpmProcess,headers);
        return restTemplate.exchange(baseurl+ JBPM.HOLDPROCESS, HttpMethod.POST, entity, String.class).getBody();
    }
    public String resumeTask(WorkFlowProcessRest workflowProcess, WorkFlowAction workFlowAction) throws  RuntimeException{
        String baseurl=configurationService.getProperty("jbpm.server");
        JBPMProcess jbpmProcess=new JBPMProcess();
        jbpmProcess.setQueueid(workflowProcess.getId());
        if(workflowProcess.getWorkflowProcessEpersonRests()!= null) {
            jbpmProcess.setUsers(workflowProcess.getWorkflowProcessEpersonRests().stream().map(w -> w.getUuid()).collect(Collectors.toList()));
        }
        jbpmProcess.setProcstatus("inprogress");
        System.out.println("jbpm json::"+new Gson().toJson(jbpmProcess));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<JBPMProcess> entity = new HttpEntity<JBPMProcess>(jbpmProcess,headers);
        return restTemplate.exchange(baseurl+ JBPM.RESUMEPROCESS, HttpMethod.POST, entity, String.class).getBody();
    }

    public String gettasklist(String uuid) throws  RuntimeException{
        String baseurl=configurationService.getProperty("jbpm.server");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(uuid,headers);
        return restTemplate.exchange(baseurl+ JBPM.GETTASKLIST+"/"+uuid, HttpMethod.GET, entity, String.class).getBody();
    }
}

