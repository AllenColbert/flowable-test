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
package com.shareniu.shareniu_flowable_study.network.ifpart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.io.Serializable;
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
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.engine.common.impl.util.CollectionUtil;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;

/**
 * 
 * <case id="testIfPartOnly" name="testIfPartOnly">
    <casePlanModel id="casePlanModel" name="testIfPartOnly">
      <planItem id="planItem1" name="A" definitionRef="sid-8C0F0CBA-45D1-480C-80F4-F047FDDD4935"></planItem>
      <planItem id="planItem2" name="B" definitionRef="sid-5F978681-D5F8-4C1A-A00C-4120257D15CD">
        <entryCriterion id="sid-0EFE24A3-E64D-48EC-9764-F60B837480CA" sentryRef="sentry1"></entryCriterion>
      </planItem>
      <sentry id="sentry1">
        <ifPart>
          <condition><![CDATA[${caseInstance.getVariable('variable') != null && variable}]]></condition>
        </ifPart>
      </sentry>
      <task id="sid-8C0F0CBA-45D1-480C-80F4-F047FDDD4935" name="A"></task>
      <task id="sid-5F978681-D5F8-4C1A-A00C-4120257D15CD" name="B"></task>
    </casePlanModel>
  </case>
  1、启动实例的时候，如果condition计算结果为true，则实例表有planItem2
  2、启动实例的时候，如果condition计算结果为false，则实例表没有planItem2
  3、启动实例的时候，如果condition计算结果为false，则实例表没有planItem2 ，谨紧接着开始设置变量使conditio为true，则实例表有planItem2。
  
  ========================================================================
  <case id="testSimpleCondition" name="testSimpleCondition">
    <casePlanModel id="casePlanModel" name="testSimpleCondition">
      <planItem id="planItem1" name="B" definitionRef="sid-016B7F87-A130-498A-98F7-1DBDF70F1AF6">
        <entryCriterion id="sid-494B3CC0-04B9-4D9B-B9AB-F8766BAD6EE2" sentryRef="sentry1"></entryCriterion>
      </planItem>
      <planItem id="planItem2" name="A" definitionRef="sid-C6172C31-3CD5-459F-872B-7AB6371000BE"></planItem>
      <sentry id="sentry1">
        <planItemOnPart id="sentryOnPart1" sourceRef="planItem2">
          <standardEvent>complete</standardEvent>
        </planItemOnPart>
        <ifPart>
          <condition><![CDATA[${conditionVariable}]]></condition>
        </ifPart>
      </sentry>
      <task id="sid-016B7F87-A130-498A-98F7-1DBDF70F1AF6" name="B"></task>
      <task id="sid-C6172C31-3CD5-459F-872B-7AB6371000BE" name="A"></task>
    </casePlanModel>
  </case>
 
  
  1、启动不设置变量 直接报错
  2、启动设置变量为false在直接实例结束
  3、启动设置变量为false并且isBlocking="true"，那么则实例表有planItem2，planItem1
  4、完成planItem1 ，conditionVariable为false 则planItem2不会被完成，
                  我们再次设置conditionVariable为true，planItem2还是available状态
               只能调用cmmnRuntimeService.completeCaseInstance("04a8d06d-3c5c-11e8-990f-c6debfa7f78c");完成整个实例
  5、结论：只有planItem1完成并且conditionVariable计算结果为true，才能保证planItem2可以正常运行。
  
  
  
   <casePlanModel id="casePlanModel" name="testSimpleCondition">
      <planItem id="planItem1" name="B" definitionRef="sid-016B7F87-A130-498A-98F7-1DBDF70F1AF6">
        <entryCriterion id="sid-494B3CC0-04B9-4D9B-B9AB-F8766BAD6EE2" sentryRef="sentry1"></entryCriterion>
      </planItem>
      <planItem id="planItem2" name="A" definitionRef="sid-C6172C31-3CD5-459F-872B-7AB6371000BE"></planItem>
      <sentry id="sentry1">
        <planItemOnPart id="sentryOnPart1" sourceRef="planItem2">
          <standardEvent>complete</standardEvent>
        </planItemOnPart>
        <ifPart>
          <condition><![CDATA[${caseInstance.getVariable('conditionVariable') != null && conditionVariable}]]></condition>
        </ifPart>
      </sentry>
      <task id="sid-016B7F87-A130-498A-98F7-1DBDF70F1AF6" name="B"></task>
      <task id="sid-C6172C31-3CD5-459F-872B-7AB6371000BE" name="A"></task>
    </casePlanModel>
    
    1、必须planItem2完成并且${caseInstance.getVariable('conditionVariable') != null && conditionVariable} true才会执行planItem1
    
    
    
    
    
    
    
    
     <sentry id="sentry1">
        <ifPart>
          <condition><![CDATA[${someBean.isSatisfied()}]]></condition>
        </ifPart>
      </sentry>
    
      /**
     * 
     * 1、如果someBean.isSatisfied()为false，则planItem2不能使用  状态是available
     * 可以调用  cmmnRuntimeService.evaluateCriteria(caseInstance.getId()); 进行激活 变为active
     * 
     * 2、如果someBean.isSatisfied()为true，则planItem2可以使用
     *
     *
     *<sentry id="sentry1">
        <planItemOnPart id="sentryOnPart1" sourceRef="planItem1">
          <standardEvent>complete</standardEvent>
        </planItemOnPart>
        <planItemOnPart id="sentryOnPart2" sourceRef="planItem2">
          <standardEvent>complete</standardEvent>
        </planItemOnPart>
        <planItemOnPart id="sentryOnPart3" sourceRef="planItem3">
          <standardEvent>complete</standardEvent>
        </planItemOnPart>
        <ifPart>
          <condition><![CDATA[${caseInstance.getVariable('conditionVariable') != null && conditionVariable}]]></condition>
        </ifPart>
      </sentry>
      1、多个的话，必须是每一个sourceRef都完成并且满足条件才执行下一个。
      
      
      
       <casePlanModel id="casePlanModel" name="testExitPlanModelWithIfPart">
      <planItem id="planItem1" name="A" definitionRef="sid-F59B44F4-383E-4612-9469-5A2200B2E0BF"></planItem>
      <planItem id="planItem2" name="B" definitionRef="sid-8FFD88A7-4977-440B-B6F3-498D0C0174B1"></planItem>
      <sentry id="sentry1">
        <planItemOnPart id="sentryOnPart1" sourceRef="planItem1">
          <standardEvent>complete</standardEvent>
        </planItemOnPart>
        <ifPart>
          <condition><![CDATA[${caseInstance.getVariable('exitPlanModelVariable') != null &&  exitPlanModelVariable}]]></condition>
        </ifPart>
      </sentry>
      <sentry id="sentry2">
        <planItemOnPart id="sentryOnPart2" sourceRef="planItem2">
          <standardEvent>complete</standardEvent>
        </planItemOnPart>
      </sentry>
      <task id="sid-F59B44F4-383E-4612-9469-5A2200B2E0BF" name="A"></task>
      <task id="sid-8FFD88A7-4977-440B-B6F3-498D0C0174B1" name="B"></task>
      <exitCriterion id="sid-4EEE1DC3-6FDE-45DF-9856-82ADB4E05FC9" sentryRef="sentry1"></exitCriterion>
      <exitCriterion id="sid-860E5BFE-AED4-4524-A208-F0EDF8653C60" sentryRef="sentry2"></exitCriterion>
    </casePlanModel>
    
    1、当sentry2满足条件的时候实例直接终止。
    2、当sentryOnPart1完成的时候，如果    exitPlanModelVariable为true则直接结束，如果为false
     则不会结束，再次设置     exitPlanModelVariable值为true。实例直接结束。     
     *
     *
     */



