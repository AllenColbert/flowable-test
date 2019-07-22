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
package com.shareniu.shareniu_flowable_study.network.usereventlistener;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnManagementService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.runtime.*;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * @author Dennis Federico
 */
public class UserEventListenerTest  {
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
			//	"com/shareniu/shareniu_flowable_study/cmmn/variables/VariablesTest.testGetVariables.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/variables/VariablesTest.testResolveMilestoneNameAsExpression.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/variables/VariablesTest.testTransientVariables.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/usereventlistener/UserEventListenerTest.testSimpleEnableTask.cmmn")
			//"com/shareniu/shareniu_flowable_study/cmmn/usereventlistener/UserEventListenerTest.testTerminateTask.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/usereventlistener/UserEventListenerTest.testTerminateStage.cmmn")
			//"com/shareniu/shareniu_flowable_study/cmmn/usereventlistener/UserEventListenerTest.testRepetitionWithUserEventExitCriteria.cmmn")
			//"com/shareniu/shareniu_flowable_study/cmmn/usereventlistener/UserEventListenerTest.testRepetitionWithUserEventEntryCriteria.cmmn")
			//"com/shareniu/shareniu_flowable_study/cmmn/usereventlistener/UserEventListenerTest.testStageWithoutFiringTheEvent.cmmn")
			//"com/shareniu/shareniu_flowable_study/cmmn/usereventlistener/UserEventListenerTest.testStageWithoutFiringTheEvent.cmmn")
			"com/shareniu/shareniu_flowable_study/network/usereventlistener/UserEventListenerTest.testCaseWithoutFiringTheEvent.cmmn")
				.deploy().getId();
	}
   // @Rule
  //  public TestName name = new TestName();
	/**
	 *  <case id="testSimpleEnableTask" name="testUserEventListener" flowable:initiatorVariableName="initiator">
        <casePlanModel id="casePlanModel">
            <planItem id="planItemA" name="A" definitionRef="taskA"/>
            <planItem id="userEventListenerPlanItem" definitionRef="userEventListener"/>
            <planItem id="planItemB" name="B" definitionRef="taskB">
                <entryCriterion id="entryTaskB" sentryRef="sentryOnUserEventListener"/>
            </planItem>
            <sentry id="sentryOnUserEventListener">
                <planItemOnPart id="sentryOnUserEvent" sourceRef="userEventListenerPlanItem">
                    <standardEvent>occur</standardEvent>
                </planItemOnPart>
            </sentry>
            <humanTask id="taskA" name="A"/>
            <userEventListener id="userEventListener" name="myUserEventListener">
                <documentation>UserEventListener documentation</documentation>
            </userEventListener>
            <humanTask id="taskB" name="B"/>
        </casePlanModel>
    </case>
    
    entryCriterion
    1、实例表存在三条数据 planItemA active
    				userEventListenerPlanItem   available
    			    planItemB   available
	2、UserEventListenerInstance cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult()  查询userEventListener实例
	3、触发listener cmmnRuntimeService.completeUserEventListenerInstance(listenerInstance.getId());
	则  planItemB 变为available  userEventListenerPlanItem 完成。
	4、
	 * 
	 * 
	 */
    @Test
    public void testSimpleEnableTask() {
        //Simple use of the UserEventListener as EntryCriteria of a Task
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testSimpleEnableTask").start();
        assertNotNull(caseInstance);
     //   assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());

        //3 PlanItems reachable
        assertEquals(3, cmmnRuntimeService.createPlanItemInstanceQuery().count());

        //1 User Event Listener
        PlanItemInstance listenerInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).singleResult();
        assertNotNull(listenerInstance);
        assertEquals("userEventListener", listenerInstance.getPlanItemDefinitionId());
        assertEquals(PlanItemInstanceState.AVAILABLE, listenerInstance.getState());
        
        // Verify same result is returned from query
        UserEventListenerInstance userEventListenerInstance = cmmnRuntimeService.createUserEventListenerInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertNotNull(userEventListenerInstance);
        assertEquals(userEventListenerInstance.getId(), listenerInstance.getId());
        assertEquals(userEventListenerInstance.getCaseDefinitionId(), listenerInstance.getCaseDefinitionId());
        assertEquals(userEventListenerInstance.getCaseInstanceId(), listenerInstance.getCaseInstanceId());
        assertEquals(userEventListenerInstance.getElementId(), listenerInstance.getElementId());
        assertEquals(userEventListenerInstance.getName(), listenerInstance.getName());
        assertEquals(userEventListenerInstance.getPlanItemDefinitionId(), listenerInstance.getPlanItemDefinitionId());
        assertEquals(userEventListenerInstance.getStageIntanceId(), listenerInstance.getStageInstanceId());
        assertEquals(userEventListenerInstance.getState(), listenerInstance.getState());
        
        assertEquals(1, cmmnRuntimeService.createUserEventListenerInstanceQuery().count());
        assertEquals(1, cmmnRuntimeService.createUserEventListenerInstanceQuery().list().size());
        
        assertNotNull(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseDefinitionId(listenerInstance.getCaseDefinitionId()).singleResult());
        assertNotNull(cmmnRuntimeService.createUserEventListenerInstanceQuery().caseDefinitionId(listenerInstance.getCaseDefinitionId()).singleResult());

        //2 HumanTasks ... one active and other waiting (available)
        assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK).count());
        PlanItemInstance active = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK).planItemInstanceStateActive().singleResult();
        assertNotNull(active);
        assertEquals("taskA", active.getPlanItemDefinitionId());
        PlanItemInstance available = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK).planItemInstanceStateAvailable().singleResult();
        assertNotNull(available);
        assertEquals("taskB", available.getPlanItemDefinitionId());

        //Trigger the listener
        cmmnRuntimeService.completeUserEventListenerInstance(listenerInstance.getId());

        //UserEventListener should be completed
        assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).count());

        //Only 2 PlanItems left
        assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().list().size());

        //Both Human task should be "active" now, as the sentry kicks on the UserEventListener transition
        assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK).planItemInstanceStateActive().count());

        //Finish the caseInstance
       // assertCaseInstanceNotEnded(caseInstance);
        cmmnTaskService.createTaskQuery().list().forEach(t -> cmmnTaskService.complete(t.getId()));
      //  assertCaseInstanceEnded(caseInstance);
    }

    
    
    
    /**
     *  <case id="testTerminateTask" name="testTerminateTaskUserEventListener">
        <casePlanModel id="casePlanModel">

            <planItem id="planItemStage" name="Stage One" definitionRef="stage1"/>

            <stage id="stage1" name="Stage One">
                <planItem id="planItemTaskA" name="Task A" definitionRef="taskA">
                    <exitCriterion id="onUserEventExit" sentryRef="onUserEventExitSentry"/>
                </planItem>
                <planItem id="planItemTaskB" name="Task B" definitionRef="taskB"/>
                <planItem id="userEventListenerPlanItem" definitionRef="userEventListener"/>

                <sentry id="onUserEventExitSentry">
                    <planItemOnPart sourceRef="userEventListenerPlanItem">
                        <standardEvent>occur</standardEvent>
                    </planItemOnPart>
                </sentry>

                <task id="taskA" name="Task A"/>
                <task id="taskB" name="Task B"/>

                <userEventListener id="userEventListener" name="myUserEventListener">
                    <documentation>UserEventListener documentation</documentation>
                </userEventListener>
            </stage>

        </casePlanModel>
    </case>
    exitCriterion
    1、userEventListenerPlanItem 是available 当userEventListenerPlanItem完成的时候，planItemTaskA也完成了。
    
     */
    @Test
    public void testTerminateTask() {
        //Test case where the UserEventListener is used to complete (ExitCriteria) of a task
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTerminateTask").start();
        assertNotNull(caseInstance);
      //  assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());

        //4 PlanItems reachable
        assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().list().size());

        //1 Stage
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.STAGE).count());

        //1 User Event Listener
        PlanItemInstance listenerInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).singleResult();
        assertNotNull(listenerInstance);
        assertEquals("userEventListener", listenerInstance.getPlanItemDefinitionId());
        assertEquals("userEventListenerPlanItem", listenerInstance.getElementId());
        assertEquals(PlanItemInstanceState.AVAILABLE, listenerInstance.getState());

        //2 Tasks on Active state
        List<PlanItemInstance> tasks = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType("task").list();
        assertEquals(2, tasks.size());
        tasks.forEach(t -> assertEquals(PlanItemInstanceState.ACTIVE, t.getState()));
        //Trigger the listener
        cmmnRuntimeService.triggerPlanItemInstance(listenerInstance.getId());
        //UserEventListener should be completed
        assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).count());

        //Only 2 PlanItems left (1 stage & 1 active task)
        assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().list().size());
        PlanItemInstance activeTask = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType("task").planItemInstanceStateActive().singleResult();
        assertNotNull(activeTask);
        assertEquals("taskB", activeTask.getPlanItemDefinitionId());

        //Finish the caseInstance
       // assertCaseInstanceNotEnded(caseInstance);
        cmmnRuntimeService.triggerPlanItemInstance(activeTask.getId());
        //assertCaseInstanceEnded(caseInstance);
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * 
     *  <case id="testTerminateStage" name="testTerminateStageUserEventListener">
        <casePlanModel id="casePlanModel">

            <planItem id="planItemStage" name="Stage One" definitionRef="stage1">
                <exitCriterion id="onUserEventExit" sentryRef="onUserEventExitSentry"/>
            </planItem>
            <planItem id="userEventListenerPlanItem" definitionRef="userEventListener"/>

            <sentry id="onUserEventExitSentry">
                <planItemOnPart sourceRef="userEventListenerPlanItem">
                    <standardEvent>occur</standardEvent>
                </planItemOnPart>
            </sentry>

            <userEventListener id="userEventListener" name="myUserEventListener">
                <documentation>UserEventListener documentation</documentation>
            </userEventListener>

            <stage id="stage1" name="Stage One">
                <planItem id="planItemTaskA" name="Task A" definitionRef="taskA"/>
                <task id="taskA" name="Task A"/>
            </stage>

        </casePlanModel>
    </case>
    
    exitCriterion：
    1、启动之后userEventListenerPlanItem  状态是available。
    2、直接完成userEventListenerPlanItem 则所有实例结束
     */
    @Test
    public void testTerminateStage() {
        //Test case where the UserEventListener is used to complete (ExitCriteria) of a Stage
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTerminateStage").start();
        assertNotNull(caseInstance);
      //  assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());

        //3 PlanItems reachable
        assertEquals(3, cmmnRuntimeService.createPlanItemInstanceQuery().list().size());

        //1 Stage
        PlanItemInstance stage = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.STAGE).singleResult();
        assertNotNull(stage);

        //1 User Event Listener
        PlanItemInstance listenerInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).singleResult();
        assertNotNull(listenerInstance);
        assertEquals("userEventListener", listenerInstance.getPlanItemDefinitionId());
        assertEquals(PlanItemInstanceState.AVAILABLE, listenerInstance.getState());

        //1 Tasks on Active state
        PlanItemInstance task = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType("task").singleResult();
        assertNotNull(task);
        assertEquals(PlanItemInstanceState.ACTIVE, task.getState());
        assertEquals(stage.getId(), task.getStageInstanceId());

        //Trigger the listener
       // assertCaseInstanceNotEnded(caseInstance);
        cmmnRuntimeService.triggerPlanItemInstance(listenerInstance.getId());
        //assertCaseInstanceEnded(caseInstance);
    }
    
    
    
    
    /**
     * <case id="testRepetitionWithUserEventExitCriteria" name="testRepetitionRule">
        <casePlanModel id="casePlanModel">

            <planItem id="planItemStage" name="Stage One" definitionRef="stage1"/>

            <stage id="stage1" name="Stage One">
                <planItem id="planItemTaskA" name="Task A" definitionRef="taskA">
                    <itemControl>
                        <repetitionRule>
                            <condition><![CDATA[${whileTrue}]]></condition>
                        </repetitionRule>
                    </itemControl>
                    <exitCriterion sentryRef="onUserEventAbortTaskSentry"/>
                </planItem>
                <planItem id="planItemUserEventAbortTask" definitionRef="abortTaskUserEvent"/>
                <sentry id="onUserEventAbortTaskSentry">
                    <planItemOnPart sourceRef="planItemUserEventAbortTask">
                        <standardEvent>occur</standardEvent>
                    </planItemOnPart>
                </sentry>

                <humanTask id="taskA" name="Task A"/>

                <userEventListener id="abortTaskUserEvent" name="Abort Task"/>
            </stage>

        </casePlanModel>
    </case>
    exitCriterion
    1、 1、启动之后userEventListenerPlanItem  状态是available。
    2、直接完成userEventListenerPlanItem 则所有实例结束
     */
    @Test
    public void testRepetitionWithUserEventExitCriteria() {
        //Test case where a repeating task is completed by its ExitCriteria fired by a UserEvent
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testRepetitionWithUserEventExitCriteria")
                .variable("whileTrue", "true")
                .start();

        assertNotNull(caseInstance);

        for (int i = 0; i < 3; i++) {
            Task taskA = cmmnTaskService.createTaskQuery().active().taskDefinitionKey("taskA").singleResult();
            cmmnTaskService.complete(taskA.getId());
           /// assertCaseInstanceNotEnded(caseInstance);
        }

        PlanItemInstance userEvent = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(userEvent.getId());
        //assertCaseInstanceEnded(caseInstance);
    }
    
    
    
    
    
    
    
    
    
    /**
     * <case id="testRepetitionWithUserEventEntryCriteria" name="testRepetitionRule">
        <casePlanModel id="casePlanModel">

            <planItem id="planItemStage" name="Stage One" definitionRef="stage1"/>

            <stage id="stage1" name="Stage One">
                <planItem id="planItemTaskA" name="Task A" definitionRef="taskA">
                    <itemControl>
                        <repetitionRule>
                            <condition><![CDATA[${whileRepeatTaskA}]]></condition>
                        </repetitionRule>
                    </itemControl>
                    <entryCriterion sentryRef="sentryActivateTask"/>
                </planItem>
                <planItem id="planItemTaskB" name="Task B" definitionRef="taskB"/>
                <planItem id="userAction" definitionRef="activateTaskA"/>

                <sentry id="sentryActivateTask">
                    <planItemOnPart sourceRef="userAction">
                        <standardEvent>occur</standardEvent>
                    </planItemOnPart>
                </sentry>

                <humanTask id="taskA" name="Task A"/>
                <humanTask id="taskB" name="Task B"/>
                <userEventListener id="activateTaskA" name="Activate Task"/>
            </stage>

        </casePlanModel>
    </case>
    entryCriterion   planItemTaskA和userAction是available
    1、entryCriterionplanItemTaskA存在两个数据：一个是wait_repetition 一个是active
    
    TODO 待研究
     */
    @Test
    public void testRepetitionWithUserEventEntryCriteria() {
        //Test case that activates a repeating task (entryCriteria) with a UserEvent
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testRepetitionWithUserEventEntryCriteria")
                .variable("whileRepeatTaskA", "true")
                 .start();

        assertNotNull(caseInstance);

        //TaskA on available state until the entry criteria occurs
        PlanItemInstance taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK).planItemDefinitionId("taskA").singleResult();
        assertEquals(PlanItemInstanceState.AVAILABLE, taskA.getState());

        //Trigger the userEvent
        PlanItemInstance userEvent = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(userEvent.getId());

        //UserEventListener is consumed
        assertEquals(0, cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).count());

        //Task is om Active state and a second task instance waiting for repetition
        Map<String, List<PlanItemInstance>> tasks = cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK)
                .planItemDefinitionId("taskA")
                .list().stream()
                .collect(Collectors.groupingBy(PlanItemInstance::getState));
        assertEquals(2, tasks.size());
        assertTrue(tasks.containsKey(PlanItemInstanceState.ACTIVE));
        assertEquals(1, tasks.get(PlanItemInstanceState.ACTIVE).size());
        assertTrue(tasks.containsKey(PlanItemInstanceState.WAITING_FOR_REPETITION));
        assertEquals(1, tasks.get(PlanItemInstanceState.WAITING_FOR_REPETITION).size());

        //Complete active task
        cmmnTaskService.complete(cmmnTaskService.createTaskQuery().taskDefinitionKey("taskA").active().singleResult().getId());

        //Only TaskB remains on Active state and one instance of TaskA waiting for repetition
        tasks = cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK)
                .list().stream().collect(Collectors.groupingBy(PlanItemInstance::getPlanItemDefinitionId));
        assertEquals(2, tasks.size());
        assertEquals(PlanItemInstanceState.ACTIVE, tasks.get("taskB").get(0).getState());
        assertEquals(PlanItemInstanceState.WAITING_FOR_REPETITION, tasks.get("taskA").get(0).getState());

        //Since WAITING_FOR_REPETITION is a "Semi-terminal", completing taskB should complete the stage and case
        cmmnTaskService.complete(cmmnTaskService.createTaskQuery().active().singleResult().getId());
       // assertCaseInstanceEnded(caseInstance);
    }
    /**
     * 
     *   <case id="testStageWithoutFiringTheEvent" name="testStageWithoutFiringTheEvent">
        <casePlanModel id="casePlanModel">

            <planItem id="planItemStage" name="Stage One" definitionRef="stage1"/>

            <stage id="stage1" name="Stage One">
                <planItem id="planItemTaskA" name="Task A" definitionRef="taskA">
                    <exitCriterion sentryRef="onUserEventAbortTaskSentry"/>
                </planItem>
                <planItem id="planItemUserEventAbortTask" definitionRef="abortTaskUserEvent"/>
                <sentry id="onUserEventAbortTaskSentry">
                    <planItemOnPart sourceRef="planItemUserEventAbortTask">
                        <standardEvent>occur</standardEvent>
                    </planItemOnPart>
                </sentry>
                <task id="taskA" name="Task A"/>
                <userEventListener id="abortTaskUserEvent" name="Abort Task"/>
            </stage>
        </casePlanModel>
    </case>
    
    1、planItemUserEventAbortTask  状态是 available
    2、完成Task A  表数据没有变化
    3、Trigger the listener should end the case
    
     */
    @Test
    public void testStageWithoutFiringTheEvent() {
        //Test case where the only "standing" plainItem for a stage is a UserEventListener
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testStageWithoutFiringTheEvent")
                .start();
        assertNotNull(caseInstance);
//        assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());

        Map<String, List<PlanItemInstance>> planItems = cmmnRuntimeService.createPlanItemInstanceQuery()
                .list().stream().collect(Collectors.groupingBy(PlanItemInstance::getPlanItemDefinitionType));

        //3 types of planItems
       // assertEquals(3, planItems.size());

        //1 User Event Listener
        assertEquals(1, planItems.getOrDefault(PlanItemDefinitionType.USER_EVENT_LISTENER, Collections.emptyList()).size());
        //1 Stage
        assertEquals(1, planItems.getOrDefault(PlanItemDefinitionType.STAGE, Collections.emptyList()).size());
        PlanItemInstance stage = planItems.get(PlanItemDefinitionType.STAGE).get(0);

        //1 Active Task (inside the stage)
        assertEquals(1, planItems.getOrDefault("task", Collections.emptyList()).size());
        PlanItemInstance task = planItems.get("task").get(0);
        assertEquals(PlanItemInstanceState.ACTIVE, task.getState());
        assertEquals(stage.getId(), task.getStageInstanceId());

        //Complete the Task
        cmmnRuntimeService.triggerPlanItemInstance(task.getId());

        //Listener should still be available and Stage active
        planItems = cmmnRuntimeService.createPlanItemInstanceQuery()
                .list().stream().collect(Collectors.groupingBy(PlanItemInstance::getPlanItemDefinitionType));
        assertEquals(2, planItems.size());
        assertEquals(1, planItems.getOrDefault(PlanItemDefinitionType.USER_EVENT_LISTENER, Collections.emptyList()).size());
        PlanItemInstance listener = planItems.get(PlanItemDefinitionType.USER_EVENT_LISTENER).get(0);
        assertEquals(PlanItemInstanceState.AVAILABLE, listener.getState());
        assertEquals(1, planItems.getOrDefault(PlanItemDefinitionType.STAGE, Collections.emptyList()).size());
        stage = planItems.get(PlanItemDefinitionType.STAGE).get(0);
        assertEquals(PlanItemInstanceState.ACTIVE, stage.getState());

        //Trigger the listener should end the case
        cmmnRuntimeService.triggerPlanItemInstance(listener.getId());

        //Stage and case instance should have ended...
        //assertCaseInstanceEnded(caseInstance);
    }
