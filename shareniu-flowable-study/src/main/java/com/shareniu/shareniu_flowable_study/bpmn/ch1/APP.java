package com.shareniu.shareniu_flowable_study.bpmn.ch1;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
				.getResourceAsStream("com/shareniu/shareniu_flowable_study/bpmn/ch1/flowable.cfg.xml");
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
		byte[] bytes = IoUtil.readInputStream(ProcessengineTest.class.getClassLoader()
				.getResourceAsStream("com/shareniu/shareniu_flowable_study/bpmn/ch1/f.bpmn20.xml"), "f.bpmn20.xml");
		String resourceName = "f.bpmn";
		Deployment deployment = repositoryService.createDeployment().addBytes(resourceName, bytes).deploy();
		System.out.println(deployment);
	}

	@Test
	public void getBpmnModel() {
		String processDefinitionId = "f:1:4";
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		System.out.println(bpmnModel);
		Process process = bpmnModel.getProcesses().get(0);
		List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);

		userTasks.forEach(action -> {
			System.out.println(action.getAssignee());
			System.out.println(action.getId());
			// getAttributes
			Map<String, List<ExtensionAttribute>> attributes = action.getAttributes();
			String shareniuext = attributes.get("shareniuext").get(0).getValue();
			System.out.println(shareniuext);
			String shareniuextboolen = attributes.get("shareniuextboolen").get(0).getValue();
			System.out.println(shareniuextboolen);
			// Set<Entry<String,List<ExtensionAttribute>>> entrySet = attributes.entrySet();
			// for (Entry<String, List<ExtensionAttribute>> entry : entrySet) {
			// String key = entry.getKey();
			// List<ExtensionAttribute> value = entry.getValue();
			// System.out.println("key:"+key);
			// System.out.println("value:"+value);
			// for (ExtensionAttribute extensionAttribute : value) {
			// System.out.println(extensionAttribute.getNamespace());
			// System.out.println(extensionAttribute.getNamespacePrefix());
			// System.out.println(extensionAttribute.getValue());
			// System.out.println(extensionAttribute.getClass());
			// }
			// }
		});

	}
}
