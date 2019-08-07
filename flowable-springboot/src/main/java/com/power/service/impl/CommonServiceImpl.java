package com.power.service.impl;

import com.power.cmd.GetProcessCmd;
import com.power.cmd.GetProcessDefinitionCacheEntryCmd;
import com.power.entity.PowerTask;
import com.power.service.CommonService;
import org.flowable.bpmn.model.GraphicInfo;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author : xuyunfeng
 * @date :   2019/8/5 10:09
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Qualifier("processEngine")
    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private ManagementService managementService;

    @Override
    public UserTask createUserTask(PowerTask taskInfo) {
        UserTask userTask = new UserTask();

        userTask.setId(taskInfo.getId());
        userTask.setName(taskInfo.getName());
        userTask.setAssignee(taskInfo.getAssignee());
        userTask.setBehavior(createUserTaskBehavior(userTask));
        return  userTask;
    }

    @Override
    public GraphicInfo createGraphicInfo() {
        GraphicInfo graphicInfo = new GraphicInfo();
        graphicInfo.setX(130);
        graphicInfo.setY(140);
        graphicInfo.setHeight(13.55);
        graphicInfo.setWidth(10.23);
        graphicInfo.setExpanded(true);
        return graphicInfo;
    }

    @Override
    public Process queryProcessById(String processDefinitionId) {
        return managementService.executeCommand(new GetProcessCmd(processDefinitionId));
    }

    @Override
    public SequenceFlow createSequenceFlow(String sourceRef,String targetRef,String processDefinitionId) {

        Process process = queryProcessById(processDefinitionId);

        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId(sourceRef+"to"+targetRef);
        sequenceFlow.setSourceRef(sourceRef);
        sequenceFlow.setTargetRef(targetRef);
        sequenceFlow.setSourceFlowElement(process.getFlowElement(sourceRef));
        sequenceFlow.setTargetFlowElement(process.getFlowElement(targetRef));

        return sequenceFlow;
    }

    @Override
    public void updateProcess(Process process, String processDefinitionId) {
        //获取ProcessCache缓存管理对象
        ProcessDefinitionCacheEntry processCacheEntry = managementService
                .executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId));
        //设置缓存
        processCacheEntry.setProcess(process);

    }

    /**
     * 创建任务节点行为类
     * 可能会复用，单独抽出来
     *
     * @param userTask 用户任务节点
     * @return 任务节点行为类
     */
    private Object createUserTaskBehavior(UserTask userTask) {
        //获取流程引擎配置类
        ProcessEngineConfigurationImpl engineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        // 获取活动行为工厂
        ActivityBehaviorFactory activityBehaviorFactory = engineConfiguration.getActivityBehaviorFactory();
        return activityBehaviorFactory.createUserTaskActivityBehavior(userTask);
    }
}
