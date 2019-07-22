package com.shareniu.shareniu_flowable_study.cmmn.tenant;

import java.io.InputStream;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnManagementService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;
/**
 * CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase")
				.tenantId("test-tenant")
				.start();
 *如果部署的时候指定了租户ID，则启动的时候也需要指定
 *
 */
public class MultiTenantTest {
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
				.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/tenant/one-human-task-model.cmmn")
			//	.tenantId("test-tenant")
				.deploy()
				.getId();
	}
//oneTaskCase
	@Test
	public void testBasicNonBlocking() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("oneTaskCase")
				.tenantId("test-tenant")
				.start();
		System.out.println(caseInstance);
		
		System.out.println(cmmnRuntimeService.createCaseInstanceQuery().caseInstanceWithoutTenantId().count());
		System.out.println(cmmnHistoryService.createHistoricCaseInstanceQuery().finished().count());
		System.out.println(cmmnHistoryService.createHistoricCaseInstanceQuery().unfinished().count());
	}
	
}
