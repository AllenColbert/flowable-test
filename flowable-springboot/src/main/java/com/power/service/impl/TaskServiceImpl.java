package com.power.service.impl;

import com.power.entity.PowerTask;
import com.power.mapper.TaskMapper;
import com.power.service.PowerTaskService;
import org.flowable.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author : xuyunfeng
 * @date :   2019/7/19 16:05
 */
@Service
public class TaskServiceImpl implements PowerTaskService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Object queryUserTask(String assignee) {
        PowerTask task = taskMapper.queryUserTask(assignee);

        if (task == null){
            return "当前用户没有任务";
        }

        return task;
    }

    @Override
    public Object completeTask(String taskId, Map<String, Object> vars) {
        taskService.complete(taskId,vars);
        return "完成任务";
    }


}
