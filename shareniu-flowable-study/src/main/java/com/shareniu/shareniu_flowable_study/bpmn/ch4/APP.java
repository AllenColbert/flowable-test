package com.shareniu.shareniu_flowable_study.bpmn.ch4;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.common.impl.util.IoUtil;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.deploy.DeploymentManager;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.shareniu.shareniu_flowable_study.bpmn.ch4.core.ActCreation;
import com.shareniu.shareniu_flowable_study.bpmn.ch4.core.GetProcessCmd;
import com.shareniu.shareniu_flowable_study.bpmn.ch4.core.StartActivityCmd;
import com.shareniu.shareniu_flowable_study.bpmn.ch4.mapper.CreationMapper;
import com.shareniu.shareniu_flowable_study.bpmn.ch4.model.FlowableCreation;
import com.shareniu.shareniu_flowable_study.bpmn.ch4.model.TaskModel;
import com.shareniu.shareniu_flowable_study.bpmn.ch4.model.TempActivityModel;
import com.shareniu.shareniu_flowable_study.ch3.ProcessengineTest;

public class APP {
	ProcessEngine processEngine;
	public static RepositoryService repositoryService;
	public static IdentityService identityService;
	public static RuntimeService runtimeService;
	public static TaskService taskService;
	public static HistoryService historyService;
	public static ProcessEngineConfigurationImpl pec;
	public static DeploymentManager deploymentManager;
	public static ManagementService managementService;
	public static ActivityBehaviorFactory activityBehaviorFactory;

	@Before
	public void init() {
		InputStream inputStream = APP.class.getClassLoader()
				.getResourceAsStream("com/shareniu/shareniu_flowable_study/bpmn/ch4/flowable.cfg.xml");
		pec = (ProcessEngineConfigurationImpl) ProcessEngineConfiguration
				.createProcessEngineConfigurationFromInputStream(inputStream);
		processEngine = pec.buildProcessEngine();
		repositoryService = processEngine.getRepositoryService();
		identityService = processEngine.getIdentityService();
		runtimeService = processEngine.getRuntimeService();
		taskService = processEngine.getTaskService();
		historyService = processEngine.getHistoryService();
		deploymentManager = pec.getDeploymentManager();
		managementService = processEngine.getManagementService();
		activityBehaviorFactory = pec.getActivityBehaviorFactory();
	}

	@Test
	public void addBytes() {
		byte[] bytes = IoUtil.readInputStream(ProcessengineTest.class.getClassLoader().getResourceAsStream(
				"com/shareniu/shareniu_flowable_study/bpmn/ch4/twoTask.bpmn20.xml"), "twoTask.bpmn20.xml");
		String resourceName = "twoTask.bpmn";
		Deployment deployment = repositoryService.createDeployment().addBytes(resourceName, bytes).deploy();
		System.out.println(deployment);
	}

	@Test
	public void startProcessInstanceByKey() {
		String processDefinitionKey = "twoTask";
		ProcessInstance startProcessInstanceByKey = runtimeService.startProcessInstanceByKey(processDefinitionKey);
		System.out.println(startProcessInstanceByKey);
	}

	/**
	 * String processDefinitionId = execution.getProcessDefinitionId();
	 * org.flowable.bpmn.model.Process process =
	 * ProcessDefinitionUtil.getProcess(processDefinitionId); String activityId =
	 * execution.getCurrentActivityId(); FlowElement currentFlowElement =
	 * process.getFlowElement(activityId, true); //DeploymentManager
	 * 
	 * @throws Exception
	 */
	@Test
	public void addMultipleTask() throws Exception {
		String taskId = "57505";
		TaskEntity taskEntity = (TaskEntity) processEngine.getTaskService().createTaskQuery().taskId(taskId)
				.singleResult();
		System.out.println(taskEntity);
		String processDefinitionId = taskEntity.getProcessDefinitionId();
		Process process = managementService.executeCommand(new GetProcessCmd(processDefinitionId));
		System.out.println(process);

		ProcessDefinitionCacheEntry processDefinitionCacheEntry = getProcessDefinitionCacheEntry(processDefinitionId);
		Process process2 = processDefinitionCacheEntry.getProcess();
		System.out.println(process2);

		// ====================生成节点
		//
		UserTask userTask = new UserTask();
		String userTaskId = "u" + 1;
		userTask.setId(userTaskId);
		userTask.setName("uName" + 1);
		userTask.setAssignee("a" + 1);
		userTask.setBehavior(createUserTaskActivityBehavior(userTask));

		SequenceFlow s1 = new SequenceFlow();
		String id = "s1";
		s1.setId(id);
		userTask.setOutgoingFlows(Arrays.asList(s1));
		s1.setTargetFlowElement(process2.getFlowElement("shareniu-B"));
		// 获取A增加出现 获取 B增加入线
		userTask.setOutgoingFlows(Arrays.asList(s1));
		process2.addFlowElement(userTask);
		process2.addFlowElement(s1);
		getProcessDefinitionCacheEntry(processDefinitionId).setProcess(process2);
		// 删除shareniu-A 让u1入库
		managementService.executeCommand(
				new StartActivityCmd(taskEntity.getExecutionId(), "twoTask:2:32504", "u1", "shareniu-A", taskId));
	}

