package com.shareniu.shareniu_flowable_study.cmmn.entrycriterion;

import java.io.InputStream;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnManagementService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;
/**
 *TODO  terminate还没测试出来
 * String TERMINATE = "terminate"; 以及exit 可能只在stage中用 相关的貌似不可以使用
// * occur 只能在UserEventListenerActivityBehaviour 、MilestoneActivityBehavior、TimerEventListenerActivityBehaviour三个使用
 * terminate 以及exit 可能只在stage中用
 * 
 * @author jz
 *
 */
public class EntryCriterionTest {
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
			//	.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/entrycriterion/exitCriterion.cmmn.xml")
				//.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/entrycriterion/entryCriterionstart.cmmn.xml")
			//	.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/entrycriterion/entryCriterionterminate.cmmn.xml")
			//	.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/entrycriterion/entryCriterionexit.cmmn.xml")
				.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/entrycriterion/exitCriterioncomlete.cmmn.xml")
				.deploy().getId();
	}

	@Test
	public void getCaseDefinition() {
		String caseDefinitionId = "5f3440cd-3e17-11e8-a35f-3a9bd428756e";
		CaseDefinition caseDefinition = cmmnRepositoryService.getCaseDefinition(caseDefinitionId);
		System.out.println(caseDefinitionId);
		
	}

	@Test
	public void testOneNestedStage() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("exitCriterioncomlete")
				.start();

	}

	@Test
	public void triggerPlanItemInstance_terminate() {
		String caseInstanceId="82a751a9-3e4c-11e8-a92f-3a9bd428756e";
		
		cmmnRuntimeService.terminateCaseInstance(caseInstanceId);
		
	}
	@Test
	public void triggerPlanItemInstance() {
		cmmnRuntimeService.triggerPlanItemInstance("82a751a9-3e4c-11e8-a92f-3a9bd428756e");

	}
}