/**
 *  <case id="testCaseWithoutFiringTheEvent" name="testCompleteWithoutFireEvent">
        <casePlanModel id="casePlanModel">

            <planItem id="planItemStage" name="Stage One" definitionRef="stage1">
                <exitCriterion sentryRef="onUserEventAbortStageSentry"/>
            </planItem>
            <planItem id="planItemUserEventAbortStage" definitionRef="abortStageUserEvent"/>

            <sentry id="onUserEventAbortStageSentry">
                <planItemOnPart sourceRef="planItemUserEventAbortStage">
                    <standardEvent>occur</standardEvent>
                </planItemOnPart>
            </sentry>

            <userEventListener id="abortStageUserEvent" name="Abort Stage"/>

            <stage id="stage1" name="Stage One" autoComplete="true">
                <planItem id="planItemTaskA" name="Task A" definitionRef="taskA"/>
                <task id="taskA" name="Task A"/>
            </stage>

        </casePlanModel>
    </case>
    1、abortStageUserEvent状态是available
    2、完成TaskA  stage1也没有了。数据库只剩下了abortStageUserEvent
    3、Trigger the listener should end the case
 */
    @Test
    public void testCaseWithoutFiringTheEvent() {
        //Test case where the only "standing" plainItem for a Case is a UserEventListener
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testCaseWithoutFiringTheEvent")
                .start();
        assertNotNull(caseInstance);
     //   assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());

        Map<String, List<PlanItemInstance>> planItems = cmmnRuntimeService.createPlanItemInstanceQuery()
                .list().stream().collect(Collectors.groupingBy(PlanItemInstance::getPlanItemDefinitionType));

        //3 types of planItems
        assertEquals(3, planItems.size());

        //1 Stage
        assertEquals(1, planItems.getOrDefault(PlanItemDefinitionType.STAGE, Collections.emptyList()).size());
        PlanItemInstance stage = planItems.get(PlanItemDefinitionType.STAGE).get(0);

        //1 Active Task (inside the stage)
        assertEquals(1, planItems.getOrDefault("task", Collections.emptyList()).size());
        PlanItemInstance task = planItems.get("task").get(0);
        assertEquals(PlanItemInstanceState.ACTIVE, task.getState());
        assertEquals(stage.getId(), task.getStageInstanceId());

        //1 Available User Event Listener (outside the stage)
        assertEquals(1, planItems.getOrDefault(PlanItemDefinitionType.USER_EVENT_LISTENER, Collections.emptyList()).size());
        PlanItemInstance listener = planItems.get(PlanItemDefinitionType.USER_EVENT_LISTENER).get(0);
        assertNull(listener.getStageInstanceId());

        //Complete the Task
        cmmnRuntimeService.triggerPlanItemInstance(task.getId());

        //Listener should still be available but Stage close/terminated
        planItems = cmmnRuntimeService.createPlanItemInstanceQuery()
                .list().stream().collect(Collectors.groupingBy(PlanItemInstance::getPlanItemDefinitionType));
        assertEquals(1, planItems.size());
        assertEquals(1, planItems.getOrDefault(PlanItemDefinitionType.USER_EVENT_LISTENER, Collections.emptyList()).size());
        listener = planItems.get(PlanItemDefinitionType.USER_EVENT_LISTENER).get(0);

        //Trigger the listener should end the case
        cmmnRuntimeService.triggerPlanItemInstance(listener.getId());

        //Stage and case instance should have ended...
    }
    /**
     * <case id="testAutocompleteStageWithoutFiringTheEvent" name="testAutocompleteStageWithoutFiringTheEvent">
        <casePlanModel id="casePlanModel">

            <planItem id="planItemStage" name="Stage One" definitionRef="stage1"/>

            <stage id="stage1" name="Stage One" autoComplete="true">
                <planItem id="planItemTaskA" name="Task A" definitionRef="taskA">
                    <exitCriterion sentryRef="onUserEventAbortTaskSentry"/>
                </planItem>
                <planItem id="planItemUserEventAbortTask" definitionRef="abortTaskUserEvent"/>
                <sentry id="onUserEventAbortTaskSentry">
                    <planItemOnPart sourceRef="planItemUserEventAbortTask">
                        <standardEvent>occur</standardEvent>
                    </planItemOnPart>
                </sentry>

                <task id="taskA" name="Task A"/>

                <userEventListener id="abortTaskUserEvent" name="Abort Task"/>
            </stage>

        </casePlanModel>
    </case>
    
    1、planItemUserEventAbortTask是available状态。
    2、完成任务之后，所有的实例完成
     */
    @Test
    public void testAutocompleteStageWithoutFiringTheEvent() {
        //Test case where the only "standing" plainItem for a autocomplete stage is a UserEventListener
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testAutocompleteStageWithoutFiringTheEvent")
                .start();
        assertNotNull(caseInstance);
      //  assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());

        //3 PlanItems reachable
        assertEquals(3, cmmnRuntimeService.createPlanItemInstanceQuery().count());

        //1 Stage
        PlanItemInstance stage = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.STAGE).singleResult();
        assertNotNull(stage);

        //1 User Event Listener
        List<PlanItemInstance> listeners = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).list();
        assertEquals(1, listeners.size());
        listeners.forEach(l -> assertEquals(PlanItemInstanceState.AVAILABLE, l.getState()));

        //1 Tasks on Active state
        PlanItemInstance task = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType("task").singleResult();
        assertNotNull(task);
        assertEquals(PlanItemInstanceState.ACTIVE, task.getState());
        assertEquals(stage.getId(), task.getStageInstanceId());

        //Complete the Task
        cmmnRuntimeService.triggerPlanItemInstance(task.getId());

        //Stage and case instance should have ended...
      //  assertCaseInstanceEnded(caseInstance);
    }

    @Test
    public void testUserEventListenerInstanceQuery() {
        //Test for UserEventListenerInstanceQuery
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey(name.getMethodName())
                .start();
        assertNotNull(caseInstance);
        assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());

        //All planItemInstances
        assertEquals(8, cmmnRuntimeService.createPlanItemInstanceQuery().count());

        //UserEventListenerIntances
        assertEquals(6, cmmnRuntimeService.createUserEventListenerInstanceQuery().stateAvailable().count());

        List<UserEventListenerInstance> events = cmmnRuntimeService.createUserEventListenerInstanceQuery().list();
        assertEquals(6, events.size());

        //All different Intances id's
        assertEquals(6, events.stream().map(UserEventListenerInstance::getId).distinct().count());

        //UserEventListenerIntances inside Stage1
        String stage1Id = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.STAGE).planItemDefinitionId("stage1").singleResult().getId();
        assertEquals(2, cmmnRuntimeService.createUserEventListenerInstanceQuery().stageInstanceId(stage1Id).count());

        //UserEventListenerIntances inside Stage2
        String stage2Id = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.STAGE).planItemDefinitionId("stage2").singleResult().getId();
        assertEquals(2, cmmnRuntimeService.createUserEventListenerInstanceQuery().stageInstanceId(stage2Id).count());

        //UserEventListenerIntances not in a Stage
        assertEquals(2, events.stream().filter(e -> e.getStageIntanceId() == null).count());

        //Test query by elementId
        assertNotNull(cmmnRuntimeService.createUserEventListenerInstanceQuery().elementId("caseUserEventListenerOne").singleResult());

        //Test query by planItemDefinitionId
        assertEquals(2, cmmnRuntimeService.createUserEventListenerInstanceQuery().planItemDefinitionId("caseUEL1").count());

        //Test sort Order - using the names because equals is not implemented in UserEventListenerInstance
        List<String> names = events.stream().map(UserEventListenerInstance::getName).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        List<String> namesDesc = cmmnRuntimeService.createUserEventListenerInstanceQuery()
                .stateAvailable().orderByName().desc().list().stream().map(UserEventListenerInstance::getName).collect(Collectors.toList());
        assertThat(names, is(namesDesc));


    }
}

