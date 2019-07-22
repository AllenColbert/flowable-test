package com.shareniu.shareniu_flowable_study.bpmn.ch4.core;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.common.impl.AbstractEngineConfiguration;
import org.flowable.engine.common.impl.interceptor.Command;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.engine.common.impl.interceptor.CommandExecutor;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityManager;

public class StartActivityCmd implements Command<Void> {

	protected String executionId;
	protected String processDefinitionId;
	protected String currentFlowElementId;
	protected String deleteUserTaskId;
	protected String taskId;

	public StartActivityCmd(String executionId, String processDefinitionId, String currentFlowElementId,
			String deleteUserTaskId, String taskId) {
		this.executionId = executionId;
		this.processDefinitionId = processDefinitionId;
		this.currentFlowElementId = currentFlowElementId;
		this.deleteUserTaskId = deleteUserTaskId;
		this.taskId = taskId;
	}

	@Override
	public Void execute(CommandContext commandContext) {

		ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager(commandContext)
				.findById(executionId);

		AbstractEngineConfiguration currentEngineConfiguration = commandContext.getCurrentEngineConfiguration();
		CommandExecutor commandExecutor = currentEngineConfiguration.getCommandExecutor();
		Process process = commandExecutor.execute(new GetProcessCmd(processDefinitionId));
		FlowElement flowElement = process.getFlowElement(currentFlowElementId);
		executionEntity.setCurrentFlowElement(flowElement);

		TaskEntityManager taskEntityManager = org.flowable.task.service.impl.util.CommandContextUtil
				.getTaskEntityManager();
		TaskEntity taskEntity = taskEntityManager.findById(taskId);
		taskEntityManager.delete(taskId);
		org.flowable.engine.impl.util.CommandContextUtil.getHistoryManager(commandContext).recordTaskEnd(taskEntity, executionEntity,
				"jump");
		CommandContextUtil.getAgenda(commandContext).planContinueProcessOperation(executionEntity);
		return null;
	}

}
