package com.power.util;

import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;

/**
 * 工具类生成节点信息，---功能性废弃
 * @author : xuyunfeng
 * @date :   2019/7/25 14:58
 */
@Deprecated
public class NodeUtil {
    /**
     * 生成连线信息
     *
     * @param id
     * @param source
     * @param target
     * @return
     */
    public static SequenceFlow generateSequenceFlow(String id, String source, String target) {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId(id);
        sequenceFlow.setSourceRef(source);
        sequenceFlow.setTargetRef(target);
        return sequenceFlow;
    }

    /**
     * 生成任务节点
     *
     * @param id
     * @param name
     * @param assignee
     * @param processEngine
     * @return
     */
    public static UserTask generateUserTask(String id, String name, String assignee, ProcessEngine processEngine) {
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setAssignee(assignee);
        userTask.setName(name);
        userTask.setBehavior(createUserTaskBehavior(userTask, processEngine));
        return userTask;
    }

    /**
     * 生成任务节点行为类
     *
     * @param userTask
     * @param processEngine
     * @return
     */
    public static Object createUserTaskBehavior(UserTask userTask, ProcessEngine processEngine) {
        ProcessEngineConfigurationImpl processEngineConfiguration =
                (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        // activityBehaviorFactory
        ActivityBehaviorFactory activityBehaviorFactory = processEngineConfiguration.getActivityBehaviorFactory();
        return activityBehaviorFactory.createUserTaskActivityBehavior(userTask);
    }

}
