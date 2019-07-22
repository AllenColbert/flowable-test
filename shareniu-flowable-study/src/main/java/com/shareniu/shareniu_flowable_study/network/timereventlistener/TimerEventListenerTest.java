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
package com.shareniu.shareniu_flowable_study.network.timereventlistener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Calendar;
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
import org.flowable.cmmn.engine.test.impl.CmmnJobTestHelper;
import org.flowable.cmmn.model.HumanTask;
import org.flowable.cmmn.model.Stage;
import org.flowable.cmmn.model.TimerEventListener;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;

/**
 * @author Joram Barrez
 */
public class TimerEventListenerTest  {
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
		dc= CmmnEngineConfiguration.createCmmnEngineConfigurationFromInputStream(in);
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
		//	"com/shareniu/shareniu_flowable_study/network/timereventlistener/TimerEventListenerTest.testTimerExpressionDuration.cmmn")
			"com/shareniu/shareniu_flowable_study/network/timereventlistener/TimerEventListenerTest.testTimerExpressionDurationWithRealAsyncExeutor.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/timereventlistener/TimerEventListenerTest.testStageAfterTimer.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/timereventlistener/TimerEventListenerTest.testTimerInStage.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/timereventlistener/TimerEventListenerTest.testExitPlanModelOnTimerOccurrence.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/timereventlistener/TimerEventListenerTest.testDateExpression.cmmn")
			//"com/shareniu/shareniu_flowable_study/cmmn/timereventlistener/TimerEventListenerTest.testTimerWithBeanExpression.cmmn")
			//"com/shareniu/shareniu_flowable_study/cmmn/timereventlistener/TimerEventListenerTest.testTimerStartTrigger.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/timereventlistener/TimerEventListenerTest.testExitNestedStageThroughTimer.cmmn")
		//	"com/shareniu/shareniu_flowable_study/cmmn/timereventlistener/TimerEventListenerTest.timerActivatesAndExitStages.cmmn")
				.deploy().getId();
	}
	/**
	 * 
	 * <case id="testTimerExpression" name="testTimerExpression" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem1" definitionRef="sid-253E056E-C02F-479A-AAA9-17D8A372F846"></planItem>
      <planItem id="planItem2" name="A" definitionRef="sid-E85224AB-66DD-4C8C-8809-4F50DC117067">
        <entryCriterion id="sid-D752F3E8-D736-4A1C-9C65-FF012CBB47AA" sentryRef="sentry1"></entryCriterion>
      </planItem>
      <sentry id="sentry1">
        <planItemOnPart id="sentryOnPart1" sourceRef="planItem1">
          <standardEvent>occur</standardEvent>
        </planItemOnPart>
      </sentry>
      <timerEventListener id="sid-253E056E-C02F-479A-AAA9-17D8A372F846">
        <timerExpression><![CDATA[PT1H]]></timerExpression>
      </timerEventListener>
      <humanTask id="sid-E85224AB-66DD-4C8C-8809-4F50DC117067" name="A"></humanTask>
    </casePlanModel>
  </case>
	 * 
	 * 1、启动实例都是available状态
	 * 2、ACT_RU_TIMER_JOB表有一条数据
	 * 3、ACT_RU_JOB有数据 
	 * cmmnManagementService.moveTimerToExecutableJob(timerJob.getId());
        cmmnManagementService.executeJob(timerJob.getId());
        4、User task should be active after the timer has triggered
	 */
    @Test
    public void testTimerExpressionDuration() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTimerExpression").start();
        assertNotNull(caseInstance);
