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

    /**
     * @param fileName 文件名称
     * @param processDeploy
     * @return
     */
    Object deployProcess(String fileName, PowerDeployEntity processDeploy);

    /**
     * @return  显示流程定义列表
     */
    List<PowerDeployment> findProcessList();


    List<PowerProcdef> findProcdefList();

    Object startProcessInstance(String procDefId);

    Object startProcessInstance(String procDefId, Map<String,Object> vars);
}
