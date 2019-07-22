/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shareniu.shareniu_flowable_study.network.stage;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnManagementService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstanceState;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;

/**
 * 
 * 
 * stage如果完成则 stage下面的所有planItem都会结束
 * 
 * 如果stage下面的所有planItem都结束，那么stage也会完成。
 * 
 * stage如果完成则下面的所有planItem都会完成，包括子的stage。 StageActivityBehavior
 * TaskActivityBehavior
 * 
 * 
 * <task isBlocking="true" /> if (!evaluateIsBlocking(planItemInstanceEntity)) {
 * CommandContextUtil.getAgenda(commandContext).planCompletePlanItemInstanceOperation((PlanItemInstanceEntity)
 * planItemInstanceEntity); } 如果false,则直接完成
 * 
 * isBlocking="false"则启动之后，立即结束
 */
public class StageTest {

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
		oneTaskCaseDeploymentId = cmmnRepositoryService.createDeployment().addClasspathResource(
				// "com/shareniu/shareniu_flowable_study/cmmn/stage/StageTest.testOneNestedStage.cmmn")
				// "com/shareniu/shareniu_flowable_study/cmmn/stage/StageTest.testOneNestedStageNonBlocking.cmmn")
				// "com/shareniu/shareniu_flowable_study/cmmn/stage/StageTest.testTwoNestedStages.cmmn")
				// "com/shareniu/shareniu_flowable_study/cmmn/stage/StageTest.testTwoNestedStagesNonBlocking.cmmn")
				// "com/shareniu/shareniu_flowable_study/cmmn/stage/StageTest.testThreeNestedStages.cmmn")
				// "com/shareniu/shareniu_flowable_study/cmmn/stage/StageTest.testThreeNestedStagesNonBlocking.cmmn")
				"com/shareniu/shareniu_flowable_study/network/stage/StageTest.testTerminateCaseInstanceNestedStages.cmmn")
				.deploy().getId();
	}

	

	@Test
	public void testOneNestedStageNonBlocking() {
		CaseInstance start = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("shareniu-myCase").start();
		System.out.println(start);
	}
	
	

	@Test
	public void testTwoNestedStages1() {
		
		String planItemInstanceId="43c9abea-42c9-11e8-a34e-165c82b1966a";
		cmmnRuntimeService
		.triggerPlanItemInstance(planItemInstanceId);
		
		
	}

	
	@Test
	public void testTerminateCaseInstanceNestedStages() {

		String caseInstanceId="3740fa31-42bc-11e8-9f2a-165c82b1966a";
		cmmnRuntimeService.terminateCaseInstance(caseInstanceId);
	}
	
	@Test
	public void testTwoNestedStages() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
		List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery()
				.caseInstanceId(caseInstance.getId()).orderByName().asc().list();
		assertEquals(6, planItemInstances.size());
		String[] expectedNames = new String[] { "Stage One", "Stage Two", "Task Four", "Task One", "Task Three",
				"Task Two" };
		for (int i = 0; i < planItemInstances.size(); i++) {
			assertEquals(expectedNames[i], planItemInstances.get(i).getName());
		}

		// Complete inner nested stage
		cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(2).getId());
		planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
				.planItemInstanceState(PlanItemInstanceState.ACTIVE).orderByName().asc().list();
		assertEquals(4, planItemInstances.size());
		expectedNames = new String[] { "Stage One", "Task One", "Task Three", "Task Two" };
		for (int i = 0; i < planItemInstances.size(); i++) {
			assertEquals(expectedNames[i], planItemInstances.get(i).getName());
		}
	}

	@Test
	public void testTwoNestedStages_testBasicNonBlocking() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
		System.out.println(caseInstance);

		System.out.println(cmmnRuntimeService.createCaseInstanceQuery().count());
		System.out.println(cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
		System.out.println(cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
	}

	@Test
	public void testTwoNestedStages_triggerPlanItemInstance() {
		// Complete inner nested stage
		cmmnRuntimeService.triggerPlanItemInstance("088e4bc7-3bb6-11e8-a9a1-c6debfa7f78c");
	}

	/**
	 * //============================================================
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * //============================================================
	 */
	@Test
	public void testTwoNestedStagesNonBlocking() {
		CaseInstance start = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();

		// assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
		// assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
		// assertEquals(1,
		// cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
	}

	/**
	 * //============================================================
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * //============================================================
	 */
	@Test
	public void testThreeNestedStages() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
		List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery()
				.caseInstanceId(caseInstance.getId()).orderByName().asc().list();
		assertEquals(8, planItemInstances.size());
		String[] expectedNames = new String[] { "Stage One", "Stage Three", "Stage Two", "Task Five", "Task Four",
				"Task One", "Task Three", "Task Two" };
		for (int i = 0; i < planItemInstances.size(); i++) {
			assertEquals(expectedNames[i], planItemInstances.get(i).getName());
		}

		// Complete inner nested stage (3th stage)
		cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(3).getId());
		planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
				.planItemInstanceState(PlanItemInstanceState.ACTIVE).orderByName().asc().list();
		assertEquals(6, planItemInstances.size());
		expectedNames = new String[] { "Stage One", "Stage Two", "Task Four", "Task One", "Task Three", "Task Two" };
		for (int i = 0; i < planItemInstances.size(); i++) {
			assertEquals(expectedNames[i], planItemInstances.get(i).getName());
		}

		// Commplete inner nested stage (2nd stage)
		cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(2).getId());
		planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
				.planItemInstanceState(PlanItemInstanceState.ACTIVE).orderByName().asc().list();
		assertEquals(4, planItemInstances.size());
		expectedNames = new String[] { "Stage One", "Task One", "Task Three", "Task Two" };
		for (int i = 0; i < planItemInstances.size(); i++) {
			assertEquals(expectedNames[i], planItemInstances.get(i).getName());
		}

		cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(2).getId());
		cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(3).getId());

		planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
				.planItemInstanceState(PlanItemInstanceState.ACTIVE).orderByName().asc().list();
		assertEquals(1, planItemInstances.size());
		expectedNames = new String[] { "Task One" };
		for (int i = 0; i < planItemInstances.size(); i++) {
			assertEquals(expectedNames[i], planItemInstances.get(i).getName());
		}

		cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
		assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
		// assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
		// assertEquals(1,
		// cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
	}

	/**
	 * /** //============================================================
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * //============================================================
	 * 
	 */
	@Test
	public void testThreeNestedStagesNonBlocking() {
		cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
		// assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
		// assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
		// assertEquals(1,
		// cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
	}

	@Test
	public void testThreeNestedStagesWithCriteria() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
		List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery()
				.caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).orderByName()
				.asc().list();
		assertEquals(4, planItemInstances.size());
		String[] expectedNames = new String[] { "Stage A", "Task A", "Task B", "Task C" };
		for (int i = 0; i < planItemInstances.size(); i++) {
			assertEquals(expectedNames[i], planItemInstances.get(i).getName());
		}

		// Completing A and B triggers stage 2
		cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(1).getId());// planItem1 Task A
		cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(2).getId());// planItem3 Task B

		planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
				.planItemInstanceState(PlanItemInstanceState.ACTIVE).orderByName().asc().list();
		assertEquals(6, planItemInstances.size());
		expectedNames = new String[] { "Stage A", "Stage B", "Stage C", "Task C", "Task D", "Task E" };
		for (int i = 0; i < planItemInstances.size(); i++) {
			assertEquals(expectedNames[i], planItemInstances.get(i).getName());
		}

		// Triggering Task C should exit stage 2, which should also exit the inner
		// nested stage
		cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(3).getId());

		assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
		assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
		assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
	}
	/**
	 * 启动实例
	 */
	@Test
	public void testThreeNestedStagesWithCriteria_1() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
		List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery()
				.caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).orderByName()
				.asc().list();

	}

	/**
	 * 完成a跟B
	 */
	@Test
	public void testThreeNestedStagesWithCriteria_2() {
		cmmnRuntimeService.triggerPlanItemInstance("aee11468-3bf6-11e8-a594-c6debfa7f78c");// planItem1 Task A
		cmmnRuntimeService.triggerPlanItemInstance("aee07826-3bf6-11e8-a594-c6debfa7f78c");// planItem1 Task A

	}
	/**
	 * 完成c
	 */
	@Test
	public void testThreeNestedStagesWithCriteria_3() {
		cmmnRuntimeService.triggerPlanItemInstance("aee11469-3bf6-11e8-a594-c6debfa7f78c");// planItem1 Task A
		
	}


}
