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
package com.shareniu.shareniu_flowable_study.network.requiredrule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import org.flowable.cmmn.engine.impl.persistence.entity.PlanItemInstanceContainer;
import org.flowable.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import org.flowable.cmmn.engine.impl.repository.CaseDefinitionUtil;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.engine.common.api.FlowableIllegalArgumentException;
import org.flowable.engine.common.impl.util.CollectionUtil;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;

/**
 * @author Joram Barrez
 */
public class RequiredRuleTest  {

	
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
	//.addClasspathResource("com/shareniu/shareniu_flowable_study/network/requiredrule/RequiredRuleTest.testCompleteStageManually.cmmn")
	//.addClasspathResource("com/shareniu/shareniu_flowable_study/network/requiredrule/RequiredRuleTest.testNonAutoCompleteStageManualCompleteable.cmmn")
	.addClasspathResource("com/shareniu/shareniu_flowable_study/network/requiredrule/RequiredRuleTest.testCompleteCaseInstanceManually.cmmn")
				.deploy()
				.getId();
	}
	
	
	/**
	 * <case id="testOneRequiredHumanTask" name="testOneRequiredHumanTask" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel" autoComplete="true">
      <planItem id="planItem1" name="Required task" definitionRef="sid-1E0DFB07-A127-485B-9EEF-80021D62E97D">
        <entryCriterion id="sid-3FE93E64-1B4C-43F9-AE64-81A9800D5309" sentryRef="sentry2"></entryCriterion>
      </planItem>
      <planItem id="planItem2" name="Non-required task" definitionRef="sid-DFDDEFD6-306E-4616-81BE-19402F5BCF05">
        <entryCriterion id="sid-600B7C68-48BB-42BE-9D95-2451EC64FBBE" sentryRef="sentry1"></entryCriterion>
      </planItem>
      <sentry id="sentry2">
        <ifPart>
          <condition><![CDATA[${caseInstance.getVariable('required') != null && required}]]></condition>
        </ifPart>
      </sentry>
      <sentry id="sentry1">
        <ifPart>
          <condition><![CDATA[${caseInstance.getVariable('nonRequired') != null && nonRequired}]]></condition>
        </ifPart>
      </sentry>
      <humanTask id="sid-1E0DFB07-A127-485B-9EEF-80021D62E97D" name="Required task"></humanTask>
      <humanTask id="sid-DFDDEFD6-306E-4616-81BE-19402F5BCF05" name="Non-required task"></humanTask>
    </casePlanModel>
  </case>
  1、只设置required 则planItem1是激活状态，planItem2没有被激活  // The required task is made active, the non-required not.
  2、因为配置的是autoComplete="true"  所以planItem1完成则所有的实例都结束。
  3、同时激活planItem1和planItem2，当只完成其中一个的时候，实例不会自动完成，因此还有一个没有完成。
	 */
	
    @Test
    public void testOneRequiredHumanTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testOneRequiredHumanTask")
                .variable("required", true)
                .start();
        
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseInstanceId(caseInstance.getId()).orderByName().asc().list();
        assertEquals(2, planItemInstances.size());
        assertEquals("Non-required task", planItemInstances.get(0).getName());
        assertEquals(PlanItemInstanceState.AVAILABLE, planItemInstances.get(0).getState());
        assertEquals("Required task", planItemInstances.get(1).getName());
        assertEquals(PlanItemInstanceState.ACTIVE, planItemInstances.get(1).getState());
        
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("Required task", task.getName());
        
        // Completing the task should autocomplete the plan model, as the plan model is autoComplete enabled
        cmmnTaskService.complete(task.getId());
      //  assertCaseInstanceEnded(caseInstance);
        
        // Both required and non-required task are made active.
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testOneRequiredHumanTask")
                .variable("required", true)
                .variable("nonRequired", true)
                .start();
        
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseInstanceId(caseInstance.getId()).orderByName().asc().list();
        assertEquals(2, planItemInstances.size());
        assertEquals("Non-required task", planItemInstances.get(0).getName());
        assertEquals(PlanItemInstanceState.ACTIVE, planItemInstances.get(0).getState());
        assertEquals("Required task", planItemInstances.get(1).getName());
        assertEquals(PlanItemInstanceState.ACTIVE, planItemInstances.get(1).getState());
        
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals(2, tasks.size());
        assertEquals("Non-required task", tasks.get(0).getName());
        assertEquals("Required task", tasks.get(1).getName());
        
        // Completing the required task should not autocomplete the plan model 
        cmmnTaskService.complete(tasks.get(1).getId());
        
        cmmnTaskService.complete(tasks.get(0).getId());
    }
    /**
     *  <casePlanModel id="casePlanModel">
      <planItem id="planItem3" name="The Stage" definitionRef="sid-17705AF8-4146-42B7-B3BF-8C444CB1291B"></planItem>
      <planItem id="planItem4" name="Other task" definitionRef="sid-6FADCDFD-9F35-4A72-9387-F7FE4B9196E8"></planItem>
      <stage id="sid-17705AF8-4146-42B7-B3BF-8C444CB1291B" name="The Stage" autoComplete="true">
        <planItem id="planItem1" name="Required task" definitionRef="sid-1E0DFB07-A127-485B-9EEF-80021D62E97D">
          <entryCriterion id="sid-3FE93E64-1B4C-43F9-AE64-81A9800D5309" sentryRef="sentry1"></entryCriterion>
        </planItem>
        <planItem id="planItem2" name="Non-required task" definitionRef="sid-DFDDEFD6-306E-4616-81BE-19402F5BCF05">
          <entryCriterion id="sid-600B7C68-48BB-42BE-9D95-2451EC64FBBE" sentryRef="sentry2"></entryCriterion>
        </planItem>
        <sentry id="sentry1">
          <ifPart>
            <condition><![CDATA[${caseInstance.getVariable('required') != null && required}]]></condition>
          </ifPart>
        </sentry>
        <sentry id="sentry2">
          <ifPart>
            <condition><![CDATA[${caseInstance.getVariable('nonRequired') != null && nonRequired}]]></condition>
          </ifPart>
        </sentry>
        <humanTask id="sid-1E0DFB07-A127-485B-9EEF-80021D62E97D" name="Required task"></humanTask>
        <humanTask id="sid-DFDDEFD6-306E-4616-81BE-19402F5BCF05" name="Non-required task"></humanTask>
      </stage>
      <humanTask id="sid-6FADCDFD-9F35-4A72-9387-F7FE4B9196E8" name="Other task"></humanTask>
    </casePlanModel>
    1、 流程有两个节点，如果一个完成但是另一个没有被激活，但是设置了 autoComplete="true"，另外一个没有被激活的也会自动// Completing the task should autocomplete the stage
    2、同时激活planItem1和planItem2，当只完成其中一个的时候，实例不会自动完成，因此还有一个没有完成。如果都完成则stage也完成。
     */
    
    
    @Test
    public void testOneRequiredHumanTaskInStage() {
        
        // The required task is made active, the non-required not.
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testOneRequiredHumanTaskInStage")
                .variable("required", true)
                .start();
        
        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseInstanceId(caseInstance.getId()).orderByName().asc().list();
        assertEquals(4, planItemInstances.size());
        assertEquals("Non-required task", planItemInstances.get(0).getName());
        assertEquals(PlanItemInstanceState.AVAILABLE, planItemInstances.get(0).getState());
        assertEquals("Other task", planItemInstances.get(1).getName());
        assertEquals(PlanItemInstanceState.ACTIVE, planItemInstances.get(1).getState());
        assertEquals("Required task", planItemInstances.get(2).getName());
        assertEquals(PlanItemInstanceState.ACTIVE, planItemInstances.get(2).getState());
        assertEquals("The Stage", planItemInstances.get(3).getName());
        assertEquals(PlanItemInstanceState.ACTIVE, planItemInstances.get(3).getState());
        
        Task task = cmmnTaskService.createTaskQuery().taskName("Required task").singleResult();
        assertEquals("Required task", task.getName());
        
        // Completing the task should autocomplete the stage
        cmmnTaskService.complete(task.getId());
       // assertCaseInstanceNotEnded(caseInstance);
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).count());
        
        cmmnTaskService.complete(cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult().getId());
      //  assertCaseInstanceEnded(caseInstance);
        
        // Both required and non-required task are made active.
        caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testOneRequiredHumanTaskInStage")
                .variable("required", true)
                .variable("nonRequired", true)
                .start();
        
        planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseInstanceId(caseInstance.getId()).orderByName().asc().list();
        assertEquals(4, planItemInstances.size());
        assertEquals("Non-required task", planItemInstances.get(0).getName());
        assertEquals(PlanItemInstanceState.ACTIVE, planItemInstances.get(0).getState());
        assertEquals("Other task", planItemInstances.get(1).getName());
        assertEquals(PlanItemInstanceState.ACTIVE, planItemInstances.get(1).getState());
        assertEquals("Required task", planItemInstances.get(2).getName());
        assertEquals(PlanItemInstanceState.ACTIVE, planItemInstances.get(2).getState());
        assertEquals("The Stage", planItemInstances.get(3).getName());
        assertEquals(PlanItemInstanceState.ACTIVE, planItemInstances.get(3).getState());
        
        Task otherTask = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).taskName("Other task").singleResult();
        cmmnTaskService.complete(otherTask.getId());
        
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals(2, tasks.size());
        assertEquals("Non-required task", tasks.get(0).getName());
        assertEquals("Required task", tasks.get(1).getName());
        
        cmmnTaskService.complete(tasks.get(1).getId());
       // assertCaseInstanceNotEnded(caseInstance);
        
        cmmnTaskService.complete(tasks.get(0).getId());
        //assertCaseInstanceEnded(caseInstance);
        
    }
    
    
    
    
    
    
    /**
     *  <casePlanModel id="casePlanModel">
      <planItem id="planItem3" name="The Stage" definitionRef="sid-17705AF8-4146-42B7-B3BF-8C444CB1291B"></planItem>
      <stage id="sid-17705AF8-4146-42B7-B3BF-8C444CB1291B" name="The Stage" autoComplete="false">
        <planItem id="planItem1" name="Required task" definitionRef="sid-1E0DFB07-A127-485B-9EEF-80021D62E97D">
          <entryCriterion id="sid-3FE93E64-1B4C-43F9-AE64-81A9800D5309" sentryRef="sentry1"></entryCriterion>
        </planItem>
        <planItem id="planItem2" name="Non-required task" definitionRef="sid-DFDDEFD6-306E-4616-81BE-19402F5BCF05">
          <entryCriterion id="sid-600B7C68-48BB-42BE-9D95-2451EC64FBBE" sentryRef="sentry2"></entryCriterion>
        </planItem>
        <sentry id="sentry1">
          <ifPart>
            <condition><![CDATA[${caseInstance.getVariable('required') != null && required}]]></condition>
          </ifPart>
        </sentry>
        <sentry id="sentry2">
          <ifPart>
            <condition><![CDATA[${caseInstance.getVariable('nonRequired') != null && nonRequired}]]></condition>
          </ifPart>
        </sentry>
        <humanTask id="sid-1E0DFB07-A127-485B-9EEF-80021D62E97D" name="Required task"></humanTask>
        <humanTask id="sid-DFDDEFD6-306E-4616-81BE-19402F5BCF05" name="Non-required task"></humanTask>
      </stage>
    </casePlanModel>
    1、      planItemInstanceEntity.setCompleteable(false); an active child = stage cannot be completed anymore
    
  stage是active  task一个是active  一个是available  stagePlanItemInstance.isCompleteable()=false
    2、完成一个任务，只有一个task状态是available stagePlanItemInstance.isCompleteable()=true
    3、完成一个任务，再次激活之后只有一个任务并且状态为active。 stagePlanItemInstance.isCompleteable()=false
    
     */
    @Test
    public void testNonAutoCompleteStageManualCompleteable() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testNonAutoCompleteStageManualCompleteable")
                .variable("required", true)
                .start();
        
        PlanItemInstance stagePlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.STAGE).singleResult();
        assertEquals(PlanItemInstanceState.ACTIVE, stagePlanItemInstance.getState());
        assertFalse(stagePlanItemInstance.isCompleteable());
        
        // Completing the one task should mark the stage as completeable 
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("Required task", task.getName());
        cmmnTaskService.complete(task.getId());
        
        stagePlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceId(stagePlanItemInstance.getId()).singleResult();
        assertEquals(PlanItemInstanceState.ACTIVE, stagePlanItemInstance.getState());
        assertTrue(stagePlanItemInstance.isCompleteable());
        assertEquals(0, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        
        // Making the other task active, should disable the completeable flag again
        cmmnRuntimeService.setVariables(caseInstance.getId(), CollectionUtil.singletonMap("nonRequired", true));
        assertEquals(1, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());
        stagePlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceId(stagePlanItemInstance.getId()).singleResult();
        assertEquals(PlanItemInstanceState.ACTIVE, stagePlanItemInstance.getState());
        assertFalse(stagePlanItemInstance.isCompleteable());
    }
    /**
     * <casePlanModel id="casePlanModel">
      <planItem id="planItem3" name="The Stage" definitionRef="sid-17705AF8-4146-42B7-B3BF-8C444CB1291B"></planItem>
      <stage id="sid-17705AF8-4146-42B7-B3BF-8C444CB1291B" name="The Stage" autoComplete="false">
        <planItem id="planItem1" name="Required task" definitionRef="sid-1E0DFB07-A127-485B-9EEF-80021D62E97D">
          <entryCriterion id="sid-3FE93E64-1B4C-43F9-AE64-81A9800D5309" sentryRef="sentry1"></entryCriterion>
        </planItem>
        <planItem id="planItem2" name="Non-required task" definitionRef="sid-DFDDEFD6-306E-4616-81BE-19402F5BCF05">
          <entryCriterion id="sid-600B7C68-48BB-42BE-9D95-2451EC64FBBE" sentryRef="sentry2"></entryCriterion>
        </planItem>
        <sentry id="sentry1">
          <ifPart>
            <condition><![CDATA[${caseInstance.getVariable('required') != null && required}]]></condition>
          </ifPart>
        </sentry>
        <sentry id="sentry2">
          <ifPart>
            <condition><![CDATA[${caseInstance.getVariable('nonRequired') != null && nonRequired}]]></condition>
          </ifPart>
        </sentry>
        <humanTask id="sid-1E0DFB07-A127-485B-9EEF-80021D62E97D" name="Required task"></humanTask>
        <humanTask id="sid-DFDDEFD6-306E-4616-81BE-19402F5BCF05" name="Non-required task"></humanTask>
      </stage>
    </casePlanModel>
    
    1、 task一个是active  一个是available  stagePlanItemInstance.isCompleteable()=false
    
    
    
    if (!planItemInstanceEntity.isCompleteable()) {
            throw new FlowableIllegalArgumentException("Can only complete a stage plan item instance that is marked as completeable (there might still be active plan item instance).");
        }
    2、stagePlanItemInstance.isCompleteable()=true
    
          cmmnRuntimeService.completeStagePlanItemInstance(stagePlanItemInstance.getId());
     */
    @Test
    public void testCompleteStageManually() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testNonAutoCompleteStageManualCompleteable")
                .variable("required", true)
                .start();
        
        PlanItemInstance stagePlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemDefinitionType(PlanItemDefinitionType.STAGE).singleResult();
        assertEquals(PlanItemInstanceState.ACTIVE, stagePlanItemInstance.getState());
        assertFalse(stagePlanItemInstance.isCompleteable());
        
        try {
            cmmnRuntimeService.completeStagePlanItemInstance(stagePlanItemInstance.getId());
            fail();
        } catch (FlowableIllegalArgumentException e) {
            assertEquals("Can only complete a stage plan item instance that is marked as completeable (there might still be active plan item instance).", e.getMessage());
        }
        
        // Completing the one task should mark the stage as completeable 
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertEquals("Required task", task.getName());
        cmmnTaskService.complete(task.getId());
        
        stagePlanItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceId(stagePlanItemInstance.getId()).singleResult();
        assertEquals(PlanItemInstanceState.ACTIVE, stagePlanItemInstance.getState());
        assertTrue(stagePlanItemInstance.isCompleteable());
        assertEquals(0, cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).count());

        assertNotNull(cmmnRuntimeService.createPlanItemInstanceQuery().planItemCompleteable().singleResult());
        cmmnRuntimeService.completeStagePlanItemInstance(stagePlanItemInstance.getId());
    }
    /**
     *  <casePlanModel id="casePlanModel">
      <planItem id="planItem1" name="Other task" definitionRef="sid-6FADCDFD-9F35-4A72-9387-F7FE4B9196E8"></planItem>
      <planItem id="planItem2" name="Required task" definitionRef="sid-1E0DFB07-A127-485B-9EEF-80021D62E97D">
        <entryCriterion id="sid-3FE93E64-1B4C-43F9-AE64-81A9800D5309" sentryRef="sentry1"></entryCriterion>
      </planItem>
      <planItem id="planItem3" name="Non-required task" definitionRef="sid-DFDDEFD6-306E-4616-81BE-19402F5BCF05">
        <entryCriterion id="sid-600B7C68-48BB-42BE-9D95-2451EC64FBBE" sentryRef="sentry2"></entryCriterion>
      </planItem>
      <sentry id="sentry1">
        <ifPart>
          <condition><![CDATA[${caseInstance.getVariable('required') != null && required}]]></condition>
        </ifPart>
      </sentry>
      <sentry id="sentry2">
        <ifPart>
          <condition><![CDATA[${caseInstance.getVariable('nonRequired') != null && nonRequired}]]></condition>
        </ifPart>
      </sentry>
      <humanTask id="sid-6FADCDFD-9F35-4A72-9387-F7FE4B9196E8" name="Other task"></humanTask>
      <humanTask id="sid-1E0DFB07-A127-485B-9EEF-80021D62E97D" name="Required task"></humanTask>
      <humanTask id="sid-DFDDEFD6-306E-4616-81BE-19402F5BCF05" name="Non-required task"></humanTask>
    </casePlanModel>
    
    
     protected boolean isEndStateReachedForAllRequiredChildPlanItems(PlanItemInstanceContainer planItemInstanceContainer) {
        if (planItemInstanceContainer.getChildPlanItemInstances() != null) {
            for (PlanItemInstanceEntity childPlanItemInstance : planItemInstanceContainer.getChildPlanItemInstances()) {
                if (PlanItemInstanceState.END_STATES.contains(childPlanItemInstance.getState())) {
                    continue;
                }
                if (isRequiredPlanItemInstance(childPlanItemInstance)) {
                    return false;
                }
                return isEndStateReachedForAllChildPlanItems(childPlanItemInstance);
            }
        }
        return true;
    }
    protected boolean isPlanModelComplete() {
        boolean allRequiredChildrenInEndState = isEndStateReachedForAllRequiredChildPlanItems(caseInstanceEntity);
        if (allRequiredChildrenInEndState) {
            caseInstanceEntity.setCompleteable(true);
        }
        
        boolean isAutoComplete = CaseDefinitionUtil.getCase(caseInstanceEntity.getCaseDefinitionId()).getPlanModel().isAutoComplete();

        if (caseInstanceEntity.isCompleteable()) {
            if (isAutoComplete) {
                return true;
            } else {
                return isAvailableChildPlanCompletionNeutralOrNotActive(caseInstanceEntity);
            }
        } else {
            return false;
        }
    }
    
     */
    @Test
    public void testCompleteCaseInstanceManually() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("testCompleteCaseInstanceManually")
                .variable("required", true)
                .start();
        
        assertFalse(caseInstance.isCompleteable());
        assertEquals(2, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateActive().count());
        assertEquals(1, cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId()).planItemInstanceStateAvailable().count());
        
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals("Other task", tasks.get(0).getName());
        assertEquals("Required task", tasks.get(1).getName());
        
        // Case should not be completeale
        try {
            cmmnRuntimeService.completeCaseInstance(caseInstance.getId());
            fail();
        } catch (FlowableIllegalArgumentException e) {
            assertEquals("Can only complete a case instance which is marked as completeable. Check if there are active plan item instances.", e.getMessage());
        }
        
        // Completing both tasks should not auto complete the case, as the plan model is not auto complete
        for (Task task : tasks) {
            cmmnTaskService.complete(task.getId());
        }
        
        caseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertTrue(caseInstance.isCompleteable());
        cmmnRuntimeService.completeCaseInstance(caseInstance.getId());
        //assertCaseInstanceEnded(caseInstance);
    }
    /**
     * 
     *  <case id="myCase" name="myCase" flowable:initiatorVariableName="initiator">
    <casePlanModel id="casePlanModel">
      <planItem id="planItem6" definitionRef="sid-83FE6135-3E99-444A-9473-BF45C72FFED8">
        <entryCriterion id="sid-5CF32124-8373-4163-987B-CA1A78A3ED02" sentryRef="sentry6"></entryCriterion>
      </planItem>
      <planItem id="planItem14" definitionRef="sid-7C3E8D8A-B777-407D-9E78-DC5A43D56D98">
        <entryCriterion id="sid-6376E008-01B4-41D0-AC1F-AABF210EBF0F" sentryRef="sentry7"></entryCriterion>
        <exitCriterion id="sid-D142FB9F-5B61-4BF2-B281-4F296709AEA9" sentryRef="sentry8"></exitCriterion>
      </planItem>
      <planItem id="planItem15" name="A" definitionRef="sid-5EF68E1E-ADF3-4C84-8739-1DC03E1F51E7"></planItem>
      <sentry id="sentry6">
        <planItemOnPart id="sentryOnPart4" sourceRef="planItem15">
          <standardEvent>complete</standardEvent>
        </planItemOnPart>
      </sentry>
      <sentry id="sentry7">
        <planItemOnPart id="sentryOnPart6" sourceRef="planItem6">
          <standardEvent>complete</standardEvent>
        </planItemOnPart>
      </sentry>
      <sentry id="sentry8">
        <planItemOnPart id="sentryOnPart7" sourceRef="planItem12">
          <standardEvent>complete</standardEvent>
        </planItemOnPart>
      </sentry>
      <stage id="sid-83FE6135-3E99-444A-9473-BF45C72FFED8" autoComplete="true">
        <planItem id="planItem1" name="B" definitionRef="sid-035E44C4-AB1A-44B9-A35F-77F94560D801">
          <itemControl>
            <manualActivationRule></manualActivationRule>
          </itemControl>
        </planItem>
        <planItem id="planItem2" name="M1" definitionRef="sid-387E6103-BF29-4397-86E4-BDF9ED5528DC">
          <entryCriterion id="sid-082E2F93-347C-4BB5-BD5B-060ACAA7AAB9" sentryRef="sentry1"></entryCriterion>
        </planItem>
        <planItem id="planItem3" name="C" definitionRef="sid-F7B7876C-1981-4AF6-A563-8723F0188E92">
          <entryCriterion id="sid-00A2DF51-ABEF-494B-ACD4-92DEA3DF70B3" sentryRef="sentry2"></entryCriterion>
        </planItem>
        <planItem id="planItem4" name="D" definitionRef="sid-7B16BD5D-5B97-49BA-8E7B-98FB387794E6">
          <itemControl>
            <requiredRule>
              <condition><![CDATA[${dRequired}]]></condition>
            </requiredRule>
          </itemControl>
        </planItem>
        <planItem id="planItem5" name="S1" definitionRef="sid-DE55F03A-6FF7-4C6F-8A5A-11F1BF815E0B">
          <entryCriterion id="sid-3C4CEEF8-B31C-48B5-8214-1F6A326A2267" sentryRef="sentry3"></entryCriterion>
        </planItem>
        <sentry id="sentry1">
          <planItemOnPart id="sentryOnPart1" sourceRef="planItem1">
            <standardEvent>complete</standardEvent>
          </planItemOnPart>
        </sentry>
        <sentry id="sentry2">
          <planItemOnPart id="sentryOnPart2" sourceRef="planItem1">
            <standardEvent>complete</standardEvent>
          </planItemOnPart>
        </sentry>
        <sentry id="sentry3">
          <planItemOnPart id="sentryOnPart3" sourceRef="planItem3">
            <standardEvent>complete</standardEvent>
          </planItemOnPart>
        </sentry>
        <humanTask id="sid-035E44C4-AB1A-44B9-A35F-77F94560D801" name="B"></humanTask>
        <milestone id="sid-387E6103-BF29-4397-86E4-BDF9ED5528DC" name="M1"></milestone>
        <humanTask id="sid-F7B7876C-1981-4AF6-A563-8723F0188E92" name="C"></humanTask>
        <humanTask id="sid-7B16BD5D-5B97-49BA-8E7B-98FB387794E6" name="D"></humanTask>
        <task id="sid-DE55F03A-6FF7-4C6F-8A5A-11F1BF815E0B" name="S1" isBlocking="false" flowable:type="java" flowable:expression="#{!booleanVar}" flowable:resultVariableName="booleanVar"></task>
      </stage>
      <stage id="sid-7C3E8D8A-B777-407D-9E78-DC5A43D56D98" autoComplete="true">
        <planItem id="planItem9" definitionRef="sid-08D9CB24-A2A0-4135-A116-60DE6FFC964B">
          <itemControl>
            <requiredRule></requiredRule>
          </itemControl>
          <entryCriterion id="sid-999866E5-C05C-4C24-B016-772F16185F2B" sentryRef="sentry5"></entryCriterion>
        </planItem>
        <planItem id="planItem10" name="E" definitionRef="sid-A921F004-344F-43A0-8999-F97467BB8C5F">
          <itemControl>
            <manualActivationRule></manualActivationRule>
          </itemControl>
        </planItem>
        <planItem id="planItem11" name="S2" definitionRef="sid-EE6B9F79-9797-48A2-91E0-ECB9716297EE">
          <entryCriterion id="sid-23A38CFB-33F6-446B-BE35-D64DB7639185" sentryRef="sentry4"></entryCriterion>
        </planItem>
        <planItem id="planItem12" name="F" definitionRef="sid-82414956-7F45-4EAA-953D-2EA420CCAE91">
          <itemControl>
            <manualActivationRule></manualActivationRule>
          </itemControl>
        </planItem>
        <planItem id="planItem13" name="M2" definitionRef="sid-CC24997F-0C15-47F7-B596-89C9E470F38F"></planItem>
        <sentry id="sentry5">
          <ifPart>
            <condition><![CDATA[${caseInstance.getVariable('enableSubStage') != null && enableSubStage}]]></condition>
          </ifPart>
        </sentry>
        <sentry id="sentry4">
          <planItemOnPart id="sentryOnPart5" sourceRef="planItem10">
            <standardEvent>complete</standardEvent>
          </planItemOnPart>
        </sentry>
        <stage id="sid-08D9CB24-A2A0-4135-A116-60DE6FFC964B">
          <planItem id="planItem7" name="G" definitionRef="sid-EAFAEB9A-7C1A-42D5-A237-DD9ABF0F935C"></planItem>
          <planItem id="planItem8" name="M3" definitionRef="sid-38B5177C-06CA-4EFC-BB9A-6D56908E55E3"></planItem>
          <humanTask id="sid-EAFAEB9A-7C1A-42D5-A237-DD9ABF0F935C" name="G"></humanTask>
          <milestone id="sid-38B5177C-06CA-4EFC-BB9A-6D56908E55E3" name="M3"></milestone>
        </stage>
        <humanTask id="sid-A921F004-344F-43A0-8999-F97467BB8C5F" name="E"></humanTask>
        <task id="sid-EE6B9F79-9797-48A2-91E0-ECB9716297EE" name="S2" isBlocking="false" flowable:type="java" flowable:expression="${caseInstance.setVariable('enableSubStage', true)}"></task>
        <humanTask id="sid-82414956-7F45-4EAA-953D-2EA420CCAE91" name="F"></humanTask>
        <milestone id="sid-CC24997F-0C15-47F7-B596-89C9E470F38F" name="M2"></milestone>
      </stage>
      <humanTask id="sid-5EF68E1E-ADF3-4C84-8739-1DC03E1F51E7" name="A"></humanTask>
    </casePlanModel>
    
    
     */
    @Test
    public void testComplexCase() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("myCase")
                .variable("dRequired", false)
                .variable("enableSubStage", true)
                .start();
        
        Task taskA = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertNotNull(taskA);
        cmmnTaskService.complete(taskA.getId());
        
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals(1, tasks.size());
        assertEquals("D", tasks.get(0).getName());
        
        // D is required. So completing D will auto complete the stage
        cmmnTaskService.complete(tasks.get(0).getId());
        assertEquals(2, cmmnRuntimeService.createMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).count()); // M1 is never reached. M2 and M3 are
        
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals(1, tasks.size());
        assertEquals("G", tasks.get(0).getName());
        
        // G is the only required task. Completing it should complete the stage and case instance
        cmmnTaskService.complete(tasks.get(0).getId());
      //  assertCaseInstanceEnded(caseInstance);
    }
    
    @Test
    @CmmnDeployment
    public void testComplexCase02() {
        
        // Same as testComplexCase, but now B and E are manually enabled
        
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                .caseDefinitionKey("myCase")
                .variable("dRequired", false)
                .variable("enableSubStage", false)
                .variable("booleanVar", true)
                .variable("subStageRequired", false)
                .start();
        
        Task taskA = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertNotNull(taskA);
        cmmnTaskService.complete(taskA.getId());
        
        PlanItemInstance planItemInstanceB = cmmnRuntimeService.createPlanItemInstanceQuery().planItemInstanceName("B").singleResult();
        assertEquals(PlanItemInstanceState.ENABLED, planItemInstanceB.getState());
        cmmnRuntimeService.startPlanItemInstance(planItemInstanceB.getId());
        
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals(2, tasks.size());
        assertEquals("B", tasks.get(0).getName());
        assertEquals("D", tasks.get(1).getName());
        
        // D is required. But B is still active
        cmmnTaskService.complete(tasks.get(1).getId());
        assertEquals(0, cmmnRuntimeService.createMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).count());
        cmmnTaskService.complete(tasks.get(0).getId());
        assertEquals(1, cmmnRuntimeService.createMilestoneInstanceQuery().milestoneInstanceCaseInstanceId(caseInstance.getId()).count());
        
        tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).orderByTaskName().asc().list();
        assertEquals(1, tasks.size());
        assertEquals("C", tasks.get(0).getName());
        
        // There are no active tasks in the second stage (as the nested stage is not active and not required).
        // Stage should autocomplete immediately after task completion
        cmmnTaskService.complete(tasks.get(0).getId());
    }
      
}
