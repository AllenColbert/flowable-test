package com.shareniu.shareniu_flowable_study.cmmn.task;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;
/**
 * <itemControl>
          <manualActivationRule>
            <condition><![CDATA[333]]></condition>
          </manualActivationRule>
           
        </itemControl>
        
        
 * @author jz
 *
 */
public class ShareniuManualActivationRule {
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

	/**
	 * 1、部署并且启动
	 */
	@Test
	public void deployOneTaskCaseDefinition() {
		oneTaskCaseDeploymentId = cmmnRepositoryService.createDeployment()
				.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/task/shareniu-manualActivationRule.cmmn.xml")
				.deploy().getId();
		testBasicNonBlocking();
	}

	@Test
	public void testBasicNonBlocking() {
		Map<String, Object> variables = new HashMap<>();
		// repetitionCounter_p1

		variables.put("repetitionCounter_p1_execution", true);
		variables.put("repetitionCounter_p2_execution", true);
		variables.put("shareniu_execution", true);

		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
				.caseDefinitionKey("shareniu-manualActivationRule").variables(variables).start();
		System.out.println(caseInstance);

		System.out.println(cmmnRuntimeService.createCaseInstanceQuery().count());
		System.out.println(cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
		System.out.println(cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
	}
	
	/**
	 * 3、设置repetitionCounter_p2_execution变量为false ，然后再次完成任务 并观察效果
	 */
	@Test
	public void setVariables() {
		Map<String, Object> variables = new HashMap<>();
		variables.put("repetitionCounter_p1_execution", true);
		String caseInstanceId = "a3e55c9e-3a37-11e8-8481-ea6b958eb0f0";
		cmmnRuntimeService.setVariables(caseInstanceId, variables);
	}
	
	
	
	@Test
	public void enablePlanItemInstance() {
		String planItemInstanceId="1727eeca-3a39-11e8-87a8-ea6b958eb0f0";
		cmmnRuntimeService.enablePlanItemInstance(planItemInstanceId);
	}
	/**
	 * 4、完成 shareniu-repetition
	 */
	@Test
	public void triggerPlanItemInstance() {
		PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
				.caseInstanceId("1725a4d6-3a39-11e8-87a8-ea6b958eb0f0").planItemInstanceElementId("planItem1")

				.singleResult();
		System.out.println(planItemInstance);
		// Triggering the task should start the case instance (which is non-blocking ->
		// directly go to task two)
		cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
	}
}
