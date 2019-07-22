package com.shareniu.shareniu_flowable_study.ch3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.common.impl.util.IoUtil;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DiagramLayout;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.junit.Before;
import org.junit.Test;

public class ProcessengineTest {
	ProcessEngine processEngine;
	RepositoryService repositoryService;
	IdentityService identityService;
	RuntimeService runtimeService;
	TaskService taskService;
	HistoryService historyService;

	@Before
	public void init() {
		processEngine = ProcessEngines.getDefaultProcessEngine();
		repositoryService = processEngine.getRepositoryService();
		identityService = processEngine.getIdentityService();
		runtimeService = processEngine.getRuntimeService();
		taskService = processEngine.getTaskService();
		historyService = processEngine.getHistoryService();
	}

	@Test
	public void addBytes() {
		byte[] bytes = IoUtil.readInputStream(ProcessengineTest.class.getClassLoader()
				.getResourceAsStream("com/shareniu/shareniu_flowable_study/ch3/leave.bpmn"), "leave.bpmn");
		String resourceName = "a.bpmn";
		Deployment deployment = repositoryService.createDeployment().addBytes(resourceName, bytes).deploy();
		System.out.println(deployment);
	}

	@Test
	public void validateProcess() {
		List<ValidationError> validateProcess = repositoryService.validateProcess(getBpmnModel());
		System.out.println(validateProcess);
	}

	@Test
	public void startProcessInstanceById1() {
		runtimeService.startProcessInstanceById("myProcess:2:40004");
	}

	@Test
	public void bm() {
		String processDefinitionId = "leave:1:4";
		BpmnModel bm = repositoryService.getBpmnModel(processDefinitionId);
		System.out.println(bm);
	}

	@Test
	public void dl() {
		String processDefinitionId = "leave:1:4";
		DiagramLayout dl = repositoryService.getProcessDiagramLayout(processDefinitionId);
		System.out.println(dl);
	}

	@Test
	public void getProcessDiagram() throws IOException {
		String processDefinitionId = "leave:1:4";
		InputStream in = repositoryService.getProcessDiagram(processDefinitionId);
		File destination = new File("/software/1.png");
		FileUtils.copyInputStreamToFile(in, destination);
	}

	/**
	 * 级联删除流程定义以及附属信息
	 */
	@Test
	public void deleteDeployment1() {
		String deploymentId = "32501";
		repositoryService.deleteDeployment(deploymentId, true);
	}

	/**
	 * 普通的删除方式
	 */
	@Test
	public void deleteDeployment() {
		String deploymentId = "32501";
		repositoryService.deleteDeployment(deploymentId);
	}

	@Test
	public void startProcessInstanceById() {
		String processDefinitionId = "parallelgateway:1:20004";
		ProcessInstance startProcessInstanceById = runtimeService.startProcessInstanceById(processDefinitionId);
		System.out.println(startProcessInstanceById);
	}

	@Test
	public void createTaskQuery() {
		String taskId = "17502";
		/// Task singleResult =
		/// taskService.createTaskQuery().taskId(taskId).singleResult();
		// System.out.println(singleResult);
		// taskService.setVariable(taskId, "a", "b");
		// taskService.setVariableLocal(taskId, "c", "c");

		// taskService.resolveTask(taskId);

		Map<String, Object> variables = new HashMap<>();
		Map<String, Object> transientVariables = new HashMap<>();
		variables.put("d", "d");
		transientVariables.put("f", "f");
		taskService.resolveTask(taskId, variables, transientVariables);
		// setTransientVariableLocal
	}

	@Test
	public void completeTask() {
		String taskId = "22511";
		taskService.complete(taskId);
	}

	@Test
	public void createHistoricTaskInstanceQuery() {
		String assignee = "003";
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().finished()
				.taskAssignee(assignee).list();
		for (HistoricTaskInstance ht : list) {
			System.out.println("任务处理人#######" + ht.getAssignee());
			System.out.println("任务名称#######" + ht.getName());
			System.out.println("任务优先级#######" + ht.getPriority());
			System.out.println("任务处理耗时#######" + ht.getDueDate());
			System.out.println("任务结束时间#######" + ht.getEndTime());
		}
	}

	/**
	 * 
	 */
	@Test
	public void createNativeProcessDefinitionQuery() {
		List<ProcessDefinition> list = repositoryService.createNativeProcessDefinitionQuery()
				.sql("select * from ACT_RE_PROCDEF").list();
		for (ProcessDefinition processDefinition : list) {
			System.out.println(processDefinition.getId());
		}

	}

