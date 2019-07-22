package com.shareniu.v6.ch6;

import java.util.List;

import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.FlowableEngineAgenda;
import org.flowable.engine.common.impl.interceptor.Command;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityManager;
import org.flowable.task.service.impl.util.CommandContextUtil;

public class ShareniuMuliInstanceJump implements Command<Void> {

	protected String taskId;
	protected String targetNodeId;

	public ShareniuMuliInstanceJump(String taskId, String targetNodeId) {
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
		ExecutionEntity parentExecutionEntity = executionEntity.getParent();
		System.out.println(parentExecutionEntity);

		Process process = ProcessDefinitionUtil.getProcess(executionEntity.getProcessDefinitionId());
		// 当前任务的key
		String currentTaskDefinitionKey = taskEntity.getTaskDefinitionKey();
		// 当前任务
		Activity currentActivity = (Activity) process.getFlowElement(currentTaskDefinitionKey);
		Object behavior = currentActivity.getBehavior();
//		if (parentExecutionEntity != null) {
//			executionEntity = parentExecutionEntity;
//		}
		// 如果是多实例任务有
		if (behavior instanceof MultiInstanceActivityBehavior) {
			executionEntityManager.deleteChildExecutions(parentExecutionEntity, "shareniu-mulijumop", false);
			// 修改is_mi_root_字段
			parentExecutionEntity.setMultiInstanceRoot(false);
			// 修改is_active_
			parentExecutionEntity.setActive(true);
			// 需要更新数据
			executionEntityManager.update(parentExecutionEntity);
		} else {
			// 删除当前的任务，注意：不能完成任务 比如：taskService.complete(taskId);
			taskEntityManager.delete(taskId);

			org.flowable.engine.impl.util.CommandContextUtil.getHistoryManager().recordTaskEnd(taskEntity,
					executionEntity, "shareniu-jump");

			org.flowable.engine.impl.util.CommandContextUtil.getHistoryManager().recordActivityEnd(executionEntity,
					"shareniu-jump");
		}

		// 执行任务节点的跳转
		// ===============================================================

		FlowElement targetFlowElement = process.getFlowElement(targetNodeId);
		executionEntity.setCurrentFlowElement(targetFlowElement);
		// 设置当前的执行实例ID对应的 CurrentFlowElement为目标节点，目标节点是一个参数，可以进行传递。

		FlowableEngineAgenda flowableEngineAgenda = org.flowable.engine.impl.util.CommandContextUtil.getAgenda();
		// 如果是多实例任务
		if (!(behavior instanceof MultiInstanceActivityBehavior)) {
			// 触发一下，让执行实例往下运行
			currentActivity = (Activity) targetFlowElement;
			behavior = currentActivity.getBehavior();
			if (behavior instanceof MultiInstanceActivityBehavior) {
				flowableEngineAgenda.planContinueProcessInCompensation(executionEntity);
			} else {
				flowableEngineAgenda.planContinueProcessInCompensation(
						parentExecutionEntity == null ? executionEntity : parentExecutionEntity);
			}

		} else {
			ExecutionEntity rootExecutionEntity = parentExecutionEntity.getParent();
			List<ExecutionEntity> findChildExecutionsByParentExecutionId = executionEntityManager
					.findChildExecutionsByParentExecutionId(rootExecutionEntity.getId());
			if (findChildExecutionsByParentExecutionId.size() > 0) {
				ExecutionEntity executionEntity2 = findChildExecutionsByParentExecutionId.get(0);
				executionEntity2.setCurrentFlowElement(targetFlowElement);
				flowableEngineAgenda.planContinueProcessInCompensation(executionEntity2);
			}
		}

		// HistoricActivityInstanceEntityImpl
		return null;
	}

}
