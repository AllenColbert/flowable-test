package com.shareniu.shareniu_flowable_study.network.task;

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
				.addClasspathResource("com/shareniu/shareniu_flowable_study/network/task/shareniu-helloword.cmmn.xml")
				.deploy().getId();
		// testBasicNonBlocking();
	}
	@Test
	public void testBasicNonBlocking1() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
				.caseDefinitionKey("shareniu-helloword")
			//	.variable("test", "test")
				.start();
	}
	@Test
	public void testBasicNonBlocking() {
		CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
				.caseDefinitionKey("shareniu-helloword")
			//	.variable("test", "test")
				.start();
	}
	@Test
	public void createCaseDefinitionQuery() {
		String caseDefinitionKey = "shareniu-helloword";
		List<CaseDefinition> list = cmmnRepositoryService.createCaseDefinitionQuery()
				.caseDefinitionKey(caseDefinitionKey).list();

		for (CaseDefinition cd : list) {
			System.out.println("分类######" + cd.getCategory());
			System.out.println("部署ID######" + cd.getDeploymentId());
			System.out.println("描述信息######" + cd.getDescription());
			System.out.println("id ######" + cd.getId());
			System.out.println("key######" + cd.getKey());
			System.out.println("名称######" + cd.getName());
			System.out.println("资源名称######" + cd.getResourceName());
			System.out.println("租户ID######" + cd.getTenantId());
			System.out.println("版本######" + cd.getVersion());
			System.out.println("映射实体类######" + cd.getClass());
		}
	}

	@Test
	public void createDeploymentQuery() {
		List<CmmnDeployment> list = cmmnRepositoryService.createDeploymentQuery().list();
		for (CmmnDeployment cd : list) {
			System.out.println("分类#######" + cd.getCategory());
			System.out.println("引擎版本#######" + cd.getEngineVersion());
			System.out.println("主键ID#######" + cd.getId());
			System.out.println("key#######" + cd.getKey());
			System.out.println("名称#######" + cd.getName());
			System.out.println("父部署ID#######" + cd.getParentDeploymentId());
			System.out.println("租户ID#######" + cd.getTenantId());
			Map<String, EngineResource> resources = cd.getResources();
			Set<Entry<String, EngineResource>> entrySet = resources.entrySet();
			for (Entry<String, EngineResource> entry : entrySet) {
				String key = entry.getKey();
				EngineResource value = entry.getValue();
				System.out.println(value.getDeploymentId());
				System.out.println(value.getName());
				System.err.println(new String(value.getBytes()));
				System.out.println(value.getClass());

			}
		}
	}

	/**
	 * 查询case实例
	 */
	@Test
	public void createCaseInstanceQuery() {
		String caseDefinitionKey = "shareniu-helloword";
		List<CaseInstance> list = cmmnRuntimeService.createCaseInstanceQuery().caseDefinitionKey(caseDefinitionKey)
				.list();
		for (CaseInstance ci : list) {
			System.out.println("业务键：" + ci.getBusinessKey());
			System.out.println("case定义ID：" + ci.getCaseDefinitionId());
			System.out.println("名称：" + ci.getName());
			System.out.println("父ID：" + ci.getParentId());
			System.out.println("状态" + ci.getState());
			System.out.println("状态：" + ci.getState());
			System.out.println("启动人：" + ci.getStartUserId());
			System.out.println("租户ID：" + ci.getTenantId());
			System.out.println("启动时间：" + ci.getStartTime());
			System.out.println("映射实体类：" + ci.getClass());
			Map<String, Object> caseVariables = ci.getCaseVariables();
			Set<Entry<String, Object>> entrySet = caseVariables.entrySet();

			for (Entry<String, Object> entry : entrySet) {
				System.out.println("##################变量信息");
				System.out.println(entry.getKey());
				Object value = entry.getValue();
				System.out.println(value);
			}
		}

	}

	@Test
	public void createHistoricCaseInstanceQuery() {
		String caseInstanceId = "53f2d070-4030-11e8-8863-de1fd53de8a4";
		List<HistoricCaseInstance> list = cmmnHistoryService.createHistoricCaseInstanceQuery()
				.caseInstanceId(caseInstanceId).list();

		for (HistoricCaseInstance hc : list) {
			System.out.println("业务键########" + hc.getBusinessKey());
			System.out.println("case定义ID########" + hc.getCaseDefinitionId());
			System.out.println("开始时间########" + hc.getStartTime());
			System.out.println("结束时间########" + hc.getEndTime());
		}
	}

	@Test
	public void createTaskQuery() {
		String caseInstanceId = "53f2d070-4030-11e8-8863-de1fd53de8a4";
		List<Task> list = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstanceId).list();
		for (Task task : list) {
			System.out.println("任务ID############" + task.getId());
			System.out.println("任务处理人############" + task.getAssignee());
			System.out.println("任务分类############" + task.getCategory());
			System.out.println("任务描述############" + task.getDescription());
			System.out.println("任务优先级############" + task.getPriority());
			System.out.println("任务定义key############" + task.getTaskDefinitionKey());
			System.out.println("任务定义创建时间############" + task.getCreateTime());
		}
	}

	@Test
	public void createPlanItemInstanceQuery() {
		String caseInstanceId = "53f2d070-4030-11e8-8863-de1fd53de8a4";

		List<PlanItemInstance> list = cmmnRuntimeService.createPlanItemInstanceQuery().caseInstanceId(caseInstanceId)
				.list();

		for (PlanItemInstance pii : list) {
			System.out.println("ID############" + pii.getId());
			System.out.println("名称############" + pii.getName());
			System.out.println("启动人############" + pii.getStartUserId());
			System.out.println("实体映射类############" + pii.getClass());

		}
	}

	@Test
	public void triggerPlanItemInstance() {
		String caseInstanceId = "953df5b9-49c9-11e8-94e1-a652884ed93a";
//		PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
//				.caseInstanceId(caseInstanceId).planItemInstanceElementId("planItem1")
//				.planItemInstanceState(PlanItemInstanceState.ACTIVE).singleResult();
//、	System.out.println(planItemInstance);
		cmmnRuntimeService.triggerPlanItemInstance("63a9c04b-49d9-11e8-a11e-a652884ed93a");
	}

	@Test
	public void complete() {
		String taskId = "shareniu-8341b1c6-4111-11e8-aa1c-de1fd53de8a4";
		cmmnTaskService.complete(taskId);
	}
}
