package com.shareniu.v6.ch4;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.FlowableEngineAgenda;
import org.flowable.engine.common.impl.interceptor.Command;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityManager;
import org.flowable.task.service.impl.util.CommandContextUtil;

public class ShareniuJumpCmd implements Command<Void> {

	protected String taskId;
	protected String targetNodeId;

	public ShareniuJumpCmd(String taskId, String targetNodeId) {
		this.taskId = taskId;
		this.targetNodeId = targetNodeId;
	}

	@Override
	public Void execute(CommandContext commandContext) {
		ExecutionEntityManager executionEntityManager = org.flowable.engine.impl.util.CommandContextUtil
				.getExecutionEntityManager();

		TaskEntityManager taskEntityManager = CommandContextUtil.getTaskEntityManager();

		// 根据taskId获取执行实例信息

		TaskEntity taskEntity = taskEntityManager.findById(taskId);
		// 获取执行实例ID
		String executionId = taskEntity.getExecutionId();

		ExecutionEntity executionEntity = executionEntityManager.findById(executionId);
		ExecutionEntity rootExecutionEntity = executionEntity.getParent();
		System.out.println(rootExecutionEntity);
		Process process = ProcessDefinitionUtil.getProcess(executionEntity.getProcessDefinitionId());

		FlowElement targetFlowElement = process.getFlowElement(targetNodeId);
		// 设置当前的执行实例ID对应的 CurrentFlowElement为目标节点，目标节点是一个参数，可以进行传递。
		executionEntity.setCurrentFlowElement(targetFlowElement);
		FlowableEngineAgenda flowableEngineAgenda = org.flowable.engine.impl.util.CommandContextUtil.getAgenda();
		// 触发一下，让执行实例往下运行
		flowableEngineAgenda.planContinueProcessInCompensation(executionEntity);
		// 删除当前的任务，注意：不能完成任务 比如：taskService.complete(taskId);
		taskEntityManager.delete(taskId);

		org.flowable.engine.impl.util.CommandContextUtil.getHistoryManager().recordTaskEnd(taskEntity, executionEntity,
				"shareniu-jump");

		org.flowable.engine.impl.util.CommandContextUtil.getHistoryManager().recordActivityEnd(executionEntity,
				"shareniu-jump");
		// HistoricActivityInstanceEntityImpl
		return null;
	}

}