public class IfPartTest {
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
				
				//"com/shareniu/shareniu_flowable_study/cmmn/ifpart/IfPartTest.testIfPartOnly.cmmn")
//			"com/shareniu/shareniu_flowable_study/cmmn/ifpart/IfPartTest.testOnAndIfPart2.cmmn")
			//"com/shareniu/shareniu_flowable_study/cmmn/ifpart/IfPartTest.testIfPartConditionTriggerOnSetVariables.cmmn")
	//		"com/shareniu/shareniu_flowable_study/cmmn/ifpart/IfPartTest.testManualEvaluateCriteria.cmmn")
			//"com/shareniu/shareniu_flowable_study/cmmn/ifpart/IfPartTest.testMultipleOnParts.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/ifpart/IfPartTest.testEntryAndExitConditionBothSatisfied.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/ifpart/IfPartTest.testExitPlanModelWithIfPart.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/ifpart/IfPartTest.testNestedStagesWithIfPart.cmmn")
	//		"com/shareniu/shareniu_flowable_study/cmmn/ifpart/IfPartTest.testNestedStagesWithIfPart2.cmmn")
			//"com/shareniu/shareniu_flowable_study/cmmn/ifpart/IfPartTest.testNestedStagesWithIfPart3.cmmn")
			//"com/shareniu/shareniu_flowable_study/cmmn/ifpart/IfPartTest.testStageWithExitIfPart.cmmn")
			//"com/shareniu/shareniu_flowable_study/cmmn/ifpart/IfPartTest.testStageWithExitIfPart2.cmmn")
			"com/shareniu/shareniu_flowable_study/network/ifpart/IfPartTest.testMultipleOnParts.cmmn")
				.deploy().getId();
	}

    @Test
    public void testIfPartOnly() {
        // Case 1 : Passing variable from the start
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testIfPartOnly")
                .variable("variable", true)
                .start();
        assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        
        // Case 2 : Passing variable after case instance start
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testIfPartOnly")
                .start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().list();
        assertEquals(1, planItemInstances.size());
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("variable", true));
        assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        
        // Case 3 : Completing A after start should end the case instance
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testIfPartOnly")
                .start();
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().list();
        assertEquals(1, planItemInstances.size());
        assertEquals("A", planItemInstances.get(0).getName());
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        
        // Be should remain in the available state, until the variable is set
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateAvailable().list();
        assertEquals(1, planItemInstances.size());
        assertEquals("B", planItemInstances.get(0).getName());
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("variable", true));
        
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().list();
        assertEquals(1, planItemInstances.size());
        assertEquals("B", planItemInstances.get(0).getName());
        
        // Completing B ends the case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
    //    assertCaseInstanceEnded(caseInstance);
    }
    
    @Test
    public void testOnAndIfPart() {
        // Passing the variable for the if part condition at start
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
            .caseDefinitionKey("testSimpleCondition")
            .variable("conditionVariable", true)
            .start();
        
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().list();
        assertEquals(1, planItemInstances.size());
        assertEquals("A", planItemInstances.get(0).getName());
        
        // Completing plan item A should trigger B
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().list();
        assertEquals(1, planItemInstances.size());
        assertEquals("B", planItemInstances.get(0).getName());
        
        // Completing B should end the case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
       // assertCaseInstanceEnded(caseInstance);
    }
    
    
    @Test
    public void testOnAndIfPart1() {
        // Passing the variable for the if part condition at start
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
            .caseDefinitionKey("testSimpleCondition")
            .variable("conditionVariable", false)
            .start();
        
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().list();
        assertEquals(1, planItemInstances.size());
        assertEquals("A", planItemInstances.get(0).getName());
        
        // Completing plan item A should trigger B
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
    }
    @Test
    public void testOnAndIfPart2() {
    	//   cmmnRuntimeService.setVariables("ca41251c-46b2-11e8-b879-a652884ed93a", CollectionUtil.singletonMap("variable", true));
    	cmmnRuntimeService
   	.triggerPlanItemInstance("ca44325e-46b2-11e8-b879-a652884ed93a");
    }
    /**
     * 启动不设置变量 直接报错
     * 启动设置变量为false在直接实例结束
     */
    @Test
    public void testOnAndIfPart_1() {
    	   // Passing the variable for the if part condition at start
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
            .caseDefinitionKey("testSimpleCondition")
            .variable("conditionVariable", false)
            .start();
    }
    @Test
    public void testOnAndIfPart_2() {
    	//ff39c85b-3c5a-11e8-9bae-c6debfa7f78c
    	cmmnRuntimeService.triggerPlanItemInstance("79e52c22-3c81-11e8-901e-c6debfa7f78c");
    }
    @Test
    public void testOnAndIfPart_3() {
        cmmnRuntimeService.setVariables("04aaf34f-3c5c-11e8-990f-c6debfa7f78c", CollectionUtil.singletonMap("variable", true));
    }
    @Test
    public void testOnAndIfPart_4() {
    	//cmmnRuntimeService.enablePlanItemInstance("04aaf34f-3c5c-11e8-990f-c6debfa7f78c");
    	cmmnRuntimeService.completeCaseInstance("04a8d06d-3c5c-11e8-990f-c6debfa7f78c");
    }
    
    
    
    /**
     * TODO 
     */
    @Test
    public void testIfPartConditionTriggerOnSetVariables() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testSimpleCondition")
        	.variable("conditionVariable", true)
        		.start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().list();
        assertEquals(1, planItemInstances.size());
        assertEquals("A", planItemInstances.get(0).getName());
        
        // Completing plan item A should not trigger B
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().singleResult();
        assertEquals("B", planItemInstance.getName());
        assertEquals(PlanItemInstanceState.AVAILABLE, planItemInstance.getState());
    }
    
    
    
    
    
    
    
    
    
    /**
     * 1、如果someBean.isSatisfied()为false，则planItem2不能使用  状态是available
     * 可以调用  cmmnRuntimeService.evaluateCriteria(caseInstance.getId()); 进行激活 变为active
     * 
     * 2、如果someBean.isSatisfied()为true，则planItem2可以使用
     */
    @Test
    public void testManualEvaluateCriteria() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testManualEvaluateCriteria")
                .variable("someBean", new TestBean())
                .start();
        
        // Triggering the evaluation twice will satisfy the entry criterion for B
       // assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        TestBean.RETURN_VALUE = true;
        
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("variable", true));
       cmmnRuntimeService.evaluateCriteria(caseInstance.getId());
        //assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
    }
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 
     */
    
    @Test
    public void testMultipleOnParts() {
        cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testMultipleOnParts")
                .variable("conditionVariable", true)
                .start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        assertEquals(3, planItemInstances.size());
        assertEquals("A", planItemInstances.get(0).getName());
        assertEquals("B", planItemInstances.get(1).getName());
        assertEquals("C", planItemInstances.get(2).getName());
        
        for (PlanItemInstance planItemInstance : planItemInstances) {
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
        }
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().singleResult();
        assertEquals("D", planItemInstance.getName());
    }
    @Test
    public void testMultipleOnParts3() {
        cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testMultipleOnParts")
                .variable("conditionVariable", true)
                .variable("conditionVariable1", false)
                .start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        assertEquals(3, planItemInstances.size());
        assertEquals("A", planItemInstances.get(0).getName());
        assertEquals("B", planItemInstances.get(1).getName());
        assertEquals("C", planItemInstances.get(2).getName());
    }
    
    
    @Test
    public void testMultipleOnParts1() {
        cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testMultipleOnParts")
                .variable("conditionVariable", true)
                .start();
    }
    @Test
    public void testMultipleOnParts2() {
    	//ab1bf7d2-46bd-11e8-8aec-a652884ed93a
    	  cmmnRuntimeService.triggerPlanItemInstance("ab1bf7d4-46bd-11e8-8aec-a652884ed93a");
    }
    
    //TODO 需要在研究
    @Test
    public void testEntryAndExitConditionBothSatisfied() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
            .caseDefinitionKey("testEntryAndExitConditionBothSatisfied")
            .start();
        assertNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().planItemInstanceName("A").singleResult());
        assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().planItemInstanceName("B").singleResult());
        
        // Setting the variable will trigger the entry condition of A and the exit condition of B
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("conditionVariable", true));
        
        assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().planItemInstanceName("A").singleResult());
        assertNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().planItemInstanceName("B").singleResult());
    }
    /**
     * testExitPlanModelWithIfPart 已经测试
     * TODO 
     */
    @Test
    public void testExitPlanModelWithIfPart() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testExitPlanModelWithIfPart").start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        assertEquals(2, planItemInstances.size());
        assertEquals("A", planItemInstances.get(0).getName());
        assertEquals("B", planItemInstances.get(1).getName());
        
        // Completing B terminates the case through one of the exit criteria
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(1).getId());
       // assertCaseInstanceEnded(caseInstance);
        
        // Now B isn't completed, but A is. When the variable is set, the case is terminated
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testExitPlanModelWithIfPart").start();
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().count());
        
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("exitPlanModelVariable", true));
     //   assertCaseInstanceEnded(caseInstance);
    }
    /**
     * Triggering B should delete all stages
     * TODO
     * 
     */
    @Test
    public void testNestedStagesWithIfPart() {
        // Start case, activate inner nested stage
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testNestedStagesWithIfPart").start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        assertEquals(4, planItemInstances.size());
        assertEquals("A", planItemInstances.get(0).getName());
        assertEquals("C", planItemInstances.get(1).getName());
        assertEquals("Stage1", planItemInstances.get(2).getName());
        assertEquals("Stage2", planItemInstances.get(3).getName());
        assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateAvailable().planItemInstanceName("Stage3").singleResult());
        
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("nestedStageEntryVariable", true));
        assertEquals(6, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().count());
        PlanItemInstance planItemInstanceB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().planItemInstanceName("B").singleResult();
        assertNotNull(planItemInstanceB);
        
        // Triggering B should delete all stages
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstanceB.getId());
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        assertEquals(1, planItemInstances.size());
        assertEquals("A", planItemInstances.get(0).getName());

        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
      //  assertCaseInstanceEnded(caseInstance);
    }
    /**
     * // Setting the destroyStages variables, deletes all stages
     * TODO 
     */
    @Test
    public void testNestedStagesWithIfPart2() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testNestedStagesWithIfPart").start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        assertEquals(4, planItemInstances.size());

        // Setting the destroyStages variables, deletes all stages
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("destroyStages", true));
        
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        assertEquals(1, planItemInstances.size());
        assertEquals("A", planItemInstances.get(0).getName());

        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
      //  assertCaseInstanceEnded(caseInstance);
    }
    /**
     * /// Setting the destroyAll variable should terminate all
     * TODO
     */
    @Test
    public void testNestedStagesWithIfPart3() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testNestedStagesWithIfPart").start();
        assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().count());
        /// Setting the destroyAll variable should terminate all
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("destroyAll", true));
      //  assertCaseInstanceEnded(caseInstance);
    }
    /**
     * TODO
     */
    @Test
    public void testStageWithExitIfPart() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testStageWithExitIfPart")
                .variable("enableStage", true)
                .start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        assertEquals(4, planItemInstances.size());
        assertEquals("A", planItemInstances.get(0).getName());
        assertEquals("B", planItemInstances.get(1).getName());
        assertEquals("C", planItemInstances.get(2).getName());
        // Triggering A should terminate the stage and thus also the case
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
      //  assertCaseInstanceEnded(caseInstance);
    }
    /**
     * TODO
     */
    @Test
    public void testStageWithExitIfPart2() {
        // Not setting the enableStage variable now
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testStageWithExitIfPart")
                .start();
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceStateActive().orderByName().asc().list();
        assertEquals(1, planItemInstances.size());
        assertEquals("A", planItemInstances.get(0).getName());
        
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstances.get(0).getId());
     //   assertCaseInstanceEnded(caseInstance);
    }
    
    
    @Test
    public void testOnAndIfPart_d() {
    	   // Passing the variable for the if part condition at start
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
            .caseDefinitionKey("d")
            .variable("a", false)
            .start();
    }
    
    
    public static class TestBean implements Serializable {
        
        public static boolean RETURN_VALUE=false;
        
        public boolean isSatisfied() {
            return RETURN_VALUE;
        }
        
    }

}
