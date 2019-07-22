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
package com.shareniu.shareniu_flowable_study.cmmn.task.serviceTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnManagementService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.delegate.DelegatePlanItemInstance;
import org.flowable.cmmn.api.delegate.PlanItemJavaDelegate;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstanceState;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;

/**
 * 
 * flowable:expression="${testBean.invoke(planItemInstance)}
 * 
 * 必须在bean中定义 如下：
 * 	<bean id="testBean" class="com.shareniu.shareniu_flowable_study.cmmn.task.serviceTask.TestBean"></bean>
 * 使用变量方式没法获取到  
 * 
 * flowable:class="com.shareniu.shareniu_flowable_study.cmmn.task.serviceTask.TestJavaDelegate" 中的类
 * 需要实现PlanItemJavaDelegate接口
 * 
 * 
 * flowable:expression="${testBean.invoke(planItemInstance)}" flowable:resultVariableName="beanResponse" 
 * 则invoke方法的返回值存储到beanResponse变量中。
 * 
 * 
 *  flowable:delegateExpression="${testDelegateFieldsBean}"
 *  需要实现
 *  public class TestJavaDelegate implements PlanItemJavaDelegate {

    @Override
    public void execute(DelegatePlanItemInstance planItemInstance) {
        planItemInstance.setVariable("javaDelegate", "executed");
    }
    
}
 * 
 */
public class ServiceTaskTest  {
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
				.addClasspathResource
			//	("com/shareniu/shareniu_flowable_study/cmmn/task/serviceTask/ServiceTaskTest.testJavaServiceTask.cmmn")
		//		("com/shareniu/shareniu_flowable_study/cmmn/task/serviceTask/ServiceTaskTest.testJavaServiceTaskFields.cmmn")
			//	("com/shareniu/shareniu_flowable_study/cmmn/task/serviceTask/ServiceTaskTest.testResultVariableName.cmmn")
				//("com/shareniu/shareniu_flowable_study/cmmn/task/serviceTask/ServiceTaskTest.testDelegateExpression.cmmn")
				("com/shareniu/shareniu_flowable_study/cmmn/task/serviceTask/ServiceTaskTest.testDelegateExpressionFields.cmmn")
				.deploy()
				.getId();
	}
	
    @Test
    public void testJavaServiceTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                        .caseDefinitionKey("myCase")
                        .variable("test", "test2")
                        .start();
        assertNotNull(caseInstance);
        
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE)
                .singleResult();
        assertNotNull(planItemInstance);
        
        assertEquals("executed", cmmnRuntimeService.getVariable(caseInstance.getId(), "javaDelegate"));
        assertEquals("test2", cmmnRuntimeService.getVariable(caseInstance.getId(), "test"));
        
        // Triggering the task should start the child case instance
       // cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
       // assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        
//        assertEquals("executed", cmmnHistoryService.createHistoricVariableInstanceQuery()
//                        .caseInstanceId(caseInstance.getId())
//                        .variableName("javaDelegate")
//                        .singleResult().getValue());
    }
    
    @Test
    public void testJavaServiceTaskFields() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                        .caseDefinitionKey("myCase")
                        .variable("test", "test")
                        .start();
        assertNotNull(caseInstance);
        
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE)
                .singleResult();
        assertNotNull(planItemInstance);
        
        assertEquals("test", cmmnRuntimeService.getVariable(caseInstance.getId(), "testValue"));
        assertEquals(true, cmmnRuntimeService.getVariable(caseInstance.getId(), "testExpression"));
        
        // Triggering the task should start the child case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
      //  assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        
        assertEquals("test", cmmnHistoryService.createHistoricVariableInstanceQuery()
                        .caseInstanceId(caseInstance.getId())
                        .variableName("testValue")
                        .singleResult().getValue());
        assertEquals(true, cmmnHistoryService.createHistoricVariableInstanceQuery()
                        .caseInstanceId(caseInstance.getId())
                        .variableName("testExpression")
                        .singleResult().getValue());
    }
    
    
    
    
    
    
    
    
    
    
    
    @Test
    public void testResultVariableName() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                        .caseDefinitionKey("myCase")
                        .variable("test", "test")
                       // .variable("testBean", "com.shareniu.shareniu_flowable_study.cmmn.task.serviceTask.TestBean")
                        .start();
        assertNotNull(caseInstance);
        
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE)
                .singleResult();
        assertNotNull(planItemInstance);
        
        assertEquals("hello test", cmmnRuntimeService.getVariable(caseInstance.getId(), "beanResponse"));
        
        // Triggering the task should start the child case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
     //   assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        
        assertEquals("hello test", cmmnHistoryService.createHistoricVariableInstanceQuery()
                        .caseInstanceId(caseInstance.getId())
                        .variableName("beanResponse")
                        .singleResult().getValue());
    }
    
    @Test
    public void testDelegateExpression() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                        .caseDefinitionKey("myCase")
                        .variable("test", "test2")
                        .start();
        assertNotNull(caseInstance);
        
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE)
                .singleResult();
        assertNotNull(planItemInstance);
        
        assertEquals("executed", cmmnRuntimeService.getVariable(caseInstance.getId(), "javaDelegate"));
        assertEquals("test2", cmmnRuntimeService.getVariable(caseInstance.getId(), "test"));
        
        // Triggering the task should start the child case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
     //   assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        
        assertEquals("executed", cmmnHistoryService.createHistoricVariableInstanceQuery()
                        .caseInstanceId(caseInstance.getId())
                        .variableName("javaDelegate")
                        .singleResult().getValue());
    }
    
    
    
    
    
    
    
    
    @Test
    public void testDelegateExpressionFields() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                        .caseDefinitionKey("myCase")
                        .variable("test", "test")
                        .start();
        assertNotNull(caseInstance);
        
        PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                .caseInstanceId(caseInstance.getId())
                .planItemInstanceState(PlanItemInstanceState.ACTIVE)
                .singleResult();
        assertNotNull(planItemInstance);
        
        assertEquals("test", cmmnRuntimeService.getVariable(caseInstance.getId(), "testValue"));
        assertEquals(true, cmmnRuntimeService.getVariable(caseInstance.getId(), "testExpression"));
        
        // Triggering the task should start the child case instance
        cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
       // assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
        
        assertEquals("test", cmmnHistoryService.createHistoricVariableInstanceQuery()
                        .caseInstanceId(caseInstance.getId())
                        .variableName("testValue")
                        .singleResult().getValue());
        assertEquals(true, cmmnHistoryService.createHistoricVariableInstanceQuery()
                        .caseInstanceId(caseInstance.getId())
                        .variableName("testExpression")
                        .singleResult().getValue());
    }
    
}
