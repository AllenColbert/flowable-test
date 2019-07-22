package com.shareniu.shareniu_flowable_study.dmn;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.dmn.api.DmnHistoryService;
import org.flowable.dmn.api.DmnManagementService;
import org.flowable.dmn.api.DmnRepositoryService;
import org.flowable.dmn.api.DmnRuleService;
import org.flowable.dmn.engine.DmnEngine;
import org.flowable.dmn.engine.DmnEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.junit.Before;
import org.junit.Test;

public class DmnEngineConfigurationTest {

	DmnHistoryService dmnHistoryService;
	DmnManagementService dmnManagementService;
	DmnRepositoryService dmnRepositoryService;
	DmnRuleService ruleService;

	@Before
	public void init() {
		InputStream in = DmnEngineConfigurationTest.class.getClassLoader()
				.getResourceAsStream("com/shareniu/shareniu_flowable_study/dmn/flowable.dmn.cfg.xml");
		DmnEngineConfiguration dc = DmnEngineConfiguration.createDmnEngineConfigurationFromInputStream(in);
		DmnEngine de = dc.buildDmnEngine();
		dmnHistoryService = de.getDmnHistoryService();
		dmnManagementService = de.getDmnManagementService();
		dmnRepositoryService = de.getDmnRepositoryService();
		ruleService = de.getDmnRuleService();
		String name = de.getName();
		System.out.println(name);
	}

	@Test
	public void addClasspathResource() {
		String resource = "com/shareniu/shareniu_flowable_study/dmn/DeploymentTest.testDeployWithXmlSuffix.dmn.xml";
		dmnRepositoryService.createDeployment().addClasspathResource(resource).deploy();
	}

	

	@Test
	public void executeDecisionByKey() {
		Map<String, Object> inputVariables = new HashMap<>();
		inputVariables.put("input1", 10);
		List<Map<String, Object>> result = ruleService.executeDecisionByKey("decision", inputVariables);
		System.out.println(result);
	}
}
