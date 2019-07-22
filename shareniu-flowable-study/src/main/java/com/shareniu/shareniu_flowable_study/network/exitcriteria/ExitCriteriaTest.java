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
package com.shareniu.shareniu_flowable_study.network.exitcriteria;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
 * <case id="myCase"> <casePlanModel id="myPlanModel" name="My CasePlanModel">
 * 
 * <planItem id="taskA" name="A" definitionRef="taskDefinition" />
 * <planItem id="taskB" name="B" definitionRef="taskDefinition">
 * <exitCriterion sentryRef="sentry" /> </planItem>
 * 
 * <sentry id="sentry"> <planItemOnPart sourceRef="taskA">
 * <standardEvent>complete</standardEvent> </planItemOnPart> </sentry>
 * 
 * <task id="taskDefinition" name="A" isBlocking="true" />
 * 
 * </casePlanModel> </case> 1、当taskA完成则taskB也完成了。 2、taskB 完成 taskA不会完成
 * 3、isBlocking="true"则
 * ACT_CMMN_RU_PLAN_ITEM_INST表中有数据，如果isBlocking="false"则ACT_CMMN_RU_PLAN_ITEM_INST表中没有数据
 * 
 * 
 * 
 *  <case id="myCase">
        <casePlanModel id="myPlanModel" name="My CasePlanModel">

            <planItem id="taskA" name="A" definitionRef="taskDefinition" />
            <planItem id="taskB" name="B" definitionRef="taskDefinition" />
            <planItem id="taskC" name="C" definitionRef="taskDefinition" />
            <planItem id="taskD" name="D" definitionRef="taskDefinition" />
            <planItem id="taskE" name="E" definitionRef="taskDefinition">
                <exitCriterion sentryRef="sentry" />
            </planItem>

            <sentry id="sentry">
                <planItemOnPart sourceRef="taskA">
                    <standardEvent>complete</standardEvent>
                </planItemOnPart>
                <planItemOnPart sourceRef="taskB">
                    <standardEvent>complete</standardEvent>
                </planItemOnPart>
                <planItemOnPart sourceRef="taskC">
                    <standardEvent>complete</standardEvent>
                </planItemOnPart>
                <planItemOnPart sourceRef="taskD">
                    <standardEvent>complete</standardEvent>
                </planItemOnPart>
            </sentry>

            <task id="taskDefinition" name="A" isBlocking="true" />

        </casePlanModel>
    </case>
    1、四个节点指向同一个节点，必须4个节点同时完成，最后一个节点才能完成，如果一个个完成则最后一个节点需要手动完成
 */
public class ExitCriteriaTest {

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
				// "com/shareniu/shareniu_flowable_study/cmmn/exitcriteria/ExitCriteriaTest.testSimpleExitCriteriaBlocking.cmmn"
				// "com/shareniu/shareniu_flowable_study/cmmn/exitcriteria/ExitCriteriaTest.testSimpleExitCriteriaNonBlocking.cmmn"
				"com/shareniu/shareniu_flowable_study/network/exitcriteria/myCase22.exit.cmmn2.xml")
				.deploy().getId();
	}

	
	
	@Test
	public void testSimpleExitCriteriaBlocking() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase22").start();
		List<PlanItemInstance> planItems = cmmnRuntimeService.createPlanItemInstanceQuery()
				.caseInstanceId(caseInstance.getId()).orderByName().asc().list();
		//assertEquals(2, planItems.size());
	//	assertEquals("A", planItems.get(0).getName());
		//assertEquals("B", planItems.get(1).getName());

		// Completing A should trigger exit criteria of B. Case completes.
		// cmmnRuntimeService.triggerPlanItemInstance(planItems.get(0).getId());
		// assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
		// assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
		// assertEquals(1,
		// cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
	}
	/**
	 * 启动实例数据库表产生了两条数据，并且都是激活状态。
	 * 如果首先完成A，则A完成之后，B数据的状态不变化。
	 * 如果首先完成B，则B完成之后，A也随之结束。
	 * 因为exitCriterion是complete exitCriterion是 B 连接的A  所以B结束执行之后，触发A完成
	 */
	@Test
	public void testSimpleExitCriteriaBlocking_1() {
		cmmnRuntimeService.triggerPlanItemInstance("a19a76c2-46f0-11e8-bdcf-a652884ed93a");
	}

	@Test
	public void testSimpleExitCriteriaNonBlocking() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase22").start();
	}

	@Test
	public void testSimpleExitCriteriaWithMultipleOnParts3() {
		cmmnRuntimeService.triggerPlanItemInstance("05a17874-46f5-11e8-9b5b-a652884ed93a");
	}

	
	
	@Test
	public void testSimpleExitCriteriaWithMultipleOnParts1() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
		List<PlanItemInstance> planItems = cmmnRuntimeService.createPlanItemInstanceQuery()
				.caseInstanceId(caseInstance.getId()).orderByName().asc().list();
		assertEquals(5, planItems.size());
		String[] expectedNames = new String[] { "A", "B", "C", "D" };
		for (int i = 0; i < 4; i++) {
			assertEquals(expectedNames[i], planItems.get(i).getName());
			cmmnRuntimeService.triggerPlanItemInstance(planItems.get(i).getId());
		}

		assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
