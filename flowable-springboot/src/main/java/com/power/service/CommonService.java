package com.power.service;

import com.power.entity.PowerTask;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;

/**
 * 通用Service，将一些方法抽取出来方便管理
 * @author : xuyunfeng
 * @date :   2019/8/5 10:08
 */
public interface CommonService {

    /**
     * 创建基本用户实例对象
     * @param taskInfo 用户任务实例
     * @return UserTask
     */
   UserTask createUserTask(PowerTask taskInfo);

    /**
     * 根据流程定义Id查询流程对象
     * @param processDefinitionId 流程定义Id
     * @return 流程对象
     */
   Process queryProcessById(String processDefinitionId);

    /**
     * 创建顺序流程
     * @param sourceRef 源节点
     * @param targetRef 目标节点
     * @param processDefinitionId 流程定义Id
     * @return 顺序流程
     */
   SequenceFlow createSequenceFlow(String sourceRef,String targetRef,String processDefinitionId);

    /**
     * 更新流程对象信息
     * @param process 流程对象
     * @param processDefinitionId 流程定义Id
     */
   void updateProcess(Process process,String processDefinitionId);

}
