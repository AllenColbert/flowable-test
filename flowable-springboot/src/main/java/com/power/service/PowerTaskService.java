package com.power.service;

import com.power.entity.PowerTask;
import org.flowable.task.api.Task;

import java.util.List;
import java.util.Map;

/**
 * 任务Service
 * @author : xuyunfeng
 * @date :   2019/7/17 16:37
 */
public interface PowerTaskService {
    /**
     * 根据用户id查询任务列表
     * @param assignee 用户id
     * @return TaskList 任务列表
     */
    List<PowerTask> queryUserTask(String assignee);

    /**
     * 根据任务Id完成任务，并传递参数
     * @param taskId 任务Id
     * @param vars 参数
     * @return xx
     */
    Object completeTask(String taskId, Map<String, Object> vars);

    /**
     * 根据流程实例Id查询任务信息
     * @param processInstanceId 任务流程实例Id
     * @return 任务信息
     */
    Task queryTaskByProcessInstanceId(String processInstanceId);

    /**
     * 根据用户Id查询任务信息，废弃
     * @param assignee 用户Id
     * @return xx
     */
    Object queryAllTask(String assignee);

    /**
     * 根据任务Id查询任务状态
     * @param taskId 任务Id
     * @return xx
     */
    Boolean checkTaskStatus(String taskId);
}
