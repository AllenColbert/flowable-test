package com.power.service;

import com.power.util.Result;
import org.flowable.bpmn.model.UserTask;

/**
 * 模型控制服务类接口
 * @author : xuyunfeng
 * @date :   2019/8/9 13:37
 */

public interface PowerModelService {
    /**
     * 添加单个用户任务节点到流程中
     * @param processDefinitionId 流程定义Id
     * @param userTask 用户任务节点
     * @return Result
     */
    Result addSingleNode(String processDefinitionId, UserTask userTask);

    /**
     * 测试用，查看单个用户节点的传递数据格式
     * @param processDefinitionId 流程定义Id
     * @param activityId 节点Id
     * @return Result
     */
    Result userTaskView(String processDefinitionId,String activityId);
}
