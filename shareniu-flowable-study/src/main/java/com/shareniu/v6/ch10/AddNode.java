package com.shareniu.v6.ch10;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;

import com.alibaba.fastjson.JSON;
import com.shareniu.v6.ch10.cmd.GetProcessCmd;
import com.shareniu.v6.ch10.cmd.GetProcessDefinitionCacheEntryCmd;
import com.shareniu.v6.ch10.core.GenerateActivity;
import com.shareniu.v6.ch10.mapper.CreationMapper;
import com.shareniu.v6.ch10.model.ActCreation;
import com.shareniu.v6.ch10.model.TaskModel;
import com.shareniu.v6.ch10.model.TempActivityModel;
import com.shareniu.v6.ch4.ShareniuJumpCmd;

public class AddNode {
	public void addMulitiNode(String processDefinitionId, String processInstanceId, ProcessEngine processEngine,
			List<TaskModel> taskModelList, String firstNodeId, String lastNodeId, Boolean persistenDataToDataBase,
			boolean isFire, String taskId, String targetNodeId) {

		boolean validation = validation(firstNodeId, lastNodeId, processDefinitionId, processEngine);
		if (validation) {
			ManagementService managementService = processEngine.getManagementService();
			ProcessDefinitionCacheEntry processDefinitionCacheEntry = managementService
					.executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId));
			Process process = processDefinitionCacheEntry.getProcess();
			List<UserTask> userTasks = new ArrayList<>(taskModelList.size());

			// 批量生成UserTask
			for (TaskModel taskModel : taskModelList) {
				UserTask userTask = GenerateActivity.transformation(taskModel, processEngine);
				userTasks.add(userTask);
				process.addFlowElement(userTask);

			}
			for (int i = 0; i < userTasks.size(); i++) {
				UserTask userTask = userTasks.get(i);
				SequenceFlow generateSequenceFlow = null;
				if (i == userTasks.size() - 1) {
					generateSequenceFlow = GenerateActivity.generateSequenceFlow(
							userTask.getId() + "---->>>" + lastNodeId, userTask.getId(), lastNodeId);
					generateSequenceFlow.setTargetFlowElement(process.getFlowElement(lastNodeId));
					userTask.setOutgoingFlows(Arrays.asList(generateSequenceFlow));
				} else {
					// 不是最后一个节点
					generateSequenceFlow = GenerateActivity.generateSequenceFlow(
							userTask.getId() + "--->>" + userTasks.get(i + 1).getId(), userTask.getId(),
							userTasks.get(i + 1).getId());
					generateSequenceFlow.setTargetFlowElement(userTasks.get(i + 1));
					userTask.setOutgoingFlows(Arrays.asList(generateSequenceFlow));
				}
				process.addFlowElement(generateSequenceFlow);
			}

			System.out.println(process);
			processDefinitionCacheEntry.setProcess(process);
			if (isFire) {
				managementService.executeCommand(new ShareniuJumpCmd(taskId, targetNodeId));
			}
			if (persistenDataToDataBase) {
				persistenDataToDataBase(processDefinitionId, processInstanceId, firstNodeId, lastNodeId, taskModelList,
						processEngine);
			}
		}
	}

	/**
	 * 将减签的数据持久化到数据库
	 * 
	 * @param taskEntity
	 * @param firstNodeId
	 * @param lastNodeId
	 * @param taskModelList
	 */
	private void persistenDataToDataBase(String processDefinitionId, String processInstanceId, String firstNodeId,
			String lastNodeId, List<TaskModel> taskModelList, ProcessEngine processEngine) {
		ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine
				.getProcessEngineConfiguration();
		SqlSessionFactory sqlSessionFactory = processEngineConfiguration.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();

		
		TempActivityModel t = new TempActivityModel();
		t.setFirst(firstNodeId);
		t.setLast(lastNodeId);
		t.setActivitys(taskModelList);
		StringBuffer stringBuffer = new StringBuffer();
		for (TaskModel taskModel : taskModelList) {
			stringBuffer.append(taskModel.getId() + ",");
		}
		t.setActivityIds(stringBuffer.toString());
		CreationMapper cm = sqlSession.getMapper(CreationMapper.class);
		ActCreation actCreation = new ActCreation();
		actCreation.setProcessDefinitionId(processDefinitionId);
		actCreation.setProcessInstanceId(processInstanceId);
		actCreation.setProcessText(JSON.toJSONString(t));
		cm.insert(actCreation);
		sqlSession.commit();
		sqlSession.close();

	}

	/**
	 * 校验两个节点是否是临近节点
	 * 
	 * @param firstNodeId
	 * @param lastNodeId
	 * @param processDefinitionId
	 * @return
	 */
	private boolean validation(String firstNodeId, String lastNodeId, String processDefinitionId,
			ProcessEngine processEngine) {

		boolean result = true;
		ManagementService managementService = processEngine.getManagementService();
		Process process = managementService.executeCommand(new GetProcessCmd(processDefinitionId));
		FlowElement firstFlowElement = process.getFlowElement(firstNodeId);
		if (firstFlowElement == null) {
			result = false;
		}
		FlowElement lastFlowElement = process.getFlowElement(lastNodeId);
		if (lastFlowElement == null) {
			result = false;
		}
		FlowNode f1 = null;
		FlowNode f2 = null;
		if (firstFlowElement instanceof FlowNode) {
			f1 = (FlowNode) firstFlowElement;
		}
		if (lastFlowElement instanceof FlowNode) {
			f2 = (FlowNode) lastFlowElement;
		}
		if (f1 == null || f2 == null) {
			throw new RuntimeException("节点不存在");
		}
		List<SequenceFlow> outgoingFlows = f1.getOutgoingFlows();
		boolean isContain = false;
		for (SequenceFlow sequenceFlow : outgoingFlows) {
			String id = sequenceFlow.getTargetFlowElement().getId();
			if (id.equals(lastFlowElement.getId())) {
				System.out.println("两个节点是临近界定");
				isContain = true;
				break;
			}
		}
		if (!isContain) {
			result = false;
		}
		return result;
	}
}
