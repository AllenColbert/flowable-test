package com.power.service;


import com.power.entity.PowerDeployEntity;
import com.power.entity.PowerDeployment;
import com.power.util.Result;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 流程控制Service
 * @author xuyunfeng
 * @date 2019/7/9 14:20
 */
public interface PowerProcessService {

    /**
     * 获取流程模型列表
     * @return 流程模型列表
     */
    Result queryProcessModelList();

    /**
     * 返回流程定义列表
     * @return 流程定义列表
     */
    Result queryProcessDefinitionList();

    /**
     * 根据流程定义Id启动流程
     * @param processDefinitionId 流程定义Id
     * @return xx
     */
    Result startProcessInstanceById(String processDefinitionId);

    /**
     * 根据部署id（默认级联）删除已部署的流程实例
     * @param deploymentId 部署id
     * @param concatenation 是否开启级联删除（默认开启）
     * @return Result
     */
    Result deleteProcessByDeploymentId(String deploymentId, Boolean concatenation);

    /**
     * 通过流程定义Id挂起整个流程
     * @param processDefinitionId 流程定义Id
     * @param suspendProcessInstances 是否挂起所有的流程实例,默认开启
     * @param suspensionDate 流程定义将被暂停的日期，为null时，会立即激活
     * @return Result
     */
    Result suspendProcessByProcessDefinitionId(String processDefinitionId, Boolean suspendProcessInstances, Date suspensionDate);

    /**
     *通过流程定义Id激活整个流程
     * @param processDefinitionId 流程定义Id
     * @param suspendProcessInstances 是否激活所有的流程实例，默认开启
     * @param suspensionDate 流程定义将被暂停的日期，为null时，会立即激活
     * @return Result
     */
    Result activateProcessByProcessDefinitionId(String processDefinitionId, Boolean suspendProcessInstances, Date suspensionDate);

    /**
     * 根据流程模型Id启动流程
     * @param modelId 流程模型Id act_de_model表中
     * @return Result
     */
    Result deployModelByModelId(String modelId);

    /**
     * 根据流程模型id删除流程模型
     * @param modelId 模型Id
     * @return Result
     *
     */
    Result deleteModelById(String modelId);


//################################未重构部分################################

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
     * 根据流程定义key启动流程
     * @param processDefinitionKey 流程定义key
     * @return xx
     */
    Object startProcessInstanceByKey(String processDefinitionKey);
}