	@Test
	public void changeDeploymentTenantId() {
		String deploymentId = "1";
		String newTenantId = "ccc";
		repositoryService.changeDeploymentTenantId(deploymentId, newTenantId);
	}

	/**
	 * 校验BpmnModel实例对象
	 */
	@Test
	public void testProcessValidator() {
		BpmnModel bpmnModel = getBpmnModel();
		ProcessValidatorFactory processValidatorFactory = new ProcessValidatorFactory();
		ProcessValidator defaultProcessValidator = processValidatorFactory.createDefaultProcessValidator();
		List<ValidationError> validates = defaultProcessValidator.validate(bpmnModel);
		System.out.println(validates);
	}

	@Test
	public void isFlowable5ProcessDefinition() {
		String processDefinitionId = "myProcess:1:2504";
		Boolean processDefinition = repositoryService.isFlowable5ProcessDefinition(processDefinitionId);
		System.out.println(processDefinition);
	}

	@Test
	public void startableByUser() {
		String processDefinitionId = "myProcess:1:2504";
		List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
				.startableByUser("shareniu").list();
		System.out.println(processDefinitions);
	}

	@Test
	public void getIdentityLinksForProcessDefinition() {
		String processDefinitionId = "myProcess:1:2504";
		List<IdentityLink> identityLinks = repositoryService.getIdentityLinksForProcessDefinition(processDefinitionId);
		System.out.println(identityLinks);
	}

	@Test
	public void addCandidateStarterUser() {
		String processDefinitionId = "myProcess:1:2504";
		String userId = "shareniu";
		repositoryService.addCandidateStarterUser(processDefinitionId, userId);
	}

	@Test
	public void potentialStarter() {
		String processDefinitionId = "myProcess:1:2504";
		List<User> authorizedUsers = identityService.getPotentialStarterUsers(processDefinitionId);
		System.out.println(authorizedUsers);
	}

	@Test
	public void isProcessDefinitionSuspended() {
		String processDefinitionId = "myProcess:1:2504";
		Boolean processDefinition = repositoryService.isProcessDefinitionSuspended(processDefinitionId);
		System.out.println(processDefinition);
	}

	private BpmnModel getBpmnModel() {
		BpmnModel bpmnModel = new BpmnModel();
		// 连线1
		SequenceFlow flow1 = new SequenceFlow();
		flow1.setId("flow1");
		flow1.setName("开始节点-->任务节点1");
		flow1.setSourceRef("start1");
		flow1.setTargetRef("userTask1");

		// 连线2
		SequenceFlow flow2 = new SequenceFlow();
		flow2.setId("flow2");
		flow2.setName("任务节点1-->任务节点2");
		flow2.setSourceRef("userTask1");
		flow2.setTargetRef("userTask2");

		// 连线3
		SequenceFlow flow3 = new SequenceFlow();
		flow3.setId("flow3");
		flow3.setName("任务节点2-->结束节点");
		flow3.setSourceRef("userTask2");
		flow3.setTargetRef("endEvent");
		// 开始节点
		StartEvent start = new StartEvent();
		start.setId("start1");
		start.setName("开始节点");
		start.setOutgoingFlows(Arrays.asList(flow1));
		// 任务节点1
		UserTask userTask1 = new UserTask();
		userTask1.setId("userTask1");
		userTask1.setName("任务节点1");
		userTask1.setIncomingFlows(Arrays.asList(flow1));
		userTask1.setOutgoingFlows(Arrays.asList(flow2));

		// 任务节点2
		UserTask userTask2 = new UserTask();
		userTask2.setId("userTask2");
		userTask2.setName("任务节点2");
		userTask2.setIncomingFlows(Arrays.asList(flow2));
		userTask2.setOutgoingFlows(Arrays.asList(flow3));
		// 结束节点
		EndEvent endEvent = new EndEvent();
		endEvent.setId("endEvent");
		endEvent.setName("结束节点");
		endEvent.setIncomingFlows(Arrays.asList(flow3));

		org.flowable.bpmn.model.Process process = new org.flowable.bpmn.model.Process();
		process.setId("process1");
		process.addFlowElement(start);
		process.addFlowElement(flow1);
		process.addFlowElement(flow2);
		process.addFlowElement(flow3);
		process.addFlowElement(userTask1);
		process.addFlowElement(userTask2);
		process.addFlowElement(endEvent);
		bpmnModel.addProcess(process);
		return bpmnModel;
	}

}