//		assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
	//	assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
	}
	//TODO 
	@Test
	public void testSimpleExitCriteriaWithMultipleOnParts2() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
		List<PlanItemInstance> planItems = cmmnRuntimeService.createPlanItemInstanceQuery()
				.caseInstanceId(caseInstance.getId()).orderByName().asc().list();
		assertEquals(5, planItems.size());

		// Triggering A and B exits C, which triggers the exit of D and E
		cmmnRuntimeService.triggerPlanItemInstance(planItems.get(0).getId());
		cmmnRuntimeService.triggerPlanItemInstance(planItems.get(1).getId());

		assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
		assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
		assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
	}
//TODO 
	@Test
	public void testExitPlanModelOnMilestoneReached() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
		List<PlanItemInstance> planItems = cmmnRuntimeService.createPlanItemInstanceQuery()
				.planItemInstanceState(PlanItemInstanceState.AVAILABLE).orderByName().asc().list();
		assertEquals(2, planItems.size());
		assertEquals("D", planItems.get(0).getName());
		assertEquals("The Milestone", planItems.get(1).getName());

		planItems = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
				.planItemInstanceState(PlanItemInstanceState.ACTIVE).orderByName().asc().list();
		assertEquals(3, planItems.size());
		String[] expectedNames = new String[] { "A", "B", "C" };
		for (int i = 0; i < 3; i++) {
			assertEquals(expectedNames[i], planItems.get(i).getName());
		}

		// Triggering A and B enabled the milestone
		// Completing the milestone exits the whole planmodel

		cmmnRuntimeService.triggerPlanItemInstance(planItems.get(0).getId());
		cmmnRuntimeService.triggerPlanItemInstance(planItems.get(1).getId());

		assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
		assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
		assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
	}

	@Test
	public void testExitThreeNestedStagesThroughPlanModel() {
		cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
		assertEquals(8, cmmnRuntimeService.createPlanItemInstanceQuery().count());

		PlanItemInstance taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("Task A")
				.singleResult();
		assertNotNull(taskA);
		cmmnRuntimeService.triggerPlanItemInstance(taskA.getId());

		assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
		assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
		assertEquals(1, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
	}

	@Test
	public void testExitPlanModelWithNestedCaseTasks() {

		String oneTaskCaseDeploymentId = cmmnRepositoryService.createDeployment()
				.addClasspathResource("org/flowable/cmmn/test/runtime/oneTaskCase.cmmn").deploy().getId();

		cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
		assertEquals(4, cmmnRuntimeService.createCaseInstanceQuery().count());
		assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());

		// Trigger the plan item should satisfy the sentry of the plan model exit
		// criteria
		PlanItemInstance taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("Task A")
				.singleResult();
		assertNotNull(taskA);
		cmmnRuntimeService.triggerPlanItemInstance(taskA.getId());

		assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().count());
		assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
		assertEquals(4, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());

		cmmnRepositoryService.deleteDeployment(oneTaskCaseDeploymentId, true);
	}

}
