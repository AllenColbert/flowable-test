package com.shareniu.shareniu_flowable_study.cmmn.ch2;

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
import org.junit.Before;
import org.junit.Test;

public class CaseTaskTest {
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
				.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/ch2/oneTaskCase.cmmn").deploy()
				.getId();
		
		
	}

	@Test
	public void testBasicNonBlocking() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase")
				.start();
		System.out.println(caseInstance);
		
		System.out.println(cmmnRuntimeService.createCaseInstanceQuery().count());
		System.out.println(cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
		System.out.println(cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
	}
	@Test
	public void createPlanItemInstanceQuery() {
		  PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
	                .caseInstanceId("7a3975b0-3990-11e8-85fc-ea6b958eb0f0")
	                .planItemInstanceState(PlanItemInstanceState.ACTIVE)
	                .singleResult();
		  System.out.println(planItemInstance);
		  
	        
	        // Triggering the task should start the case instance (which is non-blocking -> directly go to task two)
	        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
	        
	        assertEquals(2, cmmnRuntimeService.createCaseInstanceQuery().count());
	        assertEquals(0, cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
	        List<PlanItemInstance> planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery()
	                .planItemInstanceState(PlanItemInstanceState.ACTIVE)
	                .orderByName().asc()
	                .list();
	        assertEquals(2, planItemInstances.size());
	        assertEquals("Task Two", planItemInstances.get(0).getName());
	        assertEquals("The Task", planItemInstances.get(1).getName());
		  
		  
		  
	}
	// // Triggering the task should start the case instance (which is non-blocking -> directly go to task two)
	@Test
	public void triggerPlanItemInstance() {
		 cmmnRuntimeService.triggerPlanItemInstance("eabd64ce-398e-11e8-9b26-ea6b958eb0f0");
		
	}
	
	

}
