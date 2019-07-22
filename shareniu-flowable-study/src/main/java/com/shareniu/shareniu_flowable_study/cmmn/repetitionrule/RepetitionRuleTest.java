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
package com.shareniu.shareniu_flowable_study.cmmn.repetitionrule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Date;
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
import org.flowable.cmmn.engine.test.impl.CmmnJobTestHelper;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;

/**
 * 
 * 
 */
public class RepetitionRuleTest  {
	CmmnEngine ce;
	CmmnHistoryService cmmnHistoryService;
	CmmnManagementService cmmnManagementService;
	CmmnRepositoryService cmmnRepositoryService;
	CmmnRuntimeService cmmnRuntimeService;
	CmmnTaskService cmmnTaskService;
	protected String oneTaskCaseDeploymentId;
	CmmnEngineConfiguration dc ;

	@Before
	public void init() {
		InputStream in = CaseTaskTest.class.getClassLoader()
				.getResourceAsStream("com/shareniu/shareniu_flowable_study/cmmn/flowable.cmmn.cfg.xml");
		 dc = CmmnEngineConfiguration.createCmmnEngineConfigurationFromInputStream(in);
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
	.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/repetitionrule/RepetitionRuleTest.testRepeatingTimer.cmmn")
				.deploy()
				.getId();
	}
	/**
	 * <case id="repeatingTask" name="repeatingTask" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="My Task" definitionRef="sid-F1978236-FBB6-461B-9B3D-C7D70A803A4F">
        <itemControl>
          <repetitionRule>
            <condition><![CDATA[${repetitionCounter < 5}]]></condition>
          </repetitionRule>
        </itemControl>
      </planItem>
      <humanTask id="sid-F1978236-FBB6-461B-9B3D-C7D70A803A4F" name="My Task"></humanTask>
    </casePlanModel>
  </case>
  1、repetitionCounter内置变量，默认是1。循环一次就加1
	 */
    @Test
    public void testSimpleRepeatingTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("repeatingTask").start();
        for (int i=0; i<5; i++) {
            Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
            assertNotNull("No task found for index " + i, task);
            assertEquals("My Task", task.getName());
            cmmnTaskService.complete(task.getId());
        }
    }
    /**
     *  <case id="repeatingTask" name="repeatingTask" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="My Task" definitionRef="sid-F1978236-FBB6-461B-9B3D-C7D70A803A4F">
        <itemControl>
          <repetitionRule flowable:counterVariable="myCounter">
            <condition><![CDATA[${myCounter < 10}]]></condition>
          </repetitionRule>
        </itemControl>
      </planItem>
      <humanTask id="sid-F1978236-FBB6-461B-9B3D-C7D70A803A4F" name="My Task"></humanTask>
    </casePlanModel>
  </case>
  1、myCounter 内置变量，默认是1。循环一次就加1
     */
    @Test
    public void testCustomCounterVariable() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("repeatingTask").start();
        for (int i=0; i<10; i++) {
            Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
            assertNotNull("No task found for index " + i, task);
            assertEquals("My Task", task.getName());
            cmmnTaskService.complete(task.getId());
        }
    }
    /**
     *   <case id="testRepeatingStage" name="testRepeatingStage" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="Task outside stage" definitionRef="sid-FE742B49-4D62-46A7-8DBA-C5F65A8337BE"></planItem>
      <planItem id="planItem5" definitionRef="sid-42474674-1589-4213-9C73-6025E61DAB67">
        <itemControl>
          <repetitionRule flowable:counterVariable="repetitionCounter">
            <condition><![CDATA[${repetitionCounter < 3}]]></condition>
          </repetitionRule>
        </itemControl>
      </planItem>
      <humanTask id="sid-FE742B49-4D62-46A7-8DBA-C5F65A8337BE" name="Task outside stage"></humanTask>
      <stage id="sid-42474674-1589-4213-9C73-6025E61DAB67">
        <planItem id="planItem2" name="A" definitionRef="sid-C967BC5B-2B3B-42B5-9506-D6F310ABE4C1"></planItem>
        <planItem id="planItem3" name="B" definitionRef="sid-19E793D9-63E7-4FD8-AC3D-5376355E1145">
          <entryCriterion id="sid-DD98B821-A3B6-4D95-A735-47F46C165110" sentryRef="sentry1"></entryCriterion>
        </planItem>
        <planItem id="planItem4" name="C" definitionRef="sid-0CAA8DA5-523D-4203-A783-E363D0EC471E">
          <entryCriterion id="sid-66E2E7A2-1E50-42FF-B7EC-9DBCD9BBDC16" sentryRef="sentry2"></entryCriterion>
        </planItem>
        <sentry id="sentry1">
          <planItemOnPart id="sentryOnPart1" sourceRef="planItem2">
            <standardEvent>complete</standardEvent>
          </planItemOnPart>
        </sentry>
        <sentry id="sentry2">
          <planItemOnPart id="sentryOnPart2" sourceRef="planItem2">
            <standardEvent>complete</standardEvent>
          </planItemOnPart>
        </sentry>
        <humanTask id="sid-C967BC5B-2B3B-42B5-9506-D6F310ABE4C1" name="A"></humanTask>
        <humanTask id="sid-19E793D9-63E7-4FD8-AC3D-5376355E1145" name="B"></humanTask>
        <humanTask id="sid-0CAA8DA5-523D-4203-A783-E363D0EC471E" name="C"></humanTask>
      </stage>
    </casePlanModel>
  </case>
  stage循环了三次
     */
    @Test
    public void testRepeatingStage() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testRepeatingStage").start();
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals(2, tasks.size());
        assertEquals("A", tasks.get(0).getName());
        assertEquals("Task outside stage", tasks.get(1).getName());
        
        // Stage is repeated 3 times
        for (int i=0; i<3; i++) {
            cmmnTaskService.complete(tasks.get(0).getId()); // Completing A will make B and C active
            tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
            assertEquals(3, tasks.size());
            assertEquals("B", tasks.get(0).getName());
            assertEquals("C", tasks.get(1).getName());
            assertEquals("Task outside stage", tasks.get(2).getName());
            
            // Completing B and C should lead to a repetition of the stage
            cmmnTaskService.complete(tasks.get(0).getId()); // B
            cmmnTaskService.complete(tasks.get(1).getId()); // C
            
            tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        }
        
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("Task outside stage", task.getName());
        cmmnTaskService.complete(task.getId());
        
    }
    /**
     *  <case id="testNestedRepeatingStage" name="testNestedRepeatingStage" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="A" definitionRef="sid-0348928E-E0EA-48DF-8CB3-39D8BF770CAF">
        <itemControl>
          <repetitionRule flowable:counterVariable="repetitionCounter">
            <condition><![CDATA[${repetitionCounter < 3}]]></condition>
          </repetitionRule>
        </itemControl>
      </planItem>
      <planItem id="planItem6" name="Stage1" definitionRef="sid-B80F95D8-24D7-497F-8102-4276753CAB8C">
        <entryCriterion id="sid-5BED84F0-2EC0-48C3-8B29-45A85E441B58" sentryRef="sentry2"></entryCriterion>
      </planItem>
      <sentry id="sentry2">
        <planItemOnPart id="sentryOnPart2" sourceRef="planItem1">
          <standardEvent>complete</standardEvent>
        </planItemOnPart>
      </sentry>
      <humanTask id="sid-0348928E-E0EA-48DF-8CB3-39D8BF770CAF" name="A"></humanTask>
      <stage id="sid-B80F95D8-24D7-497F-8102-4276753CAB8C" name="Stage1">
        <planItem id="planItem5" name="Stage 2" definitionRef="sid-F8FDC505-81C6-4888-943C-16C6AFA1676C"></planItem>
        <stage id="sid-F8FDC505-81C6-4888-943C-16C6AFA1676C" name="Stage 2">
          <planItem id="planItem4" name="Stage 3" definitionRef="sid-817F2286-EAA9-4C80-9EBE-FAEC3A387CD9">
            <itemControl>
              <repetitionRule flowable:counterVariable="repetitionCounter">
                <condition><![CDATA[${repetitionCounter < 2}]]></condition>
              </repetitionRule>
            </itemControl>
          </planItem>
          <stage id="sid-817F2286-EAA9-4C80-9EBE-FAEC3A387CD9" name="Stage 3">
            <planItem id="planItem2" name="B" definitionRef="sid-3455CFA9-8A66-440C-9DFA-F7E625956EA7"></planItem>
            <planItem id="planItem3" name="C" definitionRef="sid-1C13F77A-C77B-4E0A-8D01-6B7E8AECFA69">
              <entryCriterion id="sid-B1CCA98E-8AA4-4F2B-A604-F35896AB0F87" sentryRef="sentry1"></entryCriterion>
            </planItem>
            <sentry id="sentry1">
              <planItemOnPart id="sentryOnPart1" sourceRef="planItem2">
                <standardEvent>complete</standardEvent>
              </planItemOnPart>
            </sentry>
            <humanTask id="sid-3455CFA9-8A66-440C-9DFA-F7E625956EA7" name="B"></humanTask>
            <humanTask id="sid-1C13F77A-C77B-4E0A-8D01-6B7E8AECFA69" name="C"></humanTask>
          </stage>
        </stage>
      </stage>
    </casePlanModel>
  </case>
  1、启动之后是A
  2、完成A之后，
  创建一个新的A 激活B
   // Completing A should:
        // - create a new instance of A (A is repeating)
        // - activate B
         * 
   3、成A对重复嵌套的stage3没有影响。
   4、A重复执执行三次。
   5、// Completing B should activate C
   6、// Completing C should repeat the nested stage and activate B again
   7、 // Completing C should end the case instance
     */
    @Test
    public void testNestedRepeatingStage() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testNestedRepeatingStage").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("A", task.getName());
        
        // Completing A should:
        // - create a new instance of A (A is repeating)
        // - activate B
        
        cmmnTaskService.complete(task.getId());
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals(2, tasks.size());
        assertEquals("A", tasks.get(0).getName());
        assertEquals("B", tasks.get(1).getName());
        
        // Complete A should have no impact on the repeating of the nested stage3
        cmmnTaskService.complete(tasks.get(0).getId());
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals(2, tasks.size());
        assertEquals("A", tasks.get(0).getName());
        assertEquals("B", tasks.get(1).getName());
        
        // A is repeated 3 times
        cmmnTaskService.complete(tasks.get(0).getId());
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("B", task.getName());
        
        // Completing B should activate C
        cmmnTaskService.complete(task.getId());
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("C", task.getName());
        
        // Completing C should repeat the nested stage and activate B again
        cmmnTaskService.complete(task.getId());
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("B", task.getName());
        cmmnTaskService.complete(task.getId());
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("C", task.getName());
        
        // Completing C should end the case instance
        cmmnTaskService.complete(task.getId());
    }
    /**
     * <case id="testRepeatingTimer" name="testRepeatingTimer" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem1" definitionRef="sid-5427322D-539C-4DC0-AD47-6DA9421ED309"></planItem>
      <planItem id="planItem2" name="Task after timer" definitionRef="sid-2255BF13-E956-49AA-9054-F3EAD06DA08F">
        <itemControl>
          <repetitionRule flowable:counterVariable="repetitionCounter">
            <condition><![CDATA[${true}]]></condition>
          </repetitionRule>
        </itemControl>
        <entryCriterion id="sid-A5BD6737-2F53-4934-A78B-B4600DE95DBB" sentryRef="sentry1"></entryCriterion>
      </planItem>
      <sentry id="sentry1">
        <planItemOnPart id="sentryOnPart1" sourceRef="planItem1">
          <standardEvent>occur</standardEvent>
        </planItemOnPart>
      </sentry>
      <timerEventListener id="sid-5427322D-539C-4DC0-AD47-6DA9421ED309">
        <timerExpression><![CDATA[R/PT1H]]></timerExpression>
      </timerEventListener>
      <humanTask id="sid-2255BF13-E956-49AA-9054-F3EAD06DA08F" name="Task after timer"></humanTask>
    </casePlanModel>
  </case>
  TODO 需要观察
  
     */
    @Test
    public void testRepeatingTimer() {
        Date currentTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testRepeatingTimer").start();
        
        // Should have the task plan item state available
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK).singleResult();
        assertEquals(PlanItemInstanceState.AVAILABLE, planItemInstance.getState());
        
        // Task should not be created yet
        assertEquals(0L, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        // And one for the timer event listener
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.TIMER_EVENT_LISTENER).singleResult();
        assertEquals(PlanItemInstanceState.AVAILABLE, planItemInstance.getState());
        
        // Should have a timer job available
        Job job = cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertNotNull(job);
        
        // Moving the timer 1 hour ahead, should create a task instance. 
        currentTime = new Date(currentTime.getTime() + (60 * 60 * 1000) + 10);
        setClockTo(currentTime);
        job = cmmnManagementService.moveTimerToExecutableJob(job.getId());
        cmmnManagementService.executeJob(job.getId());
        assertEquals(1L, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        // A plan item in state 'waiting for repetition' should exist for the yask
        planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK)
                .planItemInstanceStateWaitingForRepetition()
                .singleResult();
        assertEquals(PlanItemInstanceState.WAITING_FOR_REPETITION, planItemInstance.getState());
        
        // This can be repeated forever
        for (int i=0; i<10; i++) {
            currentTime = new Date(currentTime.getTime() + (60 * 60 * 1000) + 10);
            setClockTo(currentTime);
            job = cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).singleResult();
            job = cmmnManagementService.moveTimerToExecutableJob(job.getId());
            cmmnManagementService.executeJob(job.getId());
            assertEquals(i + 2, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        }
        
        // Completing all the tasks should still keep the case instance running
        for (Task task : cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list()) {
            cmmnTaskService.complete(task.getId());
        }
        assertEquals(0L, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
       // assertEquals(1L, cmmnRuntimeService.createCaseInstanceQuery().count());
        // There should also still be a plan item instance in the 'wait for repetition' state
        assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK)
                .planItemInstanceStateWaitingForRepetition()
                .singleResult());
        
        // Terminating the case instance should remove the timer
        cmmnRuntimeService.terminateCaseInstance(caseInstance.getId());
        assertEquals(0L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        assertEquals(0L, cmmnRuntimeService.createCaseInstanceQuery().count());
        assertEquals(0L, cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).count());
    }
    
    @Test
    public void testRepeatingTimerWithCronExpression() {
        Date currentTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testRepeatingTimer").start();
        
        // Moving the timer 6 minutes should trigger the timer
        for (int i=0; i<3; i++) {
            currentTime = new Date(currentTime.getTime() + (6 * 60 * 1000));
            setClockTo(currentTime);
        
            Job job = cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).singleResult();
            assertTrue(job.getDuedate().getTime() - currentTime.getTime() <= (5 * 60 * 1000));
            job = cmmnManagementService.moveTimerToExecutableJob(job.getId());
            cmmnManagementService.executeJob(job.getId());
            
            assertEquals(i + 1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        }
    }
    
    @Test
    @CmmnDeployment
    public void testLimitedRepeatingTimer() {
        Date currentTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testLimitedRepeatingTimer").start();
        
        currentTime = new Date(currentTime.getTime() + (5 * 60 * 60 * 1000) + 10000);
        setClockTo(currentTime);
    
        Job job = cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertTrue(job.getDuedate().getTime() - currentTime.getTime() <= (5 * 60 * 1000));
        job = cmmnManagementService.moveTimerToExecutableJob(job.getId());
        cmmnManagementService.executeJob(job.getId());
        assertEquals(1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        // new timer should be scheduled
        assertEquals(1L, cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).count());
        
        // Should only repeat two times
        currentTime = new Date(currentTime.getTime() + (5 * 60 * 60 * 1000) + 10000);
        setClockTo(currentTime);
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 10000L, 100L, true);
        
        assertEquals(0L, cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).count());
        assertEquals(0L, cmmnManagementService.createJobQuery().caseInstanceId(caseInstance.getId()).count());

        assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK)
                .planItemInstanceStateWaitingForRepetition()
                .singleResult());
        
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list();
        assertEquals(2, tasks.size());
        for (Task task : tasks) {
            cmmnTaskService.complete(task.getId());
        }
        
        assertCaseInstanceEnded(caseInstance);
    }
    
    @Test
    @CmmnDeployment
    public void testLimitedRepeatingTimerIgnoredAfterFirst() {
        
        // No repetition rule for task A, hence only the first one will be listened too.
        
        Date currentTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testLimitedRepeatingTimer").start();
        
        currentTime = new Date(currentTime.getTime() + (5 * 60 * 60 * 1000) + 10000);
        setClockTo(currentTime);
    
        Job job = cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertTrue(job.getDuedate().getTime() - currentTime.getTime() <= (5 * 60 * 1000));
        job = cmmnManagementService.moveTimerToExecutableJob(job.getId());
        cmmnManagementService.executeJob(job.getId());
        assertEquals(1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        // new timer should be scheduled
        assertEquals(1L, cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).count());
        
        // Should only repeat two times
        currentTime = new Date(currentTime.getTime() + (5 * 60 * 60 * 1000) + 10000);
        setClockTo(currentTime);
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 10000L, 100L, true);
        
        assertEquals(0L, cmmnManagementService.createTimerJobQuery().caseInstanceId(caseInstance.getId()).count());
        assertEquals(0L, cmmnManagementService.createJobQuery().caseInstanceId(caseInstance.getId()).count());
        
        // Ignoring second occur event
        assertEquals(1L, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
    }

    @Test
    @CmmnDeployment
    public void testRepetitionRuleWithExitCriteria() {
        //Completion of taskB will transition taskA to "exit", skipping the evaluation of the repetition rule (Table 8.8 of CMM 1.1 Spec)
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testRepetitionRuleWithExitCriteria")
                .variable("whileTrue", "true")
                .start();

        assertNotNull(caseInstance);

        for (int i = 0; i < 3; i++) {
            Task taskA = cmmnTaskService.createTaskQuery().active().taskDefinitionKey("taskA").singleResult();
            cmmnTaskService.complete(taskA.getId());
            assertCaseInstanceNotEnded(caseInstance);
        }

        Task taskB = cmmnTaskService.createTaskQuery().active().taskDefinitionKey("taskB").singleResult();
        cmmnTaskService.complete(taskB.getId());
        assertCaseInstanceEnded(caseInstance);
    }
    
    protected Date setClockFixedToCurrentTime() {
        Date date = new Date();
        dc.getClock().setCurrentTime(date);
        return date;
    }
    
    protected void setClockTo(Date date) {
        dc.getClock().setCurrentTime(date);
    }
}