	@Test
	public void complete() throws Exception {
		String taskId = "92502";
		taskService.complete(taskId);
	}

	/**
	 * 常规做法
	 * 
	 */
	@Test
	public void testAdd() {
		String taskId = "80005";
		TaskEntity taskEntity = (TaskEntity) processEngine.getTaskService().createTaskQuery().taskId(taskId)
				.singleResult();
		String first = "shareniu-A";
		String last = "shareniu-B";
		boolean isFire = true;

		String activityIds = "shareniu-A,c,d,e,f";
		TaskModel aTaskModel = genarateTaskModel("shareniu-A", "shareniu-A", "shareniu-A");
		TaskModel cTaskModel = genarateTaskModel("c", "c", "c");
		TaskModel dTaskModel = genarateTaskModel("d", "d", "d");
		List<TaskModel> listTaskModel = new ArrayList<>();
		listTaskModel.add(aTaskModel);
		listTaskModel.add(cTaskModel);
		listTaskModel.add(dTaskModel);
		addMultipleTask(taskEntity.getProcessInstanceId(), taskEntity.getProcessDefinitionId(), first, last,
				activityIds, isFire, listTaskModel, true, taskEntity);

	}

	@Test
	public void testAdd2() {
		String taskId = "82502";
		TaskEntity taskEntity = (TaskEntity) processEngine.getTaskService().createTaskQuery().taskId(taskId)
				.singleResult();
		String first = "c";
		String last = "d";
		boolean isFire = false;

		String activityIds = "c2";
		TaskModel cTaskModel = genarateTaskModel("c2", "c2", "c2");
		List<TaskModel> listTaskModel = new ArrayList<>();
		listTaskModel.add(cTaskModel);
		addMultipleTask(taskEntity.getProcessInstanceId(), taskEntity.getProcessDefinitionId(), first, last,
				activityIds, isFire, listTaskModel, true, taskEntity);

	}

	private TaskModel genarateTaskModel(String id, String name, String doUserId) {
		TaskModel taskModel = new TaskModel();
		taskModel.setId(id);
		taskModel.setName(id);
		taskModel.setDoUerId(doUserId);
		return taskModel;
	}

	/**
	 * 加签节点 加签的节点跟实例进行绑定 目前 该方法 只支持 userTask的加签
	 * 
	 * @param processInstanceId
	 * @param processDefinitionId
	 * @param first
	 * @param last
	 * @param activityIds
	 * @param isFire
	 */
	public static void addMultipleTask(String processInstanceId, String processDefinitionId, String first, String last,
			String activityIds, boolean isFire, List<TaskModel> listTaskModel, boolean persistenceDataToDataBase,
			TaskEntity taskEntity) {
		// 校验开始以及结束的是否存在
		boolean validation = validation(first, last, processDefinitionId);
		if (validation) {

			Process processCache = getProcessCache(processDefinitionId);

			if (activityIds == null || !"".equals(activityIds)) {
				String[] split = activityIds.split(",");
				List<UserTask> userTasks = new ArrayList<>();
				List<SequenceFlow> sequenceFlows = new ArrayList<>();
				for (TaskModel taskModel : listTaskModel) {
					// 循环List创建节点
					UserTask userTask = genarateRawUserTask(taskModel);
					userTasks.add(userTask);
					processCache.addFlowElement(userTask);
				}

				for (int i = 0; i < userTasks.size(); i++) {
					UserTask userTask = userTasks.get(i);
					SequenceFlow genarateRawSequenceFlow = null;
					// 先生成连线 3个节点就是三个线，两个节点就是两个出线
					if (i == userTasks.size() - 1) {
						// 最后一个节点
						genarateRawSequenceFlow = genarateRawSequenceFlow(userTask.getId() + "--->" + last,
								userTask.getId(), last);
						genarateRawSequenceFlow.setTargetFlowElement(processCache.getFlowElement(last));
						userTask.setOutgoingFlows(Arrays.asList(genarateRawSequenceFlow));
					} else {
						genarateRawSequenceFlow = genarateRawSequenceFlow(
								userTask.getId() + "--->" + userTasks.get(i + 1).getId(), userTask.getId(),
								userTasks.get(i + 1).getId());
						genarateRawSequenceFlow.setTargetFlowElement(userTasks.get(i + 1));
						userTask.setOutgoingFlows(Arrays.asList(genarateRawSequenceFlow));
					}
					sequenceFlows.add(genarateRawSequenceFlow);
					processCache.addFlowElement(genarateRawSequenceFlow);
				}
				System.out.println(sequenceFlows);
				// 将所有的节点串起来
				getProcessDefinitionCacheEntry(processDefinitionId).setProcess(processCache);
				if (persistenceDataToDataBase && isFire) {
					if (taskEntity != null) {
						managementService.executeCommand(new StartActivityCmd(taskEntity.getExecutionId(),
								processDefinitionId, userTasks.get(0).getId(), taskEntity.getTaskDefinitionKey(),
								taskEntity.getId()));
					}

				}
				if (persistenceDataToDataBase) {
					// 持久化数据到数据库
					persistenceDataToDataBase(processInstanceId, processDefinitionId, activityIds, listTaskModel, first,
							last);
				}

			} else {
				throw new RuntimeException("activityIds不能为空！");
			}
		} else {
			throw new RuntimeException("节点不存在");
		}

	}

