package com.power.service.impl;

import com.power.entity.PowerTask;
import com.power.mapper.TaskMapper;
import com.power.service.PowerTaskService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    public List<PowerTask> queryUserTask(String assignee) {

        return taskMapper.queryUserTask(assignee);
    }

    @Override
    public Object completeTask(String taskId, Map<String, Object> vars) {
        taskService.complete(taskId,vars);
        return "完成任务";
    }

    @Override
    public Task queryTaskByProcessInstanceId(String processInstanceId) {
        return  taskService.createTaskQuery().processInstanceId(processInstanceId).list().get(0);

    }

    @Override
    public Object queryAllTask(String assignee) {
        List<Task> list = new ArrayList<>();
        if (assignee == null){
            list = taskService.createTaskQuery().list();
        }else {
            list = taskService.createTaskQuery().taskAssignee(assignee).list();
        }
        //没办法呀，Task不能序列化
        List<String> infoList =  new ArrayList<>();

        for (Task task : list) {
            infoList.add("taskId："+task.getId());
            infoList.add("taskName："+task.getName());
            infoList.add("taskDefinitionKey："+task.getTaskDefinitionKey());
        }

        return infoList;
    }

    @Override
    public Boolean checkTaskStatus(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        return task.isSuspended();
    }

}
