package com.power.service;


import com.power.util.Result;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 任务Service
 * @author : xuyunfeng
 * @date :   2019/7/17 16:37
 */
public interface PowerTaskService {

    /**
     * 根据流程实例ID挂起流程实例
     * @param processInstanceId 流程实例Id
     * @return Result
     */
    Result suspendProcessInstanceById(String processInstanceId);

    /**
     * 根据流程实例ID激活流程实例
     * @param processInstanceId 流程实例Id
     * @return Result
     */
    Result activateProcessInstanceById(String processInstanceId);

    /**
     * 获取流程图
     * @param httpServletResponse response响应
     * @param processInstanceId  流程实例Id
     * @throws IOException IO流异常
     */
    void getProcessDiagram(HttpServletResponse httpServletResponse, String processInstanceId) throws IOException;

    /**
     * 查询session中当前用户任务列表
     * @param model 模型
     * @param response 响应体
     * @return Result
     */
    Result queryCurrentUserTasks(Model model,HttpServletResponse response);

    /**
     * 完成任务
     * --待删除
     * @param taskId 任务Id
     * @param assignee 用户id
     * @param vars 参数map
     * @return Result
     */
    Result completeTask(String taskId, String assignee, Map<String, Object> vars);

    /**
     * 完成任务
     * @param taskId 任务Id
     * @param vars 参数map
     * @return Result
     */
    Result completeTask(String taskId, Map<String ,Object> vars);

    /**
     * 重写CMD完成任意任务节点跳转操作
     * @param taskId 当前任务id
     * @param targetNodeId 目标节点
     * @return Result
     */
    Result nodeJumpCmd(String taskId, String targetNodeId);

    /**
     * 显示所有可退到上一步的节点
     * @param processInstanceId 执行实例Id
     * @return Result
     */
    Result returnSourceNode(String processInstanceId);

    /**
     * 执行节点退回
     * @param processInstanceId 执行实例Id
     * @param targetNodeId 退回目标节点ID
     * @return Result
     */
    Result executeReturn(String processInstanceId, String targetNodeId);

    /**
     * 返回表单填充数据
     * @param taskId 任务Id
     * @return Result
     */
    Result findConditionExpression(String taskId);

    /**
     * 检查当前任务是否存在外置表单
     * @param taskId 任务Id
     * @return result
     */
    Result checkFormExist(String taskId);

    /**
     * 给前端展示表单
     * @param taskId 任务Id
     * @return Result
     */
    Result showForm(String taskId);

    /**
     * 查询用户任务
     * @param assignee 用户Id
     * @return Result
     */
    Result queryTaskList(String assignee);

    /**
     * 条件查询用户任务
     * @param vars 任务参数
     * @return Result
     */
    Result queryTaskListByCondition(Map<String,String> vars);
}
