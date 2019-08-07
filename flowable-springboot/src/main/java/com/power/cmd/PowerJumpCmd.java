package com.power.cmd;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.FlowableEngineAgenda;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityManager;

/**
 * 普通节点跳转执行对象
 * @author : xuyunfeng
 * @date :   2019/7/23 11:29
 */
public class PowerJumpCmd implements Command<Void> {
    private String taskId;
    private String targetNodeId;

    public PowerJumpCmd(String taskId, String targetNodeId) {
        this.taskId = taskId;
        this.targetNodeId = targetNodeId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        //获取执行实例管理类
        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager();
        //获取任务实体管理类
        TaskEntityManager taskEntityManager = org.flowable.task.service.impl.util.CommandContextUtil.getTaskEntityManager();
        //获取任务执行Id
        TaskEntity taskEntity = taskEntityManager.findById(taskId);
        //获取流程实例Id
        String processInstanceId = taskEntity.getProcessInstanceId();
        //获取执行实例Id
        String executionId = taskEntity.getExecutionId();
        //根据执行实例ID获取到执行实例
        ExecutionEntity executionEntity = executionEntityManager.findById(executionId);
        //获取根节点执行实例
        ExecutionEntity rootExectionEntity = executionEntity.getParent();
        //根据流程定义Id获取流程对象
        Process process = ProcessDefinitionUtil.getProcess(executionEntity.getProcessDefinitionId());
        //根据目标节点ID获取对应的流程元素
        FlowElement targetFlowElement = process.getFlowElement(targetNodeId);
        //将目标节点设置为当前的节点
        executionEntity.setCurrentFlowElement(targetFlowElement);
        //获取流程实例触发器
        FlowableEngineAgenda flowableEngineAgenda = CommandContextUtil.getAgenda();
        //让流程实例接着往下运行
        flowableEngineAgenda.planContinueProcessInCompensation(executionEntity);
        //删除当前的任务
        taskEntityManager.delete(taskId);
        //设置任务历史记录
        CommandContextUtil.getHistoryManager().recordTaskEnd(taskEntity,executionEntity,"Node-Jump-test");
        CommandContextUtil.getHistoryManager().recordActivityEnd(executionEntity,"Node-Jump-test");

        return null;
    }
}
