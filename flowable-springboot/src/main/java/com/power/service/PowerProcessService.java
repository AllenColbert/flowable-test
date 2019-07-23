package com.power.service;


import com.power.entity.PowerDeployment;
import com.power.entity.PowerProcdef;
import com.power.entity.PowerDeployEntity;

import java.util.List;
import java.util.Map;


/**
 * @author xuyunfeng
 * @date 2019/7/9 14:20
 */
public interface PowerProcessService {

    Object deployProcess(String fileName, PowerDeployEntity processDeploy);


    List<PowerDeployment> findProcessList();


    List<PowerProcdef> findProcdefList();

    Object startProcessInstance(String procDefId);

    Object startProcessInstance(String procDefId, Map<String,Object> vars);

    Object startProcessInstanceByKey(String procDefKey, Map<String, Object> vars);
}
