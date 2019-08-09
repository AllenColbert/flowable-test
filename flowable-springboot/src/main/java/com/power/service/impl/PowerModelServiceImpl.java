package com.power.service.impl;

import com.power.cmd.GetProcessDefinitionCacheEntryCmd;
import com.power.service.PowerModelService;
import com.power.util.Result;
import com.power.util.ResultCode;
import org.apache.commons.collections.CollectionUtils;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型服务接口实现类
 * @author : xuyunfeng
 * @date :   2019/8/9 13:39
 */

@Service
public class PowerModelServiceImpl implements PowerModelService {

    @Qualifier("processEngine")
    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ManagementService managementService;


    @Override
    public Result addSingleNode(String processDefinitionId, UserTask userTask) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process process = bpmnModel.getProcesses().get(0);

        userTask.setBehavior(createUserTaskBehavior(userTask));

        process.addFlowElement(userTask);

        List<SequenceFlow> incomingFlows = userTask.getIncomingFlows();
        for (SequenceFlow incomingFlow : incomingFlows) {
            process.addFlowElement(incomingFlow);
            process.addFlowElementToMap(incomingFlow);
        }

        List<SequenceFlow> outgoingFlows = userTask.getOutgoingFlows();
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            process.addFlowElement(outgoingFlow);
            process.addFlowElementToMap(outgoingFlow);
        }

        new BpmnAutoLayout(bpmnModel).execute();

        //获取ProcessCache缓存管理对象
        ProcessDefinitionCacheEntry processCacheEntry = managementService
                .executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId));

        //设置缓存
        processCacheEntry.setProcess(process);
        return Result.success();
    }

    @Override
    public Result userTaskView(String processDefinitionId, String activityId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        Map<String, Object> map = new HashMap<>(255);
        int count = 1 ;

        List<Process> processes = bpmnModel.getProcesses();
        if (CollectionUtils.isNotEmpty(processes)) {
            for (Process process : processes) {
                //下面的map白写了，根据activityId只能获取单个UserTask
                FlowElement flowElement = process.getFlowElement(activityId);
                if (flowElement instanceof UserTask) {
                    UserTask userTask = (UserTask) flowElement;
                    map.put("userTask"+count,userTask);
                    count += 1;
                }
                if (flowElement instanceof SequenceFlow){
                    SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                    map.put("sequenceFlow"+count,sequenceFlow);
                    count += 1;
                }
                return Result.success(map);
            }
        }
        return Result.failure(ResultCode.RESULT_DATA_NONE);
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
