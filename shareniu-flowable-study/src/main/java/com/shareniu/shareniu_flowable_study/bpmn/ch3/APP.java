package com.shareniu.shareniu_flowable_study.bpmn.ch3;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
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
		byte[] bytes = IoUtil.readInputStream(
				ProcessengineTest.class.getClassLoader().getResourceAsStream(
						"com/shareniu/shareniu_flowable_study/bpmn/ch3/multiInstance_4.bpmn20.xml"),
				"multiInstance_isSequential.bpmn20.xml");
		String resourceName = "multiInstance.bpmn";
		Deployment deployment = repositoryService.createDeployment().addBytes(resourceName, bytes).deploy();
		System.out.println(deployment);
	}

	@Test
	public void startProcessInstanceByKey() {
		Map<String, Object> vars = new HashMap<>();
		String[] v = { "shareniu1", "shareniu2", "shareniu3", "shareniu4" };
		vars.put("assigneeList", Arrays.asList(v));
		String processDefinitionKey = "multiInstance";
		ProcessInstance startProcessInstanceByKey = runtimeService.startProcessInstanceByKey(processDefinitionKey);
		System.out.println(startProcessInstanceByKey);
	}

	@Test
	public void complete() {
		String taskId = "10003";
		taskService.complete(taskId);
	}
	@Test
	public void addMultiInstanceExecution() {
		
		Map<String, Object> executionVariables=new HashMap<>();
		String parentExecutionId="2501";
		String activityId="A";
		runtimeService.addMultiInstanceExecution(activityId, parentExecutionId, executionVariables);
	}
	@Test
	public void deleteMultiInstanceExecution() {
		String executionId="15008";
		boolean executionIsCompleted=true;
		runtimeService.deleteMultiInstanceExecution(executionId, executionIsCompleted);
	}

}
