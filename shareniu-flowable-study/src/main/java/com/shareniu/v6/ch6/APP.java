package com.shareniu.v6.ch6;

import java.io.InputStream;
import java.util.List;

import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.common.api.FlowableException;
import org.flowable.engine.common.impl.util.IoUtil;
import org.flowable.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.repository.Deployment;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.ch3.ProcessengineTest;
import com.shareniu.v6.ch4.ShareniuJumpCmd;

public class APP {
	ProcessEngine processEngine;
	RepositoryService repositoryService;
	IdentityService identityService;
	RuntimeService runtimeService;
	TaskService taskService;
	HistoryService historyService;
	ManagementService managementService;

	@Before
	public void init() {

		InputStream inputStream = APP.class.getClassLoader()
				.getResourceAsStream("com/shareniu/v6/ch5/flowable.cfg.xml");
		ProcessEngineConfiguration createProcessEngineConfigurationFromInputStream = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromInputStream(inputStream);
		processEngine = createProcessEngineConfigurationFromInputStream.buildProcessEngine();
		repositoryService = processEngine.getRepositoryService();
		identityService = processEngine.getIdentityService();
		runtimeService = processEngine.getRuntimeService();
		taskService = processEngine.getTaskService();
		historyService = processEngine.getHistoryService();
		managementService = processEngine.getManagementService();
	}

	@Test
	public void addBytes() {
		byte[] bytes = IoUtil.readInputStream(
				ProcessengineTest.class.getClassLoader().getResourceAsStream("com/shareniu/v6/ch6/multiinstance3.bpmn"),
				"multiinstance3.bpmn");
		String resourceName = "multiinstance3.bpmn";
		Deployment deployment = repositoryService.createDeployment().addBytes(resourceName, bytes).deploy();
		System.out.println(deployment);
	}
	@Test
	public void startProcessInstanceByKey() {
		String processDefinitionKey="ch6";
		runtimeService.startProcessInstanceByKey(processDefinitionKey);
	}
	/**
	 * 多实例任务节点跳出
	 */
	@Test
	public void executeCommand() {
		//shareniu-b
		//
		managementService.executeCommand(new ShareniuMuliInstanceJump("57502", "usertask1"));
	}
	/**
	 * 常规节点跳入到多实例任务节点
	 */
	@Test
	public void executeCommand1() {
		managementService.executeCommand(new ShareniuJumpCmd("47502", "usertask1"));
	}

	
	
	
	
	@Test
	public void complete() {
		String taskId="45002";
		taskService.complete(taskId);
	}
	
	
}
