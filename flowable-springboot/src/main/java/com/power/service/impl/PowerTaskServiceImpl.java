package com.power.service.impl;

import com.power.cmd.NodeJumpCmd;
import com.power.entity.PowerTask;
import com.power.mapper.TaskMapper;
import com.power.service.PowerTaskService;
import com.power.util.ListUtils;
import com.power.util.Result;
import com.power.util.ResultCode;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.*;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : xuyunfeng
 * @date :   2019/7/19 16:05
 */
@Service
public class PowerTaskServiceImpl implements PowerTaskService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Qualifier("processEngine")
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private HttpSession session;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private TaskMapper taskMapper;


    @Override
    public Result suspendProcessInstanceById(String processInstanceId) {
        Result result = checkProcessStatusByProcessInstanceId(processInstanceId);
        //先判断查询到的任务的状态码
        //如果状态码不是200
        if (!result.getCode().equals(ResultCode.SUCCESS.code())) {
            //返回数据为空的结果
            if (result.getCode().equals(ResultCode.RESULT_DATA_NONE.code())) {
                return result;
            }
            //当查询的任务为多实例时
            if (result.getCode().equals(ResultCode.TASK_TYPE_MULTIPLE_INSTANCES.code())) {
                result.setMsg("该任务为多实例任务，无法进行挂起操作");
                return result;
            }
        }
        //当查询到该任务已经被挂起时
        if (result.getData().equals(true)) {
            return Result.failure(ResultCode.PROCESS_IS_SUSPENDED);
        }
        runtimeService.suspendProcessInstanceById(processInstanceId);
        return Result.success();

    }

    @Override
    public Result activateProcessInstanceById(String processInstanceId) {
        Result result = checkProcessStatusByProcessInstanceId(processInstanceId);
        //首先判断查询到的任务的状态码
        //如果状态码不是200
        if (!result.getCode().equals(ResultCode.SUCCESS.code())) {
            //返回数据为空的结果
            if (result.getCode().equals(ResultCode.RESULT_DATA_NONE.code())) {
                return result;
            }
            //当查询的任务为多实例时
            if (result.getCode().equals(ResultCode.TASK_TYPE_MULTIPLE_INSTANCES.code())) {
                result.setMsg("该任务为多实例任务，无法进行激活操作");
                return result;
            }
        }
        //当查询到该任务已经被激活时
        if (result.getData().equals(false)) {
            return Result.failure(ResultCode.PROCESS_IS_ACTIVATED);
        }
        runtimeService.activateProcessInstanceById(processInstanceId);
        return Result.success();
    }

    @Override
    public void getProcessDiagram(HttpServletResponse httpServletResponse, String processInstanceId) throws IOException {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        //流程走完的不显示图
        if (pi == null) {
            return;
        }
        //
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(pi.getId()).list();

        List<String> instanceIds = new ArrayList<>();

        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        for (Task task : taskList) {
            String instanceId = task.getProcessInstanceId();
            instanceIds.add(instanceId);
        }

        List<Execution> executions = new ArrayList<>();

        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        for (String instanceId : instanceIds) {
            List<Execution> executionList = runtimeService.createExecutionQuery().processInstanceId(instanceId).list();
            executions.addAll(executionList);
        }

        //得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }

        List<ActivityInstance> flows2 = runtimeService.createActivityInstanceQuery()
                .activityType(BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW).processInstanceId(processInstanceId).list();

        for (ActivityInstance activityInstance : flows2) {
            String activityId = activityInstance.getActivityId();
            flows.add(activityId);
        }

        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessDiagramGenerator diagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();

        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, "宋体", "宋体", "宋体", null, 1.0D, true);

        OutputStream out = null;
        byte[] buf = new byte[1024];
        int length = 0;
        try {
            out = httpServletResponse.getOutputStream();
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public Result queryCurrentUserTasks(Model model, HttpServletResponse response) {

        User user = (User) session.getAttribute("user");

        if (user == null || user.getId() == null) {

            return Result.failure(ResultCode.USER_NOT_EXIST);
        }
        String userId = user.getId();

        List<PowerTask> tasks = taskMapper.queryUserTask(userId);

        if (tasks == null || tasks.size() == 0) {
            return Result.failure(ResultCode.TASKS_IS_NULL);
        }

        model.addAttribute("tasks", tasks);

        return Result.success(tasks);
    }

    @Override
    public Result completeTask(String taskId, String assignee, Map<String, Object> vars) {
        Result taskStatus = checkTaskStatus(taskId);
        if (!taskStatus.getCode().equals(ResultCode.SUCCESS.code())) {
            return taskStatus;
        }
        taskService.complete(taskId, vars);
        return Result.success();
    }

    @Override
    public Result completeTask(String taskId, Map<String, Object> vars) {
        Result taskStatus = checkTaskStatus(taskId);
        if (!taskStatus.getCode().equals(ResultCode.SUCCESS.code())) {
            return taskStatus;
        }
        String processInstanceId = findInstanceIdByTaskId(taskId);
        //完成任务的时候可以选中是否添加评论 ,防止 hi_comment表数据过大;
        String comment = vars.get("message").toString();
        if (!"".equals(comment)){
            taskService.addComment(taskId,processInstanceId,comment);
        }
        taskService.complete(taskId, vars);
        return Result.success();
    }

    @Override
    public Result nodeJumpCmd(String taskId, String targetNodeId) {
        try {
            managementService.executeCommand(new NodeJumpCmd(taskId, targetNodeId));
        } catch (Exception e) {
            return Result.failure(ResultCode.CMD_ERROR_MESSAGE);
        }
        return Result.success();
    }

    @Override
    public Result returnableNode(String processInstanceId) {

        List<String> activityIds = findActivityIdsByInstanceId(processInstanceId);

        if (activityIds.size() == 0){
            return Result.failure(ResultCode.RESULT_DATA_NONE);
        }

        String processDefinitionId = findDefinitionIdByInstanceId(processInstanceId);

        Process process = findProcessByProcessDefinitionId(processDefinitionId);

        List<UserTask> userTasks = new ArrayList<>();
        List<ParallelGateway> parallelGateways = new ArrayList<>();
        //获取 List<UserTask> ，有的任务不止一个userTask
        for (String activityId : activityIds) {
            FlowElement flowElement = process.getFlowElement(activityId);
            if (flowElement instanceof UserTask){
                UserTask userTask = (UserTask) flowElement;
                userTasks.add(userTask);
            }
            if (flowElement instanceof ParallelGateway){
                ParallelGateway parallelGateway = (ParallelGateway) flowElement;
                parallelGateways.add(parallelGateway);
            }

           if(flowElement instanceof ExclusiveGateway){
               System.out.println("流程包含 排他网关");
           }

           if (flowElement instanceof InclusiveGateway){
               System.out.println("流程包含 包容网关");
           }


        }

        System.out.println(parallelGateways);

        if (parallelGateways.size() > 0){

            List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstanceId).list();
            List<String> currentExecutionIds = new ArrayList<>();

            for (Execution execution : executions) {
                System.out.println("并行网关节点数："+execution.getActivityId());
                currentExecutionIds.add(execution.getId());
            }

/*          执行退回到并行网关需要下面的程序
            runtimeService.createChangeActivityStateBuilder()
                    .moveExecutionsToSingleActivityId(currentExecutionIds, targetTaskKey)
                    .changeState();*/

            return Result.success();
        }

        List<Map<String,String>> selectList = new ArrayList<>();
        for (UserTask userTask : userTasks) {
            List<SequenceFlow> incomingFlows = userTask.getIncomingFlows();

            for (SequenceFlow incomingFlow : incomingFlows) {
                Map<String, String> map = new HashMap<>(10);
                String sourceId = incomingFlow.getSourceRef();
                String sourceName = process.getFlowElement(sourceId).getName();
                //移除Name为空的节点（start节点名字经常为空）；
                if (sourceName == null|| "".equals(sourceName)){
                    continue;
                }
                map.put("value",sourceId);
                map.put("key",sourceName);
                selectList.add(map);
            }
        }
        List<Map<String, String>> setList = ListUtils.removeDuplicates(selectList);

        return Result.success(setList);

    }

    @Override
    public Result executeReturn(String processInstanceId, String targetNodeId){
        List<String> activityIds = findActivityIdsByInstanceId(processInstanceId);

        if (activityIds.size() == 0){
            return Result.failure(ResultCode.RESULT_DATA_NONE);
        }

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstanceId)
                .moveActivityIdsToSingleActivityId(activityIds,targetNodeId)
                .changeState();

        return Result.success();
    }


    /*#############################自定义方法区#############################*/

    /**
     * 根据执行实例Id找到 当前活动节点Ids
     * @param processInstanceId 执行实例Id
     * @return 当前活动节点List
     */
    private List<String> findActivityIdsByInstanceId(String processInstanceId){
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
        //获取当前活动节点
        List<String> activityIds = new ArrayList<>();
        for (Execution execution : executions) {
            activityIds.add(execution.getActivityId());
            //移除掉开始节点
            activityIds.remove(null);
        }
        System.out.println(activityIds);
        return activityIds;
    }

    /**
     * 根据任务ID返回流程实例Ids
     * @param taskId 任务Id
     * @return 流程实例Id
     */
    private String findInstanceIdByTaskId(String taskId){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
       return task.getProcessInstanceId();
    }

    /**
     * 根据流程实例Id找到对应的流程定义Id
     * @param processInstanceId 流程执行Id
     * @return 流程定义Id
     */
    private String findDefinitionIdByInstanceId(String processInstanceId){
        //获取流程定义Id,一个流程执行实例Id只会对应一个流程定义Id
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).list().get(0);
        return task.getProcessDefinitionId();
    }

    /**
     * 根据流程定义Id查询Process对象
     * @param processDefinitionId 流程定义Id
     * @return process
     */
    private Process findProcessByProcessDefinitionId(String processDefinitionId){
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        return bpmnModel.getMainProcess();
    }

    /**
     * 根据流程实例Id判断流程是否存在和挂起状态，返回自定义Result
     *
     * @param processInstanceId 流程实例Id
     * @return Result
     */
    private Result checkProcessStatusByProcessInstanceId(String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        //首先判断任务是否存在
        if (tasks == null || tasks.size() == 0) {
            return Result.failure(ResultCode.RESULT_DATA_NONE);
        }
        //再判断是不是多实例任务，如果是将全部的执行实例返回给上一层
        if (tasks.size() > 1) {
            return Result.failure(ResultCode.TASK_TYPE_MULTIPLE_INSTANCES);
        }
        return Result.success(tasks.get(0).isSuspended());

    }

    /**
     * 根据任务Id，判断任务状态，返回自定义Result
     *
     * @param taskId 任务Id
     * @return Result
     */
    private Result checkTaskStatus(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return Result.failure(ResultCode.RESULT_DATA_NONE);
        }
        if (task.isSuspended()) {
            return Result.failure(ResultCode.PROCESS_IS_SUSPENDED);
        }
        return Result.success();

    }
}
