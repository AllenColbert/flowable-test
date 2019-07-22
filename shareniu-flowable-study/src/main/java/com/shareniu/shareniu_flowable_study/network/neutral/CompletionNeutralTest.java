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
package com.shareniu.shareniu_flowable_study.network.neutral;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;

/**
 * 中性的
 * 
 */
public class CompletionNeutralTest  {
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
	.addClasspathResource("com/shareniu/shareniu_flowable_study/network/neutral/ManualActivationRule1.cmmn.xml")
				.deploy()
				.getId();
	}
	
	/**
	 * 如果是true，则状态为enabled 不能调用triggerPlanItemInstance方法。
	 * 如果false，则状态为active
	 */
	 @Test
	    public void manualActivationRule() {
		 CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("ManualActivationRule")
				 .variable("aa", true)
				 .start();
	 }
	 @Test
	 public void manualActivationRule1() {
		String planItemInstanceId="147aa782-487b-11e8-a412-a652884ed93a";
	cmmnRuntimeService.triggerPlanItemInstance(planItemInstanceId);
		
		//cmmnRuntimeService.evaluateCriteria("147a334f-487b-11e8-a412-a652884ed93a");
		
	//	cmmnRuntimeService.startPlanItemInstance(planItemInstanceId);
	 }
    @Rule
    public TestName name = new TestName();
    /**
     *  <case id="testSimpleStageCompletion">

        <casePlanModel id="myCompletionNeutralTestPlanModel" name="My completion neutral test plan model">
            <documentation>TaskB inside the Stage will be AVILABLE waiting for TaskA outside the stage. Once TaskC completes the stage should complete regardless of whether or not TaskA completed</documentation>

            <planItem id="taskBWaitingForThis" name="Task B is Available waiting for its completion" definitionRef="taskA"/>
            <planItem id="theStage" name="this stage should complete after taskA is completed" definitionRef="stageOne"/>

            <humanTask id="taskA"/>
            <stage id="stageOne">
                <planItem id="manuallyCompletedTask" name="manually completed task" definitionRef="taskC"/>
                <planItem id="completionNeutralTask" name="Completion Neutral Task" definitionRef="taskB">
                    <itemControl>
                        <extensionElements>
                            <flowable:completionNeutralRule/>
                        </extensionElements>
                    </itemControl>
                    <entryCriterion sentryRef="onTaskACompleteSentry"/>
                </planItem>
                <sentry id="onTaskACompleteSentry">
                    <planItemOnPart sourceRef="taskBWaitingForThis">
                        <standardEvent>complete</standardEvent>
                    </planItemOnPart>
                </sentry>

                <humanTask id="taskC"/>
                <humanTask id="taskB"/>
            </stage>
        </casePlanModel>
    </case>
    1、B 的状态是available
    2、触发C B结束了、stage也结束了
     */
    @Test
    public void testSimpleStageCompletion() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        assertNotNull(caseInstance);

        //Check case setup
        assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().count());

        PlanItemInstance taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        assertEquals(PlanItemInstanceState.ACTIVE, taskA.getState());

        PlanItemInstance stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNotNull(stageOne);
        assertEquals(PlanItemInstanceState.ACTIVE, stageOne.getState());

        PlanItemInstance taskB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskB").singleResult();
        assertNotNull(taskB);
        assertEquals(PlanItemInstanceState.AVAILABLE, taskB.getState());

        PlanItemInstance taskC = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskC").singleResult();
        assertNotNull(taskC);
        assertEquals(PlanItemInstanceState.ACTIVE, taskC.getState());

        //Trigger the test
        cmmnRuntimeService.triggerPlanItemInstance(taskC.getId());

        taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNull(stageOne);
        taskB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskB").singleResult();
        assertNull(taskB);
        taskC = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskC").singleResult();
        assertNull(taskC);


        cmmnRuntimeService.triggerPlanItemInstance(taskA.getId());
    }
    /**
     * <case id="testStagedEventListenerBypassed">

        <casePlanModel id="myCompletionNeutralTestPlanModel" name="My completion neutral test plan model">
            <planItem id="keepAliveTask" name="this task keeps alive the plan after the stage completes" definitionRef="taskA"/>
            <planItem id="theStage" name="this stage should complete after taskB is completed" definitionRef="stageOne"/>

            <task id="taskA"/>
            <stage id="stageOne" name="A stage that will remain with only completion neutral items">
                <planItem id="manuallyCompletedTask" name="manually completed task" definitionRef="taskB">
                    <exitCriterion sentryRef="onUserEventAbortTaskSentry"/>
                </planItem>
                <planItem id="completionNeutraEventListener" name="A completion Neutral event listener" definitionRef="abortTaskUserEventListener">
                    <itemControl>
                        <extensionElements>
                            <flowable:completionNeutralRule/>
                        </extensionElements>
                    </itemControl>
                </planItem>
                <sentry id="onUserEventAbortTaskSentry">
                    <planItemOnPart sourceRef="completionNeutraEventListener">
                        <standardEvent>occur</standardEvent>
                    </planItemOnPart>
                </sentry>
                <task id="taskB"/>
                <userEventListener id="abortTaskUserEventListener" name="Abort Task"/>
            </stage>
        </casePlanModel>
    </case>
    1、usereventlistener 的状态是available
    2、完成B所有的stage里面的实例都结束
    TODO 没看明白
     */
    @Test
    public void testStagedEventListenerBypassed() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        assertNotNull(caseInstance);

        //Check case setup
        assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().count());

        PlanItemInstance taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        assertEquals(PlanItemInstanceState.ACTIVE, taskA.getState());

        PlanItemInstance stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNotNull(stageOne);
        assertEquals(PlanItemInstanceState.ACTIVE, stageOne.getState());

        PlanItemInstance taskB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskB").singleResult();
        assertNotNull(taskB);
        assertEquals(PlanItemInstanceState.ACTIVE, taskB.getState());

        PlanItemInstance listener = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).singleResult();
        assertNotNull(listener);
        assertEquals(PlanItemInstanceState.AVAILABLE, listener.getState());

        //Trigger the test
        cmmnRuntimeService.triggerPlanItemInstance(taskB.getId());

        taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNull(stageOne);
        taskB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskB").singleResult();
        assertNull(taskB);
        listener = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).singleResult();
        assertNull(listener);

        //End the case
        cmmnRuntimeService.triggerPlanItemInstance(taskA.getId());
    }
    /**
     *  <case id="testEventListenerBypassed">

        <casePlanModel id="myCompletionNeutralTestPlanModel" name="My completion neutral test plan model">
            <documentation>Task B inside the Stage has a Exit Criteria on a Completion Neutral Event Listener, the stage should complete
                when TaskB complete even it the event is not triggered
            </documentation>

            <planItem id="keepAliveTask" name="this task keeps alive the plan after the stage completes" definitionRef="taskA"/>

            <planItem id="manuallyCompletedTask" name="manually completed task" definitionRef="taskB">
                <exitCriterion sentryRef="onUserEventAbortTaskSentry"/>
            </planItem>
            <planItem id="completionNeutraEventListener" name="A completion Neutral event listener" definitionRef="abortTaskUserEventListener">
                <itemControl>
                    <extensionElements>
                        <flowable:completionNeutralRule/>
                    </extensionElements>
                </itemControl>
            </planItem>
            <sentry id="onUserEventAbortTaskSentry">
                <planItemOnPart sourceRef="completionNeutraEventListener">
                    <standardEvent>occur</standardEvent>
                </planItemOnPart>
            </sentry>
            <task id="taskA"/>
            <task id="taskB"/>
            <userEventListener id="abortTaskUserEventListener" name="Abort Task"/>
        </casePlanModel>
    </case>
    TODO 没看明白
     */
    @Test
    public void testEventListenerBypassed() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        assertNotNull(caseInstance);

        //Check case setup
        assertEquals(3, cmmnRuntimeService.createPlanItemInstanceQuery().count());

        PlanItemInstance taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        assertEquals(PlanItemInstanceState.ACTIVE, taskA.getState());

        PlanItemInstance taskB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskB").singleResult();
        assertNotNull(taskB);
        assertEquals(PlanItemInstanceState.ACTIVE, taskB.getState());

        PlanItemInstance listener = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).singleResult();
        assertNotNull(listener);
        assertEquals(PlanItemInstanceState.AVAILABLE, listener.getState());

        //Trigger the test
        cmmnRuntimeService.triggerPlanItemInstance(taskB.getId());

        taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        taskB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskB").singleResult();
        assertNull(taskB);
        listener = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).singleResult();
        assertNotNull(listener);
        assertEquals(PlanItemInstanceState.AVAILABLE, listener.getState());

        //End the case
        cmmnRuntimeService.triggerPlanItemInstance(taskA.getId());
    }

    @Test
    public void testEmbeddedStage() { 
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        assertNotNull(caseInstance);

        //Check case setup
        assertEquals(6, cmmnRuntimeService.createPlanItemInstanceQuery().count());

        PlanItemInstance taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        assertEquals(PlanItemInstanceState.ACTIVE, taskA.getState());

        PlanItemInstance stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNotNull(stageOne);
        assertEquals(PlanItemInstanceState.ACTIVE, stageOne.getState());

        PlanItemInstance taskB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskB").singleResult();
        assertNotNull(taskB);
        assertEquals(PlanItemInstanceState.AVAILABLE, taskB.getState());

        PlanItemInstance stageTwo = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("completionNeutralStage").singleResult();
        assertNotNull(stageTwo);
        assertEquals(PlanItemInstanceState.ACTIVE, stageTwo.getState());

        PlanItemInstance taskC = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskC").singleResult();
        assertNotNull(taskC);
        assertEquals(PlanItemInstanceState.AVAILABLE, taskC.getState());

        PlanItemInstance taskD = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskD").singleResult();
        assertNotNull(taskD);
        assertEquals(PlanItemInstanceState.ACTIVE, taskD.getState());

        //Trigger the test
        cmmnRuntimeService.triggerPlanItemInstance(taskD.getId());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        cmmnRuntimeService.triggerPlanItemInstance(taskA.getId());
    }

    @Test
    @CmmnDeployment
    public void testRequiredPrecedence() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        assertNotNull(caseInstance);

        //Check case setup
        assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().count());

        PlanItemInstance taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        assertEquals(PlanItemInstanceState.ACTIVE, taskA.getState());

        PlanItemInstance stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNotNull(stageOne);
        assertEquals(PlanItemInstanceState.ACTIVE, stageOne.getState());

        PlanItemInstance taskB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskB").singleResult();
        assertNotNull(taskB);
        assertEquals(PlanItemInstanceState.AVAILABLE, taskB.getState());

        PlanItemInstance taskC = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskC").singleResult();
        assertNotNull(taskC);
        assertEquals(PlanItemInstanceState.ACTIVE, taskC.getState());

        //Trigger the test
        cmmnRuntimeService.triggerPlanItemInstance(taskC.getId());
        assertCaseInstanceNotEnded(caseInstance);
        assertEquals(3, cmmnRuntimeService.createPlanItemInstanceQuery().count());

        taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNotNull(stageOne);
        taskB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskB").singleResult();
        assertNotNull(taskB);
        taskC = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskC").singleResult();
        assertNull(taskC);

        cmmnRuntimeService.triggerPlanItemInstance(taskA.getId());
        assertCaseInstanceNotEnded(caseInstance);
        assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        cmmnRuntimeService.triggerPlanItemInstance(taskB.getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testRequiredPrecedenceDeepNest() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        assertNotNull(caseInstance);

        List<PlanItemInstance> list = cmmnRuntimeService.createPlanItemInstanceQuery().list();

        //Check case setup
        assertEquals(5, cmmnRuntimeService.createPlanItemInstanceQuery().count());

        PlanItemInstance taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        assertEquals(PlanItemInstanceState.ACTIVE, taskA.getState());

        PlanItemInstance stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNotNull(stageOne);
        assertEquals(PlanItemInstanceState.AVAILABLE, stageOne.getState());

        List<PlanItemInstance> listeners = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).list();
        assertEquals(3, listeners.size());
        listeners.forEach(l -> assertEquals(PlanItemInstanceState.AVAILABLE,l.getState()));

        //Trigger the test
        //Triggering Listener One will Activate StageOne which will complete as nothing ties it
        PlanItemInstance userEventOne = cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER)
                .planItemDefinitionId("userEventOne").singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(userEventOne.getId());
        stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNull(stageOne);
        listeners = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).list();
        assertEquals(2, listeners.size());
        assertCaseInstanceNotEnded(caseInstance);

        //The only thing keeping the case from ending is TaskA even with a deep nested required task, because its not AVAILABLE yet
        cmmnRuntimeService.triggerPlanItemInstance(taskA.getId());
        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testRequiredPrecedenceDeepNest2() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        assertNotNull(caseInstance);

        //Check case setup
        assertEquals(5, cmmnRuntimeService.createPlanItemInstanceQuery().count());

        PlanItemInstance taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        assertEquals(PlanItemInstanceState.ACTIVE, taskA.getState());

        PlanItemInstance stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNotNull(stageOne);
        assertEquals(PlanItemInstanceState.AVAILABLE, stageOne.getState());

        List<PlanItemInstance> listeners = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).list();
        assertEquals(3, listeners.size());
        listeners.forEach(l -> assertEquals(PlanItemInstanceState.AVAILABLE,l.getState()));

        //Trigger the test
        //This time a task inside StageOne is required, thus it will not complete once activated
        PlanItemInstance userEvent = cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER)
                .planItemDefinitionId("userEventOne").singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(userEvent.getId());

        assertEquals(6, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        listeners = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).list();
        assertEquals(2, listeners.size());
        taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        assertEquals(PlanItemInstanceState.ACTIVE, taskA.getState());
        stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNotNull(stageOne);
        assertEquals(PlanItemInstanceState.ACTIVE, stageOne.getState());
        PlanItemInstance stageTwo = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageTwo").singleResult();
        assertNotNull(stageTwo);
        assertEquals(PlanItemInstanceState.AVAILABLE, stageTwo.getState());
        PlanItemInstance taskB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskB").singleResult();
        assertNotNull(taskB);
        assertEquals(PlanItemInstanceState.AVAILABLE, taskB.getState());

        //Completing taskB and then taskA should end the case
        //Order is important since required taskC nested in StageTwo is not yet available
        //And completing TaskA first will make taskC available
        //But first TaskB needs to become Active
        userEvent = cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER)
                .planItemDefinitionId("userEventTwo").singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(userEvent.getId());
        cmmnRuntimeService.triggerPlanItemInstance(taskB.getId());
        cmmnRuntimeService.triggerPlanItemInstance(taskA.getId());

        assertCaseInstanceEnded(caseInstance);
    }

    @Test
    @CmmnDeployment
    public void testRequiredPrecedenceDeepNest3() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey(name.getMethodName()).start();
        assertNotNull(caseInstance);

        //Check case setup
        assertEquals(5, cmmnRuntimeService.createPlanItemInstanceQuery().count());

        PlanItemInstance taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        assertEquals(PlanItemInstanceState.ACTIVE, taskA.getState());

        PlanItemInstance stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNotNull(stageOne);
        assertEquals(PlanItemInstanceState.AVAILABLE, stageOne.getState());

        List<PlanItemInstance> listeners = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).list();
        assertEquals(3, listeners.size());
        listeners.forEach(l -> assertEquals(PlanItemInstanceState.AVAILABLE,l.getState()));


        //Trigger the test
        //This time a task inside StageOne is required, thus it will not complete once activated
        PlanItemInstance userEvent = cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER)
                .planItemDefinitionId("userEventOne").singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(userEvent.getId());
        assertCaseInstanceNotEnded(caseInstance);

        assertEquals(6, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        listeners = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).list();
        assertEquals(2, listeners.size());
        taskA = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskA").singleResult();
        assertNotNull(taskA);
        assertEquals(PlanItemInstanceState.ACTIVE, taskA.getState());
        stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNotNull(stageOne);
        assertEquals(PlanItemInstanceState.ACTIVE, stageOne.getState());
        PlanItemInstance stageTwo = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageTwo").singleResult();
        assertNotNull(stageTwo);
        assertEquals(PlanItemInstanceState.AVAILABLE, stageTwo.getState());
        PlanItemInstance taskB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskB").singleResult();
        assertNotNull(taskB);
        assertEquals(PlanItemInstanceState.AVAILABLE, taskB.getState());

        //This time we complete taskA first, making stageTwo Active,
        //making available the required taskC
        cmmnRuntimeService.triggerPlanItemInstance(taskA.getId());
        userEvent = cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER)
                .planItemDefinitionId("userEventTwo").singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(userEvent.getId());
        cmmnRuntimeService.triggerPlanItemInstance(taskB.getId());
        assertCaseInstanceNotEnded(caseInstance);

        List<PlanItemInstance> list = cmmnRuntimeService.createPlanItemInstanceQuery().list();
        assertEquals(4, cmmnRuntimeService.createPlanItemInstanceQuery().count());
        listeners = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER).list();
        assertEquals(1, listeners.size());
        stageOne = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageOne").singleResult();
        assertNotNull(stageOne);
        assertEquals(PlanItemInstanceState.ACTIVE, stageOne.getState());
        stageTwo = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("stageTwo").singleResult();
        assertNotNull(stageTwo);
        assertEquals(PlanItemInstanceState.ACTIVE, stageTwo.getState());
        PlanItemInstance taskC = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionId("taskC").singleResult();
        assertNotNull(taskC);
        assertEquals(PlanItemInstanceState.AVAILABLE, taskC.getState());

        //Now we need to activate TaskC and complete it to end the case
        userEvent = cmmnRuntimeService.createPlanItemInstanceQuery()
                .planItemDefinitionType(PlanItemDefinitionType.USER_EVENT_LISTENER)
                .planItemDefinitionId("userEventThree").singleResult();
        cmmnRuntimeService.triggerPlanItemInstance(userEvent.getId());
        cmmnRuntimeService.triggerPlanItemInstance(taskC.getId());
        assertCaseInstanceEnded(caseInstance);
    }
}
