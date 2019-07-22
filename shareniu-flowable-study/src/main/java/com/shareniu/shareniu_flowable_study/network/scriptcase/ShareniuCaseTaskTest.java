package com.shareniu.shareniu_flowable_study.network.scriptcase;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnManagementService;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.CmmnTaskService;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.repository.CmmnDeployment;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstanceState;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.engine.common.api.repository.EngineResource;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import com.shareniu.shareniu_flowable_study.cmmn.ch2.CaseTaskTest;
/**
 * 测试1
 *   <task id="returnValueScriptTask" name="Script with value" flowable:type="script" flowable:scriptFormat="JavaScript" flowable:resultVariableName="scriptResult">
                <extensionElements>
                    <flowable:field name="script">
                        <string><![CDATA[var a = 5; a+2;]]></string>
                    </flowable:field>
                </extensionElements>                
            </task>
flowable:resultVariableName="scriptResult" scriptResult会作为一个变量存在，变量的名称是scriptResult


测试2：
 <case id="scriptCase">
        <casePlanModel id="myScriptPlanModel" name="My Script CasePlanModel">
            <planItem id="returnValueScript" definitionRef="returnValueScriptTask" />
                <task id="returnValueScriptTask" name="Script with value" flowable:type="script" flowable:scriptFormat="JavaScript" flowable:resultVariableName="scriptResult">
                <extensionElements>
                    <flowable:field name="script">
                        <string><![CDATA[
                        a.setValue(5);
                        c = a.getValue() + b;
                    ]]></string>
                    </flowable:field>
                </extensionElements>                
            </task>
        </casePlanModel>
    </case>
    需要自定义IntValueHolder类
    
   测试3： 
    <case id="scriptCase">
        <casePlanModel id="myScriptPlanModel" name="My Script CasePlanModel">
            <planItem id="returnValueScript" definitionRef="returnValueScriptTask" />
                <task id="returnValueScriptTask" name="Script with value" flowable:type="script" flowable:scriptFormat="JavaScript" flowable:resultVariableName="scriptResult">
                <extensionElements>
                    <flowable:field name="script">
                        <string><![CDATA[c = a + b]]></string>
                    </flowable:field>
                </extensionElements>                
            </task>
        </casePlanModel>
    </case>
    
    结论：scriptResult返回值是最后操作的一个变量
    
    
   测试4： 
    <case id="scriptCase">
        <casePlanModel id="myScriptPlanModel" name="My Script CasePlanModel">
            <planItem id="planItemTaskA" name="Plan Item One" definitionRef="taskA" />
                <task id="taskA" name="Script Task Item" flowable:type="script" flowable:scriptFormat="JavaScript">
                    <documentation>This is a test documentation</documentation>
                <extensionElements>
                    <flowable:field name="script">
                        <string><![CDATA[var a = 5;planItemInstance.setVariable("int",a);planItemInstance.setVariable("string","my string value");]]></string>
                    </flowable:field>
                </extensionElements>                
            </task>
        </casePlanModel>
    </case>
   可以直接调用planItemInstance.setVariable方法
   
   
   
   测试5： 
    <case id="scriptCase">
        <casePlanModel id="myScriptPlanModel" name="My Script CasePlanModel">
            <planItem id="returnValueScript" definitionRef="returnValueScriptTask" />
                <task id="returnValueScriptTask" name="Script with value" flowable:type="script" flowable:scriptFormat="JavaScript" flowable:resultVariableName="scriptResult">
                <extensionElements>
                    <flowable:field name="script">
                        <string><![CDATA[
                        c = a.getValue() + b;
                        var IntValueHolder = Java.type("com.shareniu.shareniu_flowable_study.cmmn.task.script.CmmnScriptTaskTest$IntValueHolder");
                        var result = new IntValueHolder(c);
                        result;
                        ]]></string>
                    </flowable:field>
                </extensionElements>                
            </task>
        </casePlanModel>
    </case>
    
    
    
    
    
    
    
    
    
    
   测试6： 
   <casePlanModel id="myScriptPlanModel" name="My Script CasePlanModel">
            <planItem id="returnValueScript" definitionRef="returnValueScriptTask" />
                <task id="returnValueScriptTask" name="Script with value" flowable:type="script" flowable:scriptFormat="groovy" flowable:autoStoreVariables="true">
                <extensionElements>
                    <flowable:field name="script">
                        <string>
                            sum = 0
                            for ( i in inputArray ) {
                                sum += i
                            }
                        </string>
                    </flowable:field>
                </extensionElements>                
            </task>
        </casePlanModel>
   使用flowable:autoStoreVariables="true"会自动 作为变量存在 类似     flowable:resultVariableName="scriptResult"
   
   
   
   
   
   
 *
 *
 *
 *
 *
 *
 *
 */
public class ShareniuCaseTaskTest {
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
			//	.addClasspathResource("com/shareniu/shareniu_flowable_study/network/scriptcase/shareniu-scriptCase.cmmn.xml")
		.addClasspathResource("com/shareniu/shareniu_flowable_study/network/scriptcase/shareniu-scriptCase.testVariableResult.cmmn.xml")
				.deploy().getId();
	}

	@Test
	public void testBasicNonBlocking() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
				.caseDefinitionKey("shareniu-scriptCase")
				.variable("a", 10)
				.variable("b", 6)
				.start();
	}
	
	@Test
	public void triggerPlanItemInstance() {
		String caseInstanceId = "a126374e-4177-11e8-8fe7-de1fd53de8a4";
		PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
				.caseInstanceId(caseInstanceId).planItemInstanceElementId("planItem1")
				.planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
		System.out.println(planItemInstance);
		cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
	}

	@Test
	public void complete() {
		String taskId = "shareniu-8341b1c6-4111-11e8-aa1c-de1fd53de8a4";
		cmmnTaskService.complete(taskId);
	}
}
