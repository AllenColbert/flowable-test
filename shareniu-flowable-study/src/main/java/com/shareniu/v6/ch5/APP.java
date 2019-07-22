package com.shareniu.v6.ch5;

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
				ProcessengineTest.class.getClassLoader().getResourceAsStream("com/shareniu/v6/ch5/multiinstance3.bpmn"),
				"multiinstance3.bpmn");
		String resourceName = "multiinstance3.bpmn";
		Deployment deployment = repositoryService.createDeployment().addBytes(resourceName, bytes).deploy();
		System.out.println(deployment);
	}
	@Test
	public void startProcessInstanceByKey() {
		String processDefinitionKey="ch5";
		runtimeService.startProcessInstanceByKey(processDefinitionKey);
	}
	@Test
	public void executeCommand() {
		String processDefinitionId = "ch5:1:7504";
		Process process = managementService.executeCommand(new GetProcessCmd(processDefinitionId));
		List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);
		for (UserTask userTask : userTasks) {
			validate(userTask);
		}
	}

	public void validate(UserTask userTask) {
		/**
		 * 可以通过这种方式确定任务节点是否是多实例任务节点
		 */
		MultiInstanceLoopCharacteristics loopCharacteristics = userTask.getLoopCharacteristics();
		if (loopCharacteristics == null) {
			System.out.println(userTask.getId() + "不是多实例任务节点");
		} else {
			System.out.println(userTask.getId() + "是多实例任务节点");
		}
		/**
		 * 建议使用这种方式，比较靠谱
		 */
		Object behavior = userTask.getBehavior();
		if (behavior instanceof UserTaskActivityBehavior ) {
			System.out.println(userTask.getId() + "是普通任务节点");
		}
		
		if ((behavior instanceof MultiInstanceActivityBehavior)) {
			System.out.println(userTask.getId() + "是多实例任务节点");
        }
		
	}
	
	
	
	@Test
	public void executeCommand2() {
		managementService.executeCommand(new ShareniuMultiInstanceJump("20002","usertask1"));
	}
	
	@Test
	public void complete() {
		String taskId="25003";
		taskService.complete(taskId);
	}
	
	
}
