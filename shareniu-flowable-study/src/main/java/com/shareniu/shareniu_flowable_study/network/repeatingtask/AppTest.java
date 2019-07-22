package com.shareniu.shareniu_flowable_study.network.repeatingtask;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnManagementService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.repository.CmmnDeployment;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstanceState;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.engine.common.api.repository.EngineResource;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;

public class AppTest {
	CmmnEngine ce;
	CmmnHistoryService cmmnHistoryService;
	CmmnManagementService cmmnManagementService;
	CmmnRepositoryService cmmnRepositoryService;
	CmmnRuntimeService cmmnRuntimeService;
	CmmnTaskService cmmnTaskService;
	protected String oneTaskCaseDeploymentId;

	@Before
	public void init() {
		InputStream in = CaseTaskTest.class.getClassLoader()
				.getResourceAsStream("com/shareniu/shareniu_flowable_study/cmmn/flowable.cmmn.cfg.xml");
		CmmnEngineConfiguration dc = CmmnEngineConfiguration.createCmmnEngineConfigurationFromInputStream(in);
		ce = dc.buildCmmnEngine();
		cmmnHistoryService = ce.getCmmnHistoryService();
		cmmnManagementService = ce.getCmmnManagementService();
		cmmnRepositoryService = ce.getCmmnRepositoryService();
		cmmnRuntimeService = ce.getCmmnRuntimeService();
		cmmnTaskService = ce.getCmmnTaskService();
	}

	@Test
	public void deployOneTaskCaseDefinition() {
		oneTaskCaseDeploymentId = cmmnRepositoryService.createDeployment()
			//	.addClasspathResource("com/shareniu/shareniu_flowable_study/network/scriptcase/shareniu-scriptCase.cmmn.xml")
		.addClasspathResource("com/shareniu/shareniu_flowable_study/network/repeatingtask/RepetitionRuleTest.testCustomCounterVariable.cmmn")
				.deploy().getId();
	}

	@Test
	public void testBasicNonBlocking() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
				.caseDefinitionKey("repeatingTask")
				.start();
	}
	
	@Test
	public void triggerPlanItemInstance() {
		String caseInstanceId = "9441e70e-4225-11e8-9056-52486683f3d2";
		PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
				.caseInstanceId(caseInstanceId).planItemInstanceElementId("planItem1")
				.planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
		System.out.println(planItemInstance);
		cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
	}

	
}
