package com.power.service;


import com.power.entity.PowerDeployment;
import com.power.entity.PowerProcessDefinition;
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

    List<PowerProcessDefinition> findProcdefList();

    Object startProcessInstanceById(String processDefinitionId);

    Object startProcessInstanceById(String processDefinitionId, Map<String,Object> vars);

    Object startProcessInstanceByKey(String processDefinitionKey, Map<String, Object> vars);

    Object startProcessInstanceByKey(String processDefinitionKey);
}
