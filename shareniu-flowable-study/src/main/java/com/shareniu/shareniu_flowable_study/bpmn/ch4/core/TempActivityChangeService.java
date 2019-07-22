package com.shareniu.shareniu_flowable_study.bpmn.ch4.core;

import org.flowable.engine.ProcessEngine;

/**
 */
public interface TempActivityChangeService {

    /**
     * 加签功能 加串行的签
     * @param taskId
     * @param processId
     * @return
     */
    public void addActivity(ProcessEngine processEngine,String taskId, String processId,String user,String activityName,String doUserId) throws Exception;

    
}
