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
package com.shareniu.shareniu_flowable_study.network.manualactivationrule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.List;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnManagementService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemDefinitionType;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstanceState;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.engine.common.api.FlowableIllegalArgumentException;
import org.flowable.engine.common.impl.util.CollectionUtil;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;

/**
 * 手册激活准则
 */
public class ManualActivationRuleTest  {

	
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
			//	.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/requiredrule/RequiredRuleTest.testOneRequiredHumanTask.cmmn")
			//	.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/requiredrule/RequiredRuleTest.testOneRequiredHumanTaskInStage.cmmn")
			//	.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/requiredrule/RequiredRuleTest.testNonAutoCompleteStageManualCompleteable.cmmn")
	//.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/requiredrule/RequiredRuleTest.testNonAutoCompleteStageManualCompleteable.cmmn")
	//.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/manualactivationrule/ManualActivationRuleTest.testSingleHumanTask.cmmn")
	.addClasspathResource("com/shareniu/shareniu_flowable_study/network/manualactivationrule/ManualActivationRuleTest.testInvalidDisable.cmmn")
				.deploy()
				.getId();
	}
	
	
	 @Test
	    public void testSingleHumanTask1() {
	        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testManuallyActivatedServiceTask")
	        		.variable("manual", true)
	        		.start();
	    }
	 @Test
	 public void testSingleHumanTask2() {
		 cmmnRuntimeService.triggerPlanItemInstance("02f86abc-451e-11e8-9cd7-a652884ed93a");
	 }
	 
	
	/**
	 * 
	 * <case id="testManualActivatedHumanTask" name="testManualActivatedHumanTask" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="The Task" definitionRef="sid-C5830C3C-BEB0-46D8-9BC4-4430BC8CDD29">
        <itemControl>
          <manualActivationRule></manualActivationRule>
        </itemControl>
      </planItem>
      <humanTask id="sid-C5830C3C-BEB0-46D8-9BC4-4430BC8CDD29" name="The Task"></humanTask>
    </casePlanModel>
  </case>
  1、planItem1 enabled 任务表没有数据
  2、调用cmmnRuntimeService.startPlanItemInstance(planItemInstance.getId());则状态变为active 任务表新增数据
	 */
    @Test
    public void testSingleHumanTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testManualActivatedHumanTask").start();
        
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals(PlanItemInstanceState.ENABLED, planItemInstance.getState());
        assertEquals(0, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        cmmnRuntimeService.startPlanItemInstance(planItemInstance.getId());
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals(PlanItemInstanceState.ACTIVE, planItemInstance.getState());
        
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("The Task", task.getName());
        
        cmmnTaskService.complete(task.getId());
       // assertCaseInstanceEnded(caseInstance);
    }
    /**
     * <case id="testDisableSingleHumanTask" name="testManualActivatedHumanTask" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="The Task" definitionRef="sid-C5830C3C-BEB0-46D8-9BC4-4430BC8CDD29">
        <itemControl>
          <manualActivationRule></manualActivationRule>
        </itemControl>
      </planItem>
      <humanTask id="sid-C5830C3C-BEB0-46D8-9BC4-4430BC8CDD29" name="The Task"></humanTask>
    </casePlanModel>
  </case>
    1、planItem1 enabled 任务表没有数据
    2、 cmmnRuntimeService.disablePlanItemInstance(planItemInstance.getId());整个实例就结束
         // Disabling the single plan item will terminate the case
     */
    @Test
    public void testDisableSingleHumanTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testDisableSingleHumanTask").start();
        
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals(PlanItemInstanceState.ENABLED, planItemInstance.getState());
        assertEquals(0, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        // Disabling the single plan item will terminate the case
        cmmnRuntimeService.disablePlanItemInstance(planItemInstance.getId());
    }
    /**
     * <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="A" definitionRef="sid-5204F45A-90BF-4BB0-B7C5-395FC6F748E8">
        <itemControl>
          <manualActivationRule></manualActivationRule>
        </itemControl>
      </planItem>
      <planItem id="planItem2" name="B" definitionRef="sid-800120C5-2746-41DA-BC78-3FF63729F2B5">
        <itemControl>
          <manualActivationRule></manualActivationRule>
        </itemControl>
      </planItem>
      <humanTask id="sid-5204F45A-90BF-4BB0-B7C5-395FC6F748E8" name="A"></humanTask>
      <humanTask id="sid-800120C5-2746-41DA-BC78-3FF63729F2B5" name="B"></humanTask>
    </casePlanModel>
    
    1、planItem1 planItem2都是enabled
    2、cmmnRuntimeService.disablePlanItemInstance(planItemInstance.getId());  planItem1变为disabled planItem2还是enabled
    3、cmmnRuntimeService.enablePlanItemInstance(planItemInstance.getId());   planItem1变为enabled planItem2还是enabled
      
     */
    @Test
    public void testDisableHumanTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testDisableHumanTask").start();
        
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).list();
        for (PlanItemInstance planItemInstance : planItemInstances) {
            assertEquals(PlanItemInstanceState.ENABLED, planItemInstance.getState());
        }
        assertEquals(0, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        PlanItemInstance planItemInstance = planItemInstances.get(0);
        cmmnRuntimeService.disablePlanItemInstance(planItemInstance.getId());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateDisabled().count());
        assertEquals(0, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());

        cmmnRuntimeService.enablePlanItemInstance(planItemInstance.getId());
        assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
    }
    /**
     * <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="A" definitionRef="sid-BF438153-CD35-4E9D-A2FB-AC92F0D9859F"></planItem>
      <planItem id="planItem6" definitionRef="sid-3C390E5A-0872-4030-AAE4-A76D137521FA">
        <itemControl>
          <manualActivationRule></manualActivationRule>
        </itemControl>
        <entryCriterion id="sid-4E1F4494-6FB4-4D4F-BA2C-D546D016ABB1" sentryRef="sentry2"></entryCriterion>
      </planItem>
      <sentry id="sentry2">
        <ifPart>
          <condition><![CDATA[${caseInstance.getVariable('variable') != null && variable == 'startStage'}]]></condition>
        </ifPart>
      </sentry>
      <humanTask id="sid-BF438153-CD35-4E9D-A2FB-AC92F0D9859F" name="A"></humanTask>
      <stage id="sid-3C390E5A-0872-4030-AAE4-A76D137521FA">
        <planItem id="planItem2" name="B" definitionRef="sid-05A43453-0C83-476C-A8AC-73A7DB5C3EBD"></planItem>
        <planItem id="planItem3" name="C" definitionRef="sid-A3B83F66-08A0-49ED-982E-E21F1A9B959D"></planItem>
        <planItem id="planItem5" definitionRef="sid-3EFACEDE-0B3A-4DC3-BEEE-435661A6686E">
          <itemControl>
            <manualActivationRule></manualActivationRule>
          </itemControl>
          <entryCriterion id="sid-5B7D5EB8-2B03-40F9-891F-D37DAE40DD39" sentryRef="sentry1"></entryCriterion>
        </planItem>
        <sentry id="sentry1">
          <planItemOnPart id="sentryOnPart1" sourceRef="planItem3">
            <standardEvent>complete</standardEvent>
          </planItemOnPart>
        </sentry>
        <humanTask id="sid-05A43453-0C83-476C-A8AC-73A7DB5C3EBD" name="B"></humanTask>
        <humanTask id="sid-A3B83F66-08A0-49ED-982E-E21F1A9B959D" name="C"></humanTask>
        <stage id="sid-3EFACEDE-0B3A-4DC3-BEEE-435661A6686E">
          <planItem id="planItem4" name="D" definitionRef="sid-7CA1F3FE-34E9-4D84-B6D5-64AFFA6DF03A"></planItem>
          <humanTask id="sid-7CA1F3FE-34E9-4D84-B6D5-64AFFA6DF03A" name="D"></humanTask>
        </stage>
      </stage>
    </casePlanModel>
    1.启动实例planItem1 active    planItem6 available
    2、设置变量variable为startStage planItem6变为enabled 因为planItem6绑定了manualActivationRule和<ifPart>
          <condition><![CDATA[${caseInstance.getVariable('variable') != null && variable == 'startStage'}]]></condition>
        </ifPart>两个
     3、激活cmmnRuntimeService.startPlanItemInstance则planItem6变为active     
     4、完成 c则planItem5变为enabled
     5、 cmmnRuntimeService.startPlanItemInstance(planItemInstance.getId());则planItem5变为active D任务为active
     6、 //TODO 
     */
    @Test
    public void testManualActivationWithSentries() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testManualActivationWithSentries").start();
        assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateAvailable().count());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("variable", "startStage"));
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        cmmnRuntimeService.startPlanItemInstance(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().singleResult().getId());
        
        assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateAvailable().count());
        assertEquals(3, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        // Completing C should enable the nested stage
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).taskName("C").singleResult();
        cmmnTaskService.complete(task.getId());
        
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
        assertEquals(3, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateAvailable().count());
        
        // Enabling the nested stage activates task D
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().singleResult();
        cmmnRuntimeService.startPlanItemInstance(planItemInstance.getId());
        
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals(3, tasks.size());
        assertEquals("A", tasks.get(0).getName());
        assertEquals("B", tasks.get(1).getName());
        assertEquals("D", tasks.get(2).getName());
        
        // Completing all the tasks ends the case instance
        for (Task t : cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list()) {
            cmmnTaskService.complete(t.getId());
        }
    }
    /**
     *   <casePlanModel id="casePlanModel">
      <planItem id="planItem2" definitionRef="sid-1EEF00C9-246C-4674-AAE1-D8919A61F60D">
        <itemControl>
          <manualActivationRule></manualActivationRule>
        </itemControl>
        <exitCriterion id="sid-80056FA1-716D-4171-850E-34750783A3E9" sentryRef="sentry1"></exitCriterion>
      </planItem>
      <planItem id="planItem3" name="A" definitionRef="sid-342A5898-2A13-4F54-8173-692300C8AD54"></planItem>
      <planItem id="planItem5" definitionRef="sid-30709E62-EDE1-4DDA-AC8A-A6D050E9E49F"></planItem>
      <sentry id="sentry1">
        <planItemOnPart id="sentryOnPart1" sourceRef="planItem3">
          <standardEvent>complete</standardEvent>
        </planItemOnPart>
      </sentry>
      <stage id="sid-1EEF00C9-246C-4674-AAE1-D8919A61F60D">
        <planItem id="planItem1" name="B" definitionRef="sid-18F2732F-F827-456D-A52A-FB6F46F40406"></planItem>
        <humanTask id="sid-18F2732F-F827-456D-A52A-FB6F46F40406" name="B"></humanTask>
      </stage>
      <humanTask id="sid-342A5898-2A13-4F54-8173-692300C8AD54" name="A"></humanTask>
      <stage id="sid-30709E62-EDE1-4DDA-AC8A-A6D050E9E49F">
        <planItem id="planItem4" name="C" definitionRef="sid-950AE038-47C7-49EF-93F0-5B8908F047DE"></planItem>
        <humanTask id="sid-950AE038-47C7-49EF-93F0-5B8908F047DE" name="C"></humanTask>
      </stage>
    </casePlanModel>
    1、完成A则sid-1EEF00C9-246C-4674-AAE1-D8919A61F60D 直接结束 之前是enabled。
    2、cmmnRuntimeService.startPlanItemInstance(planItemInstance.getId());
    //TODO 
     */
    @Test
    public void testExitEnabledPlanItem() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testExitEnabledPlanItem").start();
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
        
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals(2, tasks.size());
        assertEquals("A", tasks.get(0).getName());
        assertEquals("C", tasks.get(1).getName());
        
        // Completing task A will exit the enabled stage
        cmmnTaskService.complete(tasks.get(0).getId());
        assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
        
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("C", task.getName());
        
        cmmnTaskService.complete(task.getId());
    }
    /**
     *  <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="A" definitionRef="sid-7FF4C8D1-7F70-4B41-ABB3-689E7AE364EA"></planItem>
      <planItem id="planItem2" name="B" definitionRef="sid-E184BF0B-28D4-488D-8D78-FF98ED15531F">
        <itemControl>
          <manualActivationRule>
            <condition><![CDATA[${manual}]]></condition>
          </manualActivationRule>
        </itemControl>
      </planItem>
      <humanTask id="sid-7FF4C8D1-7F70-4B41-ABB3-689E7AE364EA" name="A"></humanTask>
      <task id="sid-E184BF0B-28D4-488D-8D78-FF98ED15531F" name="B" isBlocking="false" flowable:type="java" flowable:expression="${caseInstance.setVariable('variable', 'test')}"></task>
    </casePlanModel>
    1、启动实例 并且.variable("manual", true)则servicetask为enabled 
    2、  cmmnRuntimeService.startPlanItemInstance(planItemInstance.getId());则服务任务直接结束
    
    3、启动实例 并且.variable("manual", false)则servicetask直接完成，表明不使用  Manual Activation
     */
    @Test
    public void testManuallyActivatedServiceTask() {
        // Manual Activation enabled
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testManuallyActivatedServiceTask")
                .variable("manual", true)
                .start();
        assertEquals(1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateEnabled().planItemDefinitionType(PlanItemDefinitionType.SERVICE_TASK).singleResult();
        assertNotNull(planItemInstance);
        cmmnRuntimeService.startPlanItemInstance(planItemInstance.getId());
        assertEquals("test", cmmnRuntimeService.getVariable(caseInstance.getId(), "variable"));
        
        // Manual Activation disabled
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testManuallyActivatedServiceTask")
                .variable("manual", false)
                .start();
        assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
        assertEquals("test", cmmnRuntimeService.getVariable(caseInstance.getId(), "variable"));
    }
    /**
     * <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="Non-repeated task" definitionRef="sid-2FA5B8F4-E9CD-4612-855A-FC059EBE3A22"></planItem>
      <planItem id="planItem2" name="Repeated task" definitionRef="sid-0036B754-EDAA-4D0E-93FA-06840FDD697A">
        <itemControl>
          <repetitionRule flowable:counterVariable="repetitionCounter"></repetitionRule>
          <manualActivationRule></manualActivationRule>
        </itemControl>
        <exitCriterion id="sid-38FD1909-7C90-48C2-998F-8BE49EAF0034" sentryRef="sentry1"></exitCriterion>
      </planItem>
      <sentry id="sentry1">
        <ifPart>
          <condition><![CDATA[${stopRepeat}]]></condition>
        </ifPart>
      </sentry>
      <humanTask id="sid-2FA5B8F4-E9CD-4612-855A-FC059EBE3A22" name="Non-repeated task"></humanTask>
      <humanTask id="sid-0036B754-EDAA-4D0E-93FA-06840FDD697A" name="Repeated task"></humanTask>
    </casePlanModel>
    1、启动实例variable("stopRepeat", false) humantask为enabled
    2、  cmmnRuntimeService.startPlanItemInstance之后     humantask为active
    3、 // Completing the repeated task should again lead to an enabled task
            cmmnTaskService.complete(tasks.get(1).getId());
          完成任务之后重新生成Repeated task 并且状态为enabled。
     4、循环完毕之后， humantask为active
     5、设置CollectionUtil.singletonMap("stopRepeat", true)之后，则humantask结束。
     *     //TODO 
    @Test
    public void testRepeatedManualActivatedHumanTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testRepeatedManualActivatedHumanTask")
                .variable("stopRepeat", false)
                .start();
        
        assertEquals(1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
        cmmnRuntimeService.startPlanItemInstance(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().singleResult().getId());
        
        // This can go on forever (but testing 100 here), as it's repeated without stop
        for (int i = 0; i < 100; i++) {
            
            List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
            assertEquals(2, tasks.size());
            assertEquals("Non-repeated task", tasks.get(0).getName());
            assertEquals("Repeated task", tasks.get(1).getName());
            
            // Completing the repeated task should again lead to an enabled task
            cmmnTaskService.complete(tasks.get(1).getId());
            assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
            cmmnRuntimeService.startPlanItemInstance(cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().singleResult().getId());
        }
        
        assertEquals(2, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("stopRepeat", true));
        assertEquals(1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
    }
    /**
     *  <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="A" definitionRef="sid-5204F45A-90BF-4BB0-B7C5-395FC6F748E8"></planItem>
      <planItem id="planItem2" name="B" definitionRef="sid-800120C5-2746-41DA-BC78-3FF63729F2B5">
        <itemControl>
          <manualActivationRule></manualActivationRule>
        </itemControl>
      </planItem>
      <humanTask id="sid-5204F45A-90BF-4BB0-B7C5-395FC6F748E8" name="A"></humanTask>
      <humanTask id="sid-800120C5-2746-41DA-BC78-3FF63729F2B5" name="B"></humanTask>
    </casePlanModel>
    
    1、启动实例，B为enabled 
    cmmnRuntimeService.disablePlanItemInstance(planItemInstance.getId());报错不能disable 因为
     */
    @Test
    public void testInvalidDisable() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testInvalidDisable").start();
        
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        assertEquals(1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        try {
            PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().singleResult();
            cmmnRuntimeService.disablePlanItemInstance(planItemInstance.getId());
            fail();
        } catch (FlowableIllegalArgumentException e) {
            assertEquals("Can only disable a plan item instance which is in state ENABLED", e.getMessage());
        }
    }
    /**
     * <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="A" definitionRef="sid-5204F45A-90BF-4BB0-B7C5-395FC6F748E8"></planItem>
      <planItem id="planItem2" name="B" definitionRef="sid-800120C5-2746-41DA-BC78-3FF63729F2B5">
        <itemControl>
          <manualActivationRule></manualActivationRule>
        </itemControl>
      </planItem>
      <humanTask id="sid-5204F45A-90BF-4BB0-B7C5-395FC6F748E8" name="A"></humanTask>
      <humanTask id="sid-800120C5-2746-41DA-BC78-3FF63729F2B5" name="B"></humanTask>
    </casePlanModel>
    
    cmmnRuntimeService.enablePlanItemInstance(planItemInstance.getId());
    1、Can only enable a plan item instance which is in state AVAILABLE or DISABLED
     */
    @Test
    public void testInvalidEnable() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testInvalidEnable").start();
        
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        assertEquals(1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        try {
            PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().singleResult();
            cmmnRuntimeService.enablePlanItemInstance(planItemInstance.getId());
            fail();
        } catch (FlowableIllegalArgumentException e) {
            assertEquals("Can only enable a plan item instance which is in state AVAILABLE or DISABLED", e.getMessage());
        }
    }
    /**
     * <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="A" definitionRef="sid-5204F45A-90BF-4BB0-B7C5-395FC6F748E8"></planItem>
      <planItem id="planItem2" name="B" definitionRef="sid-800120C5-2746-41DA-BC78-3FF63729F2B5">
        <itemControl>
          <manualActivationRule></manualActivationRule>
        </itemControl>
      </planItem>
      <humanTask id="sid-5204F45A-90BF-4BB0-B7C5-395FC6F748E8" name="A"></humanTask>
      <humanTask id="sid-800120C5-2746-41DA-BC78-3FF63729F2B5" name="B"></humanTask>
    </casePlanModel>
    
    an only enable a plan item instance which is in state ENABLED
     */
    @Test
    public void testInvalidStart() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testInvalidStart").start();
        
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateEnabled().count());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        assertEquals(1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        try {
            PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().singleResult();
            cmmnRuntimeService.startPlanItemInstance(planItemInstance.getId());
            fail();
        } catch (FlowableIllegalArgumentException e) {
            assertEquals("Can only enable a plan item instance which is in state ENABLED", e.getMessage());
        }
    }
    
}
