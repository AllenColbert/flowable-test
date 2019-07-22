package com.shareniu.v6.ch3;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionAttribute;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.common.impl.util.IoUtil;
import org.flowable.engine.repository.Deployment;
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
				.getResourceAsStream("com/shareniu/v6/ch3/flowable.cfg.xml");
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
				ProcessengineTest.class.getClassLoader().getResourceAsStream("com/shareniu/v6/ch3/multiinstance6.bpmn"),
				"multiinstance.bpmn");
		String resourceName = "multiinstance.bpmn";
		Deployment deployment = repositoryService.createDeployment().addBytes(resourceName, bytes).deploy();
		System.out.println(deployment);
	}

	@Test
	public void start() {
		Map<String, Object> vars = new HashMap<>();
		String[] vStrings = { "shareniuA", "shareniuB", "shareniuC" };
		vars.put("assigneeList", Arrays.asList(vStrings));
		String processDefinitionKey = "myProcess";
		runtimeService.startProcessInstanceByKey(processDefinitionKey);
	}

	@Test
	public void completeTask() {
		Map<String, Object> vars = new HashMap<>();
		vars.put("isPass", true);
		String taskId = "55008";
		taskService.complete(taskId, vars);
	}
	/**
	 * 多实例节点的加签
	 */
	@Test
	public void addMultiInstanceExecution() {
		Map<String, Object> vars = new HashMap<>();
		String[] vStrings = { "shareniuE" };
		vars.put("assigneeList", Arrays.asList(vStrings));
		vars.put("assigneee", "shareniuE");
		String parentExecutionId="2501";
		String activityId="usertask1";
		runtimeService.addMultiInstanceExecution(activityId, parentExecutionId,vars);
	}
	
	
	@Test
	public void deleteMultiInstanceExecution() {
		String executionId="57501";
		runtimeService.deleteMultiInstanceExecution(executionId, true);
	}
}
