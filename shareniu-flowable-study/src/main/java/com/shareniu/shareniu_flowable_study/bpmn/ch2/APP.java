package com.shareniu.shareniu_flowable_study.bpmn.ch2;

import java.io.InputStream;

import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.common.impl.util.IoUtil;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.ch3.ProcessengineTest;

public class APP {
	ProcessEngine processEngine;
	RepositoryService repositoryService;
	IdentityService identityService;
	RuntimeService runtimeService;
	TaskService taskService;
	HistoryService historyService;

	@Before
	public void init() {
		InputStream inputStream = APP.class.getClassLoader()
				.getResourceAsStream("com/shareniu/shareniu_flowable_study/bpmn/ch2/flowable.cfg.xml");
		ProcessEngineConfiguration createProcessEngineConfigurationFromInputStream = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromInputStream(inputStream);
		processEngine = createProcessEngineConfigurationFromInputStream.buildProcessEngine();
		repositoryService = processEngine.getRepositoryService();
		identityService = processEngine.getIdentityService();
		runtimeService = processEngine.getRuntimeService();
		taskService = processEngine.getTaskService();
		historyService = processEngine.getHistoryService();
	}

	@Test
	public void addBytes() {
		byte[] bytes = IoUtil.readInputStream(ProcessengineTest.class.getClassLoader().getResourceAsStream(
				"com/shareniu/shareniu_flowable_study/bpmn/ch2/multiInstance_isSequential.bpmn20.xml"), "nomal.bpmn20.xml");
		String resourceName = "nomal.bpmn";
		Deployment deployment = repositoryService.createDeployment().addBytes(resourceName, bytes).deploy();
		System.out.println(deployment);
	}

	@Test
	public void startProcessInstanceByKey() {
		String processDefinitionKey = "multiInstance";
		ProcessInstance startProcessInstanceByKey = runtimeService.startProcessInstanceByKey(processDefinitionKey);
		System.out.println(startProcessInstanceByKey);
	}

	@Test
	public void executeCommand() {
		ManagementService managementService = processEngine.getManagementService();
		managementService.executeCommand(new ShareniuCommonJumpTaskCmd("2514", "shareniu-B"));

	}

}
