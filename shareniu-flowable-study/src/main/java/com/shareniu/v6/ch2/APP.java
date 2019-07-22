package com.shareniu.v6.ch2;

import java.io.InputStream;
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
				.getResourceAsStream("com/shareniu/v6/ch2/flowable.cfg.xml");
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
				.getResourceAsStream("com/shareniu/v6/ch2/shareniu-helloWord.bpmn20.xml"), "f.bpmn20.xml");
		String resourceName = "f.bpmn";
		Deployment deployment = repositoryService.createDeployment().addBytes(resourceName, bytes).deploy();
		System.out.println(deployment);
	}

	@Test
	public void getBpmnModel() {
		String processDefinitionId = "shareniu-helloWord:2:2504";
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		System.out.println(bpmnModel);
		Process process = bpmnModel.getProcesses().get(0);
		List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);
		
		for (UserTask userTask : userTasks) {
			System.out.println(userTask.getId());
			System.out.println(userTask.getName());
			//String extIdValue = userTask.getAttributeValue("shareniu", "extId");
			//System.out.println(extIdValue);
			
			Map<String, List<ExtensionAttribute>> attributes = userTask.getAttributes();
			
			Set<Entry<String,List<ExtensionAttribute>>> entrySet = attributes.entrySet();
			for (Entry<String, List<ExtensionAttribute>> entry : entrySet) {
				String key = entry.getKey();
				List<ExtensionAttribute> value = entry.getValue();
				System.out.println("key:"+key);
				System.out.println("val:"+value);
				for (ExtensionAttribute extensionAttribute : value) {
					String name = extensionAttribute.getName();
					String namespace = extensionAttribute.getNamespace();
					String value2 = extensionAttribute.getValue();
					String namespacePrefix = extensionAttribute.getNamespacePrefix();
					System.out.println("name:"+name);
					System.out.println("namespace:"+namespace);
					System.out.println("val2:"+value2);
					System.out.println("namespacePrefix:"+namespacePrefix);
					
				}
			
			}
		}
	}
	@Test
	public void getBpmnModel1() {
		String processDefinitionId = "shareniu-helloWord:2:2504";
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		System.out.println(bpmnModel);
		Process process = bpmnModel.getProcesses().get(0);
		List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);
		for (UserTask userTask : userTasks) {
			System.out.println(userTask.getId());
			System.out.println(userTask.getName());
			String extIdValue = userTask.getAttributeValue("http://www.shareniu.com/", "shareniuExtId");
			System.out.println(extIdValue);
			String extNameValue = userTask.getAttributeValue("http://www.shareniu.com/", "shareniuExtNname");
			System.out.println(extNameValue);
		}
		
	}
}
