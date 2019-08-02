package com.power.service;

import com.power.entity.PowerTask;
import org.flowable.task.api.Task;

import java.util.List;
import java.util.Map;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 16:37
 */
public interface PowerTaskService {
    List<PowerTask> queryUserTask(String assignee);

    Object completeTask(String taskId, Map<String, Object> vars);

    Task queryTaskByProcessInstanceId(String processInstanceId);

    Object queryAllTask(String assignee);

    Boolean checkTaskStatus(String taskId);
}
