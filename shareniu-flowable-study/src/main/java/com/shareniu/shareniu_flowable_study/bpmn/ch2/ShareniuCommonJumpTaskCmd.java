package com.shareniu.shareniu_flowable_study.bpmn.ch2;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.FlowableEngineAgenda;
import org.flowable.engine.common.impl.interceptor.Command;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityManager;
import org.flowable.task.service.impl.util.CommandContextUtil;

public class ShareniuCommonJumpTaskCmd implements Command<Void> {
	
	protected String taskId;  
    protected String target;  
  
    public ShareniuCommonJumpTaskCmd(String taskId, String target) {  
        this.taskId = taskId;  
        this.target = target;  
    }  
	@Override
	public Void execute(CommandContext commandContext) {
		ExecutionEntityManager executionEntityManager = org.flowable.engine.impl.util.CommandContextUtil.getExecutionEntityManager();  
        TaskEntityManager taskEntityManager = CommandContextUtil.getTaskEntityManager();  
        TaskEntity taskEntity = taskEntityManager.findById(taskId);  
        ExecutionEntity ee = executionEntityManager.findById(taskEntity.getExecutionId());  
        ee=ee.getParent();
        Process process = ProcessDefinitionUtil.getProcess(ee.getProcessDefinitionId());  
        FlowElement targetFlowElement = process.getFlowElement(target);  
        ee.setCurrentFlowElement(targetFlowElement);  
        FlowableEngineAgenda agenda = org.flowable.engine.impl.util.CommandContextUtil.getAgenda();  
        agenda.planContinueProcessInCompensation(ee);  
        taskEntityManager.delete(taskId);  
        org.flowable.engine.impl.util.CommandContextUtil.getHistoryManager(commandContext).recordTaskEnd(taskEntity, ee, "jump");
		
		return null;
	}

}
