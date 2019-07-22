package com.shareniu.v6.ch10;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.common.impl.util.IoUtil;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.engine.repository.Deployment;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.ch3.ProcessengineTest;
import com.shareniu.v6.ch10.cmd.GetProcessCmd;
import com.shareniu.v6.ch10.cmd.GetProcessDefinitionCacheEntryCmd;
import com.shareniu.v6.ch10.cmd.ShareniuJumpCmd;
import com.shareniu.v6.ch10.core.GenerateActivity;
import com.shareniu.v6.ch10.model.TaskModel;

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
				.getResourceAsStream("com/shareniu/v6/ch10/flowable.cfg.xml");
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
				ProcessengineTest.class.getClassLoader().getResourceAsStream("com/shareniu/v6/ch10/ch10.bpmn"),
				"ch7.bpmn");
		String resourceName = "ch7.bpmn";
		Deployment deployment = repositoryService.createDeployment().addBytes(resourceName, bytes).deploy();
		System.out.println(deployment);
	}

	

	/**
	 * 尝试添加一个任务节点
	 */
	@Test
	public void testAddOneTask() {

		// 根据任务的ID获取任务相关的数据
		String taskId = "97505";
		TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
		System.out.println(taskEntity);
		String processDefinitionId = taskEntity.getProcessDefinitionId();
		Process process = managementService.executeCommand(new GetProcessCmd(processDefinitionId));
		System.out.println(process);

		UserTask userTask = new UserTask();
		userTask.setId("d");
		userTask.setName("d");
		userTask.setAssignee("shareniu-d");
		userTask.setBehavior(createUserTaskBehavior(userTask));

		String targetActivityId = "shareniu-b";

		SequenceFlow sequenceFlow = new SequenceFlow();
		sequenceFlow.setId("shareniu-s1");
		userTask.setOutgoingFlows(Arrays.asList(sequenceFlow));
		sequenceFlow.setTargetRef(targetActivityId);
		sequenceFlow.setTargetFlowElement(process.getFlowElement(targetActivityId));
		process.addFlowElement(userTask);
		process.addFlowElement(sequenceFlow);
		ProcessDefinitionCacheEntry processDefinitionCacheEntry = managementService
				.executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId));
		processDefinitionCacheEntry.setProcess(process);
		Process process2 = managementService.executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId))
				.getProcess();
		System.out.println(process2);

		managementService.executeCommand(new ShareniuJumpCmd("97505", "d"));
	}

	/**
	 * 创建任务节点行为类
	 * 
	 * @param userTask
	 * @return
	 */
	private Object createUserTaskBehavior(UserTask userTask) {
		ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine
				.getProcessEngineConfiguration();
		// activityBehaviorFactory
		ActivityBehaviorFactory activityBehaviorFactory = processEngineConfiguration.getActivityBehaviorFactory();
		UserTaskActivityBehavior userTaskActivityBehavior = activityBehaviorFactory
				.createUserTaskActivityBehavior(userTask);
		return userTaskActivityBehavior;
	}

	/**
	 * 在完成任务之前需要我们去手工的更新一下之前加签的逻辑，目的就是更新缓存。
	 */
	@Test
	public void complete() {
		String taskId = "100002";
		TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
		System.out.println(taskEntity);
		String processDefinitionId = taskEntity.getProcessDefinitionId();
		Process process = managementService.executeCommand(new GetProcessCmd(processDefinitionId));
		System.out.println(process);

		UserTask userTask = new UserTask();
		userTask.setId("d");
		userTask.setName("d");
		userTask.setAssignee("shareniu-d");
		userTask.setBehavior(createUserTaskBehavior(userTask));

		String targetActivityId = "shareniu-b";

		SequenceFlow sequenceFlow = new SequenceFlow();
		sequenceFlow.setId("shareniu-s1");
		userTask.setOutgoingFlows(Arrays.asList(sequenceFlow));
		sequenceFlow.setTargetRef(targetActivityId);
		sequenceFlow.setTargetFlowElement(process.getFlowElement(targetActivityId));
		process.addFlowElement(userTask);
		process.addFlowElement(sequenceFlow);
		ProcessDefinitionCacheEntry processDefinitionCacheEntry = managementService
				.executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId));
		processDefinitionCacheEntry.setProcess(process);
		Process process2 = managementService.executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId))
				.getProcess();
		System.out.println(process2);
		taskService.complete(taskId);
	}
	@Test
	public void deleteProcessInstance() {
		runtimeService.deleteProcessInstance("20001", "不想用了");
	}
	@Test
	public void completeNormal() {
		String taskId = "122502";
		taskService.complete(taskId);
	}

	@After
	public void close() {
		processEngine.close();
	}
	@Test
	public void startProcessInstanceByKey() {
		String processDefinitionKey = "ch10";
		runtimeService.startProcessInstanceByKey(processDefinitionKey);
	}
	@Test
	public void testManyNode() {
		String taskId = "105005";
		TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
		System.out.println(taskEntity);
		String firstNodeId = "shareniu-a";
		String lastNodeId = "shareniu-b";
		List<TaskModel> list = new ArrayList<>();
		//TaskModel generateTaskModel_copy = GenerateActivity.generateTaskModel("shareniu-a_copy", "a", taskEntity.getAssignee());
		TaskModel generateTaskModel1 = GenerateActivity.generateTaskModel("d", "d", "d");
		//TaskModel generateTaskModel2 = GenerateActivity.generateTaskModel("e", "e", "e");
		//list.add(generateTaskModel_copy);
		list.add(generateTaskModel1);
		//list.add(generateTaskModel2);
		AddNode addNode = new AddNode();
		addNode.addMulitiNode(taskEntity.getProcessDefinitionId(), taskEntity.getProcessInstanceId(), processEngine,
				list, firstNodeId, lastNodeId, true,true,taskId,list.get(0).getId());
	}
	//1/a-------d---b--c
	
	//a-------d----f---b--c
	//a-------d----f---b--->f2--c
	//a-------d----f---b--->f2---f2_copy>>f3--c
	
	@Test
	public void testManyNode1() {
		String taskId = "115002";
		TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
		System.out.println(taskEntity);
		String firstNodeId = "f2";
		String lastNodeId = "shareniu-c";
		List<TaskModel> list = new ArrayList<>();
		//TaskModel generateTaskModel_copy = GenerateActivity.generateTaskModel("shareniu-a_copy", "a", taskEntity.getAssignee());
		TaskModel generateTaskModel_copy = GenerateActivity.generateTaskModel("f2_copy", "f2", "f2");
		TaskModel generateTaskModel1 = GenerateActivity.generateTaskModel("f3", "f3", "f3");
		list.add(generateTaskModel_copy);
		list.add(generateTaskModel1);
		AddNode addNode = new AddNode();
		addNode.addMulitiNode(taskEntity.getProcessDefinitionId(), taskEntity.getProcessInstanceId(), processEngine,
				list, firstNodeId, lastNodeId, true,true,taskId,list.get(0).getId());
	}

	@Test
	public void executeCommand() {
		ProcessDefinitionCacheEntry executeCommand = managementService.executeCommand(new GetProcessDefinitionCacheEntryCmd("ch10:1:4"));
		Process process = executeCommand.getProcess();
		System.out.println(process);
		FlowElement flowElement = executeCommand.getProcess().getFlowElement("e");
		
		System.out.println(flowElement);
	}

}