	private static Process getProcessCache(String processDefinitionId) {
		Process process = managementService.executeCommand(new GetProcessCmd(processDefinitionId));
		ProcessDefinitionCacheEntry processDefinitionCacheEntry = getProcessDefinitionCacheEntry(processDefinitionId);
		process = processDefinitionCacheEntry.getProcess();
		return process;
	}
	private static void persistenceDataToDataBase(String processInstanceId, String processDefinitionId,
			String activityIds, List<TaskModel> listTaskModel, String first, String last) {
		SqlSessionFactory sqlSessionFactory = pec.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		//将之前的全部置为1  updateState
		CreationMapper creationMapper = sqlSession.getMapper(CreationMapper.class);
		creationMapper.updateState(processInstanceId);
		//查询状态是1的 并且是最后一条的
		List<ActCreation> findLastOne = creationMapper.findLastOne(processInstanceId);
		sqlSession.commit();
		sqlSession.close();
		ActCreation lastOne=null;
		List<TaskModel> activitys=listTaskModel;
		if (findLastOne.size()>0) {
			lastOne=findLastOne.get(0);
			TempActivityModel lastTempActivityModel = JSON.parseObject(lastOne.getProcessText(), TempActivityModel.class);
			String lastfirst2 = lastTempActivityModel.getFirst();
			int index=-1;
		
			if (!lastfirst2.equals(first)) {
				 activitys = lastTempActivityModel.getActivitys();
				for (int i = 0; i < activitys.size(); i++) {
					TaskModel taskModel = activitys.get(i);
					if (taskModel.getId().equals(first)) {
						index=i;
						break;
					}
				}
				if (index!=-1) {
					activitys.addAll(index+1, listTaskModel);
				}
				System.out.println(activitys);
				last=lastTempActivityModel.getLast();
				first=lastTempActivityModel.getFirst();
			}
		}
		
	
		TempActivityModel tempActivityModel = new TempActivityModel();
		tempActivityModel.setActivityIds(activityIds);
		tempActivityModel.setActivitys(activitys);
		tempActivityModel.setFirst(first);
		tempActivityModel.setLast(last);
		// 将数据插入到数据库
		insertActivity(processDefinitionId, processInstanceId, JSON.toJSONString(tempActivityModel));
	}

	/**
	 * 生成连线
	 * 
	 * @param id
	 * @param sourceRef
	 * @param targetRef
	 * @return
	 */
	private static SequenceFlow genarateRawSequenceFlow(String id, String sourceRef, String targetRef) {
		SequenceFlow s = new SequenceFlow();
		s.setId(id);
		s.setSourceRef(sourceRef);
		s.setTargetRef(targetRef);
		return s;

	}

	private static UserTask genarateRawUserTask(TaskModel taskModel) {

		UserTask userTask = new UserTask();
		userTask.setId(taskModel.getId());
		userTask.setName(taskModel.getName());
		userTask.setAssignee(taskModel.getId());
		userTask.setBehavior(createUserTaskActivityBehavior(userTask));

		return userTask;
	}

