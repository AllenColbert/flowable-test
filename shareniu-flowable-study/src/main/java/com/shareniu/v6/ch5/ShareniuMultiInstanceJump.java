package com.shareniu.v6.ch5;

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

public class ShareniuMultiInstanceJump implements Command<Void> {
	protected String taskId;
	protected String targetNodeId;

	public ShareniuMultiInstanceJump(String taskId, String targetNodeId) {
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
		//判断当前的节点是否是多实例节点
		ExecutionEntity executionEntity = executionEntityManager.findById(executionId);
		Process process = ProcessDefinitionUtil.getProcess(executionEntity.getProcessDefinitionId());
		Activity flowElement = (Activity) process.getFlowElement(taskEntity.getTaskDefinitionKey());
		
		
		Object behavior = flowElement.getBehavior();
		ExecutionEntity parent = executionEntity.getParent();
		if (parent!=null) {
			executionEntity=parent;
		}
		if ((behavior instanceof MultiInstanceActivityBehavior)) {
			
			executionEntityManager.deleteChildExecutions(parent, "jump", false);
			parent.setActive(true);
			parent.setMultiInstanceRoot(false);
			//parent.setScope(true);
			executionEntityManager.update(parent);
        }else {
        	taskEntityManager.delete(taskId);
        	org.flowable.engine.impl.util.CommandContextUtil.getHistoryManager().recordTaskEnd(taskEntity, executionEntity,
    				"shareniu-jump");

    		org.flowable.engine.impl.util.CommandContextUtil.getHistoryManager().recordActivityEnd(executionEntity,
    				"shareniu-jump");
        }
		
		// 删除当前的任务，注意：不能完成任务 比如：taskService.complete(taskId);
	
		Activity targetFlowElement = (Activity) process.getFlowElement(targetNodeId);
		
		 behavior = targetFlowElement.getBehavior();
		 executionEntity.setCurrentFlowElement(targetFlowElement);
			FlowableEngineAgenda flowableEngineAgenda = org.flowable.engine.impl.util.CommandContextUtil.getAgenda();
		 if (!(behavior instanceof MultiInstanceActivityBehavior)) {
			 
				flowableEngineAgenda.planContinueProcessInCompensation(parent==null ?executionEntity:parent);
		 }else {
			 List<ExecutionEntity> findChildExecutionsByParentExecutionId = executionEntityManager.findChildExecutionsByParentExecutionId(executionEntity.getId());
			 ExecutionEntity executionEntity2 = findChildExecutionsByParentExecutionId.get(0);
			 executionEntity2.setCurrentFlowElement(targetFlowElement);
			// 触发一下，让执行实例往下运行
				flowableEngineAgenda.planContinueProcessInCompensation(executionEntity2);
		 }
		
		// 设置当前的执行实例ID对应的 CurrentFlowElement为目标节点，目标节点是一个参数，可以进行传递。
		
		
		
		return null;
	}

}
