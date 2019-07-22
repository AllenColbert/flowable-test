package com.shareniu.shareniu_flowable_study.cmmn.ch1;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnManagementService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.repository.CmmnDeployment;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.junit.Before;
import org.junit.Test;

public class CmmnProcesengineTest {
	CmmnEngine ce;
	CmmnHistoryService cmmnHistoryService;
	CmmnManagementService cmmnManagementService;
	CmmnRepositoryService cmmnRepositoryService;
	CmmnRuntimeService cmmnRuntimeService;
	CmmnTaskService cmmnTaskService;
	  private String deploymentId1;
	@Before
	public void init() {
		InputStream in = CmmnProcesengineTest.class.getClassLoader()
				.getResourceAsStream("com/shareniu/shareniu_flowable_study/cmmn/flowable.cmmn.cfg.xml");
		CmmnEngineConfiguration dc = CmmnEngineConfiguration.createCmmnEngineConfigurationFromInputStream(in);
		ce = dc.buildCmmnEngine();
		cmmnHistoryService = ce.getCmmnHistoryService();
		cmmnManagementService = ce.getCmmnManagementService();
		cmmnRepositoryService = ce.getCmmnRepositoryService();
		cmmnRuntimeService = ce.getCmmnRuntimeService();
		cmmnTaskService = ce.getCmmnTaskService();
		this.deploymentId1 = cmmnRepositoryService.createDeployment()
                .addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/ch1/simple-case.cmmn")
                .deploy()
                .getId();
	}

	
	
	@Test
    public void testUpdateCategory() {
        CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId1).singleResult();
        System.out.println(caseDefinition.getCategory());
        
        cmmnRepositoryService.setCaseDefinitionCategory(caseDefinition.getId(), "testCategory");
        caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId1).singleResult();
        System.out.println(caseDefinition.getCategory());
        
        caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId1).caseDefinitionCategory("testCategory").singleResult();
       System.out.println(caseDefinition);
    }
	
	
	
	
	
	
	/**
	 * 1、创建表
	 */
	@Test
	public void createTable() {
	}
	/**
	 * 2、获取表的数量
	 */
	@Test
	public void getTableCounts() {

		Map<String, Long> tableCounts = cmmnManagementService.getTableCounts();
		System.out.println(tableCounts.size());
		Set<Entry<String, Long>> entrySet = tableCounts.entrySet();
		for (Entry<String, Long> entry : entrySet) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
	}
	
	/**
	 * 3、部署模型 ACT_CMMN_CASEDEF ACT_CMMN_DEPLOYMENT ACT_CMMN_DEPLOYMENT_RESOURCE 三张表 差生了数据
	 */
	@Test
	public void deploy() {
		
		CmmnDeployment deploy = cmmnRepositoryService
		.createDeployment()
		.addClasspathResource("com/shareniu/shareniu_flowable_study/cmmn/ch1/demo1.cmmn.xml").deploy();
		System.out.println(deploy);
	}

	
	/**
	 * 4、启动实例  ACT_CMMN_HI_CASE_INST ACT_RU_VARIABLE ACT_HI_VARINST  ACT_CMMN_RU_PLAN_ITEM_INST
	 * ACT_CMMN_RU_CASE_INST
	 */
	@Test
	public void startCaseInstance() {
		
		String caseDefinitionKey="hello";
		Map<String, Object> variables=new HashMap<>();
		variables.put("shareniu-hello", "shareniu-hello");
		CaseInstance ci = cmmnRuntimeService.createCaseInstanceBuilder()
		.caseDefinitionKey(caseDefinitionKey)
		.businessKey("1")
		.variables(variables)
		.start();
		System.out.println(ci);
	}
	
	
	@Test
	public void startPlanItemInstance() {
		
	    String planItemInstanceId="bcb7fa09-3984-11e8-ab4b-ea6b958eb0f0";
		cmmnRuntimeService
	    .startPlanItemInstance(planItemInstanceId);
	}
}