	private static boolean validation(String first, String last, String processDefinitionId) {
		boolean returnResult = true;
		Process process = getProcess(processDefinitionId);
		FlowElement flowElement = process.getFlowElement(first);
		if (flowElement == null) {
			returnResult = false;
		}
		FlowElement flowElement2 = process.getFlowElement(last);
		if (flowElement2 == null) {
			returnResult = false;
		}
		FlowNode f1 = null;
		FlowNode f2;
		if (flowElement instanceof FlowNode) {
			f1 = (FlowNode) flowElement;
		}
		if (flowElement2 instanceof FlowNode) {
			f2 = (FlowNode) flowElement2;
		}
		List<SequenceFlow> outgoingFlows = f1.getOutgoingFlows();
		boolean isContain = false;
		for (SequenceFlow sequenceFlow : outgoingFlows) {
			String id = sequenceFlow.getTargetFlowElement().getId();
			if (flowElement2.getId().equals(id)) {
				System.out.println("包含.......");
				isContain = true;
				break;
			}

		}
		if (!isContain) {
			returnResult = false;
		}
		return returnResult;
	}

	/**
	 * 创建行为类
	 * 
	 * @param userTask
	 * @return
	 */
	private static UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask) {
		UserTaskActivityBehavior userTaskActivityBehavior = activityBehaviorFactory
				.createUserTaskActivityBehavior(userTask);
		return userTaskActivityBehavior;
	}

	/**
	 * 根据processDefinitionId 获取BpmnModel实例对象
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	private static BpmnModel getBpmnModel(String processDefinitionId) {
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		return bpmnModel;
	}

	/**
	 * 根据流程定义获取Process
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	private static Process getProcess(String processDefinitionId) {
		Process process = getBpmnModel(processDefinitionId).getProcesses().get(0);
		return process;
	}

	/**
	 * 根据processDefinitionId 获取缓存信息
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	public static ProcessDefinitionCacheEntry getProcessDefinitionCacheEntry(String processDefinitionId) {
		ProcessDefinitionCacheEntry processDefinitionCacheEntry = deploymentManager.getProcessDefinitionCache()
				.get(processDefinitionId);
		if (processDefinitionCacheEntry == null) {

		}
		return processDefinitionCacheEntry;
	}

	@Test
	public void testCustomerMapper() {
		SqlSessionFactory sqlSessionFactory = pec.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		List<ActCreation> selectList = sqlSession
				.selectList("com.shareniu.shareniu_flowable_study.bpmn.ch4.mapper.CreationMapper.find", null);
		System.out.println(selectList);
		for (ActCreation actCreation : selectList) {
			System.err.println(actCreation);
		}
	}

	public static void insertActivity(String processDefinitionId, String processInstanceId, String processText) {
		SqlSessionFactory sqlSessionFactory = pec.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		CreationMapper creationMapper = sqlSession.getMapper(CreationMapper.class);
		ActCreation actCreation = new ActCreation();
		actCreation.setProcessDefinitionId(processDefinitionId);
		actCreation.setProcessInstanceId(processInstanceId);
		actCreation.setProcessText(processText);
		creationMapper.insert(actCreation);
		sqlSession.commit();
		sqlSession.close();
	}

	@Test
	public void testCustomerMapperInsert() {
		SqlSessionFactory sqlSessionFactory = pec.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		CreationMapper creationMapper = sqlSession.getMapper(CreationMapper.class);
		ActCreation actCreation = new ActCreation();
		actCreation.setProcessDefinitionId("c");
		actCreation.setProcessInstanceId("c");
		actCreation.setProcessText("c");
		creationMapper.insert(actCreation);
		sqlSession.commit();
		sqlSession.close();
	}

	@Test
	public void toJSON() {
		FlowableCreation flowableCreation = new FlowableCreation();
		flowableCreation.setId("c");
		flowableCreation.setName("c");
		List<FlowableCreation> list = new ArrayList<>();
		list.add(flowableCreation);
		TempActivityModel tempActivityModel = new TempActivityModel();
		tempActivityModel.setActivityIds("1,2,3");
		// tempActivityModel.setActivitys(list);
		Object json = JSON.toJSON(tempActivityModel);
		System.out.println(json);

		TempActivityModel parseObject = JSON.parseObject(json.toString(), TempActivityModel.class);
		System.out.println(parseObject);
	}
	@Test
	public void toList() {
		List<String> list=new ArrayList<>();
		list.add("1");
		list.add("2");
		list.add(1, "3");
		System.out.println(list);
		
		
	}
}
