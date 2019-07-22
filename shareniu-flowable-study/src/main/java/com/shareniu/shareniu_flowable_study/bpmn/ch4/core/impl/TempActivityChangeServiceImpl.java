package com.shareniu.shareniu_flowable_study.bpmn.ch4.core.impl;


import org.flowable.engine.ProcessEngine;
import org.flowable.engine.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.transaction.annotation.Transactional;

import com.shareniu.shareniu_flowable_study.bpmn.ch4.core.TempActivityChangeService;

/**
 *临时签
 */
public class TempActivityChangeServiceImpl implements TempActivityChangeService {


    /**
     * 加签功能
     * @param taskId
     * @param processId
     * @param userCodes
     * @throws Exception
     */
    @Transactional
    public void addActivity(ProcessEngine processEngine,String taskId,String processId, String userCodes,String activityName,String doUserId) throws Exception{
    	DefaultTaskFlowControlService defaultTaskFlowControlService = new DefaultTaskFlowControlService(null,
                processEngine, processId);
        TaskService taskService = processEngine.getTaskService();
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).active().processInstanceId(processId).singleResult();
        taskService.setAssignee(taskId,doUserId);
        activityName = task.getName()+","+activityName;
        String[] assos = userCodes.split(",");
        String[] activityNames = activityName.split(",");
        //添加功能
        defaultTaskFlowControlService.insertTasksAfter(task.getTaskDefinitionKey(),taskId,doUserId,activityNames,assos);
    }



    
}
