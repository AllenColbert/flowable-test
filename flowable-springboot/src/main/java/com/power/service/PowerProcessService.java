package com.power.service;


import com.power.entity.PowerDeployment;
import com.power.entity.PowerProcessDefinition;
import com.power.entity.PowerDeployEntity;
import org.flowable.ui.modeler.domain.AbstractModel;

import java.util.List;
import java.util.Map;


/**
 * 流程控制Service
 * @author xuyunfeng
 * @date 2019/7/9 14:20
 */
public interface PowerProcessService {

    /**
     * 根据流程文件名和部署信息部署流程文件
     * @param fileName 流程文件名
     * @param processDeploy 部署信息
     * @return xx
     */
    Object deployProcess(String fileName, PowerDeployEntity processDeploy);

    /**
     * 返回流程部署列表
     * @return 流程部署列表
     */
    List<PowerDeployment> findProcessList();

    /**
     * 返回流程定义列表
     * @return 流程定义列表
     */
    List<PowerProcessDefinition> findProcessDefinitionList();

    /**
     * 根据流程定义Id启动流程
     * @param processDefinitionId 流程定义Id
     * @return xx
     */
    Object startProcessInstanceById(String processDefinitionId);

    /**
     * 根据流程定义Id启动流程
     * @param processDefinitionId 流程定义Id
     * @param vars 参数
     * @return xx
     */
    Object startProcessInstanceById(String processDefinitionId, Map<String,Object> vars);

    /**
     * 根据流程定义key启动流程
     * @param processDefinitionKey 流程定义key
     * @param vars 参数
     * @return xx
     */
    Object startProcessInstanceByKey(String processDefinitionKey, Map<String, Object> vars);

    /**
     * 根据流程定义key启动流程
     * @param processDefinitionKey 流程定义key
     * @return xx
     */
    Object startProcessInstanceByKey(String processDefinitionKey);

    /**
     * 获取流程模型列表
     * @return 流程模型列表
     */
    List<AbstractModel> queryProcessModelList();
}
