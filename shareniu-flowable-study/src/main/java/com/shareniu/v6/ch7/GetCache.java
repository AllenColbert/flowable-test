package com.shareniu.v6.ch7;

import java.io.InputStream;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.common.impl.persistence.deploy.DeploymentCache;
import org.flowable.engine.common.impl.util.IoUtil;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.deploy.DeploymentManager;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.engine.repository.Deployment;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.ch3.ProcessengineTest;

public class GetCache {
	ProcessEngine processEngine;
	RepositoryService repositoryService;
	IdentityService identityService;
	RuntimeService runtimeService;
	TaskService taskService;
	HistoryService historyService;
	ManagementService managementService;
	ProcessEngineConfigurationImpl processEngineConfiguration;

	@Before
	public void init() {

		InputStream inputStream = GetCache.class.getClassLoader()
				.getResourceAsStream("com/shareniu/v6/ch7/flowable.cfg.xml");
		ProcessEngineConfiguration createProcessEngineConfigurationFromInputStream = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromInputStream(inputStream);
		processEngine = createProcessEngineConfigurationFromInputStream.buildProcessEngine();
		repositoryService = processEngine.getRepositoryService();
		identityService = processEngine.getIdentityService();
		runtimeService = processEngine.getRuntimeService();
		taskService = processEngine.getTaskService();
		historyService = processEngine.getHistoryService();
		managementService = processEngine.getManagementService();
		processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
	}

	@Test
	public void addBytes() {
		byte[] bytes = IoUtil.readInputStream(
				ProcessengineTest.class.getClassLoader().getResourceAsStream("com/shareniu/v6/ch7/ch7.bpmn"),
				"ch7.bpmn");
		String resourceName = "ch7.bpmn";
		Deployment deployment = repositoryService.createDeployment().addBytes(resourceName, bytes).deploy();
		System.out.println(deployment);
	}

	@Test
	public void getProcessDefinitionCache() {
		DeploymentManager deploymentManager = processEngineConfiguration.getDeploymentManager();
		DeploymentCache<ProcessDefinitionCacheEntry> processDefinitionCache = deploymentManager.getProcessDefinitionCache();
		String id="ch7:1:62504";
		ProcessDefinitionCacheEntry processDefinitionCacheEntry = processDefinitionCache.get(id);
		System.out.println(processDefinitionCacheEntry);
	}
	
	
	@Test
	public void getBpmnModel() {
		String processDefinitionId="ch7:1:62504";
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		DeploymentManager deploymentManager = processEngineConfiguration.getDeploymentManager();
		DeploymentCache<ProcessDefinitionCacheEntry> processDefinitionCache = deploymentManager.getProcessDefinitionCache();
		ProcessDefinitionCacheEntry processDefinitionCacheEntry = processDefinitionCache.get(processDefinitionId);
		System.out.println(processDefinitionCacheEntry);
	}
	
	

}