//        assertEquals(1, cmmnRuntimeService.createCaseInstanceQuery().count());
        
        assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.TIMER_EVENT_LISTENER).planItemInstanceStateAvailable().singleResult());
        assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK).planItemInstanceStateAvailable().singleResult());
        
        //assertEquals(1L, cmmnManagementService.createTimerJobQuery().count());
        //assertEquals(1L, cmmnManagementService.createTimerJobQuery().scopeId(caseInstance.getId()).scopeType(ScopeTypes.CMMN).count());
        
        // User task should not be active before the timer triggers
        assertEquals(0L, cmmnTaskService.createTaskQuery().count());
        
        Job timerJob = cmmnManagementService.createTimerJobQuery().scopeDefinitionId(caseInstance.getCaseDefinitionId()).singleResult();
        assertNotNull(timerJob);
        cmmnManagementService.moveTimerToExecutableJob(timerJob.getId());
        cmmnManagementService.executeJob(timerJob.getId());
        
        // User task should be active after the timer has triggered
        assertEquals(1L, cmmnTaskService.createTaskQuery().count());
    }
    
    /**
     * Similar test as #testTimerExpressionDuration but with the real async executor, 
     * instead of manually triggering the timer. 
     * 
     * 
  模板同上
  1、  // User task should not be active before the timer triggers
     //   assertEquals(0L, cmmnTaskService.createTaskQuery().count());

        // Timer fires after 1 hour, so setting it to 1 hours + 1 second
        setClockTo(new Date(startTime.getTime() + (60 * 60 * 1000 + 1)));

        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(dc, 5000L, 200L, true);
     */
    @Test
    public void testTimerExpressionDurationWithRealAsyncExeutor() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTimerExpression").start();

        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.AVAILABLE)
                .planItemDefinitionType(PlanItemDefinitionType.TIMER_EVENT_LISTENER)
                .singleResult();
        assertNotNull(planItemInstance);

        // User task should not be active before the timer triggers
     //   assertEquals(0L, cmmnTaskService.createTaskQuery().count());

        // Timer fires after 1 hour, so setting it to 1 hours + 1 second
        setClockTo(new Date(startTime.getTime() + (60 * 60 * 1000 + 1)));

        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(dc, 5000L, 200L, true);

        // User task should be active after the timer has triggered 
        assertEquals(1L, cmmnTaskService.createTaskQuery().count());
    }
    protected void setClockTo(Date date) {
        dc.getClock().setCurrentTime(date);
    }
    protected Date setClockFixedToCurrentTime() {
        Date date = new Date();
        dc.getClock().setCurrentTime(date);
        return date;
    }
    /**
     * 
     *   <case id="testStageAfterTimerEventListener" name="testStageAfterTimerEventListener" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem1" definitionRef="sid-0C9B53FA-D5A7-435E-A7C1-DD17BC00F729"></planItem>
      <planItem id="planItem4" definitionRef="sid-4DBD6FE0-C6FA-404E-BA87-A663B3B557AC">
        <entryCriterion id="sid-C356A944-08F8-450E-943F-31F7C4155260" sentryRef="sentry1"></entryCriterion>
      </planItem>
      <sentry id="sentry1">
        <planItemOnPart id="sentryOnPart1" sourceRef="planItem1">
          <standardEvent>occur</standardEvent>
        </planItemOnPart>
      </sentry>
      <timerEventListener id="sid-0C9B53FA-D5A7-435E-A7C1-DD17BC00F729">
        <timerExpression><![CDATA[P1D]]></timerExpression>
      </timerEventListener>
      <stage id="sid-4DBD6FE0-C6FA-404E-BA87-A663B3B557AC">
        <planItem id="planItem2" name="A" definitionRef="sid-AF42AAD9-277F-4F5F-BD75-F14A7B2CEECE"></planItem>
        <planItem id="planItem3" name="B" definitionRef="sid-45C5DB26-A8DF-4318-9DC6-AADDC8ADB64C"></planItem>
        <humanTask id="sid-AF42AAD9-277F-4F5F-BD75-F14A7B2CEECE" name="A"></humanTask>
        <humanTask id="sid-45C5DB26-A8DF-4318-9DC6-AADDC8ADB64C" name="B"></humanTask>
      </stage>
    </casePlanModel>
  </case>
  planItem1和 planItem4 available
  触发时间之后，A和B开始运转 
     */
    @Test
    public void testStageAfterTimer() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testStageAfterTimerEventListener").start();
        
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.AVAILABLE)
                .planItemDefinitionType(PlanItemDefinitionType.TIMER_EVENT_LISTENER)
                .singleResult();
        assertNotNull(planItemInstance);
        
       // assertEquals(0L, cmmnTaskService.createTaskQuery().count());
        
        // Timer fires after 1 day, so setting it to 1 day + 1 second
        setClockTo(new Date(startTime.getTime() + (24 * 60 * 60 * 1000 + 1)));
        
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(dc, 5000L, 200L, true);
        
        // User task should be active after the timer has triggered 
        assertEquals(2L, cmmnTaskService.createTaskQuery().count());
    }
    /**
     * 
     * <case id="testTimerInStage" name="testTimerInStage" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem1" definitionRef="sid-0FBA0ED3-B411-4BCA-BEAE-E5804873DBB9"></planItem>
      <planItem id="planItem2" name="A" definitionRef="sid-9407850A-9091-4785-8975-7B1474A4ACCE"></planItem>
      <planItem id="planItem3" name="B" definitionRef="sid-8FC33D19-AE10-44FD-8960-288F1DFB27CD">
        <entryCriterion id="sid-E922634C-4D01-4A13-9300-B3D7DB2FDEB6" sentryRef="sentry1"></entryCriterion>
      </planItem>
      <sentry id="sentry1">
        <planItemOnPart id="sentryOnPart1" sourceRef="planItem1">
          <standardEvent>occur</standardEvent>
        </planItemOnPart>
      </sentry>
      <timerEventListener id="sid-0FBA0ED3-B411-4BCA-BEAE-E5804873DBB9">
        <timerExpression><![CDATA[PT3H]]></timerExpression>
      </timerEventListener>
      <humanTask id="sid-9407850A-9091-4785-8975-7B1474A4ACCE" name="A"></humanTask>
      <humanTask id="sid-8FC33D19-AE10-44FD-8960-288F1DFB27CD" name="B"></humanTask>
    </casePlanModel>
  </case>
  1、启动实例只有a 
   2/ User task should be active after the timer has triggered
     *  
     */
    @Test
    public void testTimerInStage() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTimerInStage").start();
        
        assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceState(PlanItemInstanceState.ACTIVE).count());
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.AVAILABLE)
                .planItemDefinitionType(PlanItemDefinitionType.TIMER_EVENT_LISTENER)
                .singleResult();
        assertNotNull(planItemInstance);
        
        assertEquals(1L, cmmnTaskService.createTaskQuery().count());
        
        // Timer fires after 3 hours, so setting it to 3 hours + 1 second
        setClockTo(new Date(startTime.getTime() + (3 * 60 * 60 * 1000 + 1)));
        
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(dc, 5000L, 200L, true);
        
        // User task should be active after the timer has triggered
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals(2, tasks.size());
        assertEquals("A", tasks.get(0).getName());
        assertEquals("B", tasks.get(1).getName());
    }
    /**
     * <case id="testStageExitOnTimerOccurrence" name="testStageExitOnTimerOccurrence" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem1" definitionRef="sid-70070696-CCA4-4028-A3FA-5B587E018E05"></planItem>
      <planItem id="planItem2" name="A" definitionRef="sid-6FF3770A-BD9A-48B5-A923-31386B33C56E"></planItem>
      <planItem id="planItem3" name="B" definitionRef="sid-7BE76536-5D18-438F-93DE-0847F0FA23B2"></planItem>
      <planItem id="planItem5" definitionRef="sid-1ACB3F70-0179-48BC-B9DE-537EF2CBA087"></planItem>
      <sentry id="sentry1">
        <planItemOnPart id="sentryOnPart1" sourceRef="planItem1">
          <standardEvent>occur</standardEvent>
        </planItemOnPart>
      </sentry>
      <timerEventListener id="sid-70070696-CCA4-4028-A3FA-5B587E018E05">
        <timerExpression><![CDATA[PT24H]]></timerExpression>
      </timerEventListener>
      <humanTask id="sid-6FF3770A-BD9A-48B5-A923-31386B33C56E" name="A"></humanTask>
      <humanTask id="sid-7BE76536-5D18-438F-93DE-0847F0FA23B2" name="B"></humanTask>
      <stage id="sid-1ACB3F70-0179-48BC-B9DE-537EF2CBA087">
        <planItem id="planItem4" name="C" definitionRef="sid-FC8D163E-C196-4DCE-824C-67DEECA0D5CF"></planItem>
        <humanTask id="sid-FC8D163E-C196-4DCE-824C-67DEECA0D5CF" name="C"></humanTask>
      </stage>
      <exitCriterion id="sid-E185183B-7DF1-41EB-9C9B-BD3ED080A79D" sentryRef="sentry1"></exitCriterion>
    </casePlanModel>
  </case>
  timerEventListener激活并且类型是exitCriterion的  则整个实例结束
     */
    @Test
    public void testExitPlanModelOnTimerOccurrence() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testStageExitOnTimerOccurrence").start();
        
        assertEquals(3L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE).planItemDefinitionType(PlanItemDefinitionType.HUMAN_TASK).count());
        assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.AVAILABLE).planItemDefinitionType(PlanItemDefinitionType.TIMER_EVENT_LISTENER).count());
        assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE).planItemDefinitionType(PlanItemDefinitionType.STAGE).count());
        assertEquals(4L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE).count());
        
        // Timer fires after 24 hours, so setting it to 24 hours + 1 second
        setClockTo(new Date(startTime.getTime() + (24 * 60 * 60 * 1000 + 1)));
        
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(dc, 5000L, 200L, true);
        
        assertEquals(0L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        assertEquals(0L, cmmnRuntimeService.createCaseInstanceQuery().count());
    }
    /**
     * <case id="testDateExpression" name="testDateExpression" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem1" definitionRef="sid-A710B28D-A4BD-4FBC-94BE-C1EBE2D03135"></planItem>
      <planItem id="planItem3" definitionRef="sid-6EEDC33C-C031-4E9E-98F1-A4BEA8548078">
        <entryCriterion id="sid-3CD24137-652C-4C16-83F3-69E28EBC8CE1" sentryRef="sentry1"></entryCriterion>
      </planItem>
      <planItem id="planItem5" definitionRef="sid-CDC5EF6E-AB88-425D-86B1-A0572CE3B9F5">
        <entryCriterion id="sid-A62EF406-29E9-4EC8-913D-256C6D1AB6DD" sentryRef="sentry2"></entryCriterion>
      </planItem>
      <planItem id="planItem6" name="C" definitionRef="sid-EE824B9E-5F44-47DA-8990-C458ED17C250">
        <entryCriterion id="sid-743C6B8C-95FB-443F-9297-81AD32638A53" sentryRef="sentry3"></entryCriterion>
      </planItem>
      <sentry id="sentry1">
        <planItemOnPart id="sentryOnPart1" sourceRef="planItem1">
          <standardEvent>occur</standardEvent>
        </planItemOnPart>
      </sentry>
      <sentry id="sentry2">
        <planItemOnPart id="sentryOnPart2" sourceRef="planItem1">
          <standardEvent>occur</standardEvent>
        </planItemOnPart>
      </sentry>
      <sentry id="sentry3">
        <planItemOnPart id="sentryOnPart3" sourceRef="planItem1">
          <standardEvent>occur</standardEvent>
        </planItemOnPart>
      </sentry>
      <timerEventListener id="sid-A710B28D-A4BD-4FBC-94BE-C1EBE2D03135">
        <timerExpression><![CDATA[2017-12-05T10:00]]></timerExpression>
      </timerEventListener>
      <stage id="sid-6EEDC33C-C031-4E9E-98F1-A4BEA8548078">
        <planItem id="planItem2" name="A" definitionRef="sid-7C6BC659-1354-4DB5-86AD-7E3A3FF6F3BA"></planItem>
        <humanTask id="sid-7C6BC659-1354-4DB5-86AD-7E3A3FF6F3BA" name="A"></humanTask>
      </stage>
      <stage id="sid-CDC5EF6E-AB88-425D-86B1-A0572CE3B9F5">
        <planItem id="planItem4" name="B" definitionRef="sid-6778AF76-62C5-4331-99A1-40603274D722"></planItem>
        <humanTask id="sid-6778AF76-62C5-4331-99A1-40603274D722" name="B"></humanTask>
      </stage>
      <humanTask id="sid-EE824B9E-5F44-47DA-8990-C458ED17C250" name="C"></humanTask>
    </casePlanModel>
  </case>
  时间监听器触发之后，则entryCriterion 都会激活状态
     */
    @Test
    public void testDateExpression() {
        
        // Timer will fire on 2017-12-05T10:00
        // So moving the clock to the day before
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, 12);
        calendar.set(Calendar.DAY_OF_MONTH, 4);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 0);
        Date dayBefore = calendar.getTime();
        setClockTo(dayBefore);
        
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testDateExpression").start();
        
        assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.AVAILABLE).planItemDefinitionType(TimerEventListener.class.getSimpleName().toLowerCase()).count());
        assertEquals(2L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.AVAILABLE).planItemDefinitionType(Stage.class.getSimpleName().toLowerCase()).count());
        assertEquals(0L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE).planItemDefinitionType(Stage.class.getSimpleName().toLowerCase()).count());
        assertEquals(0L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE).planItemDefinitionType(HumanTask.class.getSimpleName().toLowerCase()).count());
        assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.AVAILABLE).planItemDefinitionType(HumanTask.class.getSimpleName().toLowerCase()).count());
        
        assertEquals(0L, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        setClockTo(new Date(dayBefore.getTime() + (24 * 60 * 60 * 1000)));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(dc, 5000L, 200L, true);
        
        assertEquals(0L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE).planItemDefinitionType(TimerEventListener.class.getSimpleName().toLowerCase()).count());
        
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list();
        assertEquals(3, tasks.size());
        for (Task task : tasks) {
            cmmnTaskService.complete(task.getId());
        }
        
       // assertCaseInstanceEnded(caseInstance);
    }
    /**
     * 
     * ${timerBean.calculateDate(startTime)}
     * exitCriterion
     * <timerEventListener id="sid-75C310A3-ACB9-4F80-866B-377BA2B3C18C">
          <timerExpression><![CDATA[${timerBean.calculateDate(startTime)}]]></timerExpression>
        </timerEventListener>
     * 时间激活则后面的实例结束
     */
    @Test
    public void testTimerWithBeanExpression() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testBean")
                .variable("startTime", startTime)
                .start();
        
     //   assertEquals(2L, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        assertEquals(2L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE).planItemDefinitionType(Stage.class.getSimpleName().toLowerCase()).count());
        assertEquals(2L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE).planItemDefinitionType(HumanTask.class.getSimpleName().toLowerCase()).count());
        
        setClockTo(new Date(startTime.getTime() + (2 * 60 * 60 * 1000)));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(dc, 5000L, 200L, true);
        
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("A", task.getName());
        cmmnTaskService.complete(task.getId());
       // assertCaseInstanceEnded(caseInstance);
    }
    /**
     * a完成 实例走到B
     * B走后流转到C
     * 
     * 激活定时器C完成
     * 
     */
    @Test
    public void testTimerStartTrigger() {
        // Completing the stage will be the start trigger for the timer.
        // The timer event will exit the whole plan model
        
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testStartTrigger").start();
        
        assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemDefinitionType(TimerEventListener.class.getSimpleName().toLowerCase()).count());
        
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("A", task.getName());
        cmmnTaskService.complete(task.getId());
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("B", task.getName());
        cmmnTaskService.complete(task.getId());
        task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("C", task.getName());
        
        assertEquals(1L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.AVAILABLE).planItemDefinitionType(TimerEventListener.class.getSimpleName().toLowerCase()).count());
        
        setClockTo(new Date(startTime.getTime() + (3 * 60 * 60 * 1000)));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(dc, 5000L, 200L, true);
       // assertCaseInstanceEnded(caseInstance);
    }
    /**
     * 当定时器触发完毕之后，所有的实例都结束了
     */
    @Test
    public void testExitNestedStageThroughTimer() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testExitNestedStageThroughTimer").start();
        assertEquals(3L, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemDefinitionType(PlanItemDefinitionType.STAGE).count());
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("The task", task.getName());
        
        setClockTo(new Date(startTime.getTime() + (5 * 60 * 60 * 1000)));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(dc, 10000L, 100L, true);
        assertEquals(0L, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
      //  assertCaseInstanceEnded(caseInstance);
    }
    
    @Test
    public void timerActivatesAndExitStages() {
        Date startTime = setClockFixedToCurrentTime();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("timerActivatesAndExitStages").start();
        
        List<Task> tasks = cmmnTaskService.createTaskQuery()
                .caseInstanceId(caseInstance.getId())
                .orderByTaskName().asc()
                .list();
        assertEquals(2, tasks.size());
        assertEquals("A", tasks.get(0).getName());
        assertEquals("B", tasks.get(1).getName());
        
        assertEquals(0L, cmmnManagementService.createTimerJobQuery().count());
        
        // Completing A activates the stage and the timer event listener
        cmmnTaskService.complete(tasks.get(0).getId());
        cmmnTaskService.complete(tasks.get(1).getId());
        
        // Timer event listener created a timer job
        assertEquals(1L, cmmnManagementService.createTimerJobQuery().count());
        tasks = cmmnTaskService.createTaskQuery()
                .caseInstanceId(caseInstance.getId())
                .orderByTaskName().asc()
                .list();
        assertEquals(3, tasks.size());
        assertEquals("Stage 1 task", tasks.get(0).getName());
        assertEquals("Stage 3 task 1", tasks.get(1).getName());
        assertEquals("Stage 3 task 2", tasks.get(2).getName());
        
        // Timer is set to 10 hours
        setClockTo(new Date(startTime.getTime() + (11 * 60 * 60 * 1000)));
        CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(dc, 10000L, 100L, true);
        
        tasks = cmmnTaskService.createTaskQuery()
                .caseInstanceId(caseInstance.getId())
                .orderByTaskName().asc()
                .list();
        assertEquals(2, tasks.size());
        assertEquals("Stage 1 task", tasks.get(0).getName());
        assertEquals("Stage 2 task", tasks.get(1).getName());
        
        for(Task task : tasks) {
            cmmnTaskService.complete(task.getId());
        }
    }
    
}