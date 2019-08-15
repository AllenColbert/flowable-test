package com.power.service.impl;

import com.power.cmd.NodeJumpCmd;
import com.power.entity.PowerTask;
import com.power.mapper.TaskMapper;
import com.power.service.PowerTaskService;
import com.power.util.ListUtils;
import com.power.util.Result;
import com.power.util.ResultCode;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.*;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
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
import java.util.*;

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
    private HistoryService historyService;
    @Autowired
    private FormService formService;
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
        if (vars.get("message") != null && vars.get("userId")!= null) {
            String userId = vars.get("userId").toString();
            String commentMsg = vars.get("message").toString();
            Comment comment = taskService.addComment(taskId, processInstanceId, commentMsg);
            comment.setUserId(userId);
            taskService.saveComment(comment);
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
    public Result returnSourceNode(String processInstanceId) {
        //通过流程实例Id获取当前活动节点列表
        List<String> activityIds = findActivityIdsByInstanceId(processInstanceId);
        //获取process
        Process process = findProcessByProcessInstanceId(processInstanceId);
        List<UserTask> userTasks = new ArrayList<>();
        //只获取 List<UserTask>，且一般来说当前活动节点都不会是网关的
        for (String activityId : activityIds) {
            FlowElement flowElement = process.getFlowElement(activityId);
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                userTasks.add(userTask);
            }
        }
        //返回该节点所有的流入流程
        List<Map<String, String>> selectList = new ArrayList<>();
        for (UserTask userTask : userTasks) {
            List<SequenceFlow> incomingFlows = userTask.getIncomingFlows();
            for (SequenceFlow incomingFlow : incomingFlows) {
                Map<String, String> map = new HashMap<>(10);
                String sourceId = incomingFlow.getSourceRef();
                String sourceName = process.getFlowElement(sourceId).getName();
                //移除Name为空的节点（start节点名字经常为空）；
                if (sourceName == null || "".equals(sourceName)) {
                    continue;
                }
                map.put("value", sourceId);
                map.put("key", sourceName);
                selectList.add(map);
            }
        }
        //去重
        List<Map<String, String>> setList = ListUtils.removeDuplicates(selectList);

        return Result.success(setList);

    }

    @Override
    public Result executeReturn(String processInstanceId, String targetNodeId) {
        List<String> activityIds = findActivityIdsByInstanceId(processInstanceId);
        //获取process
        Process process = findProcessByProcessInstanceId(processInstanceId);
        //获取到flowElement 流程元素对象
        FlowElement flowElement = process.getFlowElement(targetNodeId);
        //如果目标节点是一般的用户任务可以直接退回
        if (flowElement instanceof UserTask) {
            runtimeService.createChangeActivityStateBuilder().processInstanceId(processInstanceId)
                    .moveActivityIdsToSingleActivityId(activityIds, targetNodeId).changeState();
            return Result.success();
        }
        //如果是并行网关需要再进行判断
        if (flowElement instanceof ParallelGateway) {
            ParallelGateway parallelGateway = (ParallelGateway) flowElement;
            List<SequenceFlow> incomingFlows = parallelGateway.getIncomingFlows();
            //如果流入并行网关只有一条，说明它是并行网关开始节点，仅需要退回到上一个节点
            if (incomingFlows.size() == 1) {
                List<String> currentExecutionIds = new ArrayList<>();
                List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstanceId).list();
                for (Execution execution : executions) {
                    currentExecutionIds.add(execution.getId());
                }

                String targetNode = incomingFlows.get(0).getSourceRef();
                runtimeService.createChangeActivityStateBuilder()
                        .moveExecutionsToSingleActivityId(currentExecutionIds, targetNode)
                        .changeState();
                return Result.success();
            }
            //如果有多条流入，说明它是并行网关结束节点，需要退回到全部的并行节点
            else {
                List<String> targetTaskIds = new ArrayList<>();
                for (SequenceFlow incomingFlow : incomingFlows) {
                    String sourceNode = incomingFlow.getSourceRef();
                    targetTaskIds.add(sourceNode);
                }
                //并行网关流出只会有一个节点
                runtimeService.createChangeActivityStateBuilder()
                        .processInstanceId(processInstanceId)
                        .moveSingleActivityIdToActivityIds(activityIds.get(0), targetTaskIds)
                        .changeState();

                return Result.success();
            }
        }
        //如果节点是包容网关
        if (flowElement instanceof InclusiveGateway) {
            InclusiveGateway inclusiveGateway = (InclusiveGateway) flowElement;
            System.out.println(inclusiveGateway);
            return Result.success();
        }
        //如果节点是排他网关
        if (flowElement instanceof ExclusiveGateway) {
            ExclusiveGateway exclusiveGateway = (ExclusiveGateway) flowElement;
            List<SequenceFlow> incomingFlows = exclusiveGateway.getIncomingFlows();

            List<String> targetTaskIds = new ArrayList<>();
            for (SequenceFlow incomingFlow : incomingFlows) {
                String sourceRef = incomingFlow.getSourceRef();
                targetTaskIds.add(sourceRef);
            }
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(processInstanceId)
                    .moveSingleActivityIdToActivityIds(activityIds.get(0), targetTaskIds)
                    .changeState();

            return Result.success();
        }

        return Result.failure(ResultCode.SYSTEM_INNER_ERROR);

    }

    @Override
    public Result findConditionExpression(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String currentNodeId = task.getTaskDefinitionKey();
        Process process = findProcessByProcessInstanceId(task.getProcessInstanceId());
        FlowElement flowElement = process.getFlowElement(currentNodeId);

       // Process process = ProcessDefinitionUtil.getProcess(processDefinitionId)
        Set<String> conditions = new HashSet<>();
        List<String> targetNodes = new ArrayList<>();

        //获取当前节点的输出流条件表达式和输出节点
        UserTask currentTask = (UserTask) flowElement;
        List<SequenceFlow> outgoingFlows = currentTask.getOutgoingFlows();
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            String conditionExpression = outgoingFlow.getConditionExpression();
            if (!"".equals(conditionExpression)&& conditionExpression != null){
                conditions.add(conditionExpression);
            }
            String targetRef = outgoingFlow.getTargetRef();
            targetNodes.add(targetRef);
        }

        //遍历所有的目标节点，获取流出条件
        for (String targetNode : targetNodes) {
            FlowElement targetFlowElement = process.getFlowElement(targetNode);
            //如果下一个节点是用户任务就不用管了，当前节点到下一节点的流出条件已经被获取了
            if (targetFlowElement instanceof UserTask){
                List<FormProperty> formProperties = ((UserTask) targetFlowElement).getFormProperties();
                System.out.println(formProperties);
                System.out.println(targetFlowElement.getName());
            }
            //如果下一节点是网关类型的，要获取此网关所有的流出条件
            if (targetFlowElement instanceof Gateway){
                List<SequenceFlow> gatewayOutgoingFlows = ((Gateway) targetFlowElement).getOutgoingFlows();
                //遍历流出线路
                for (SequenceFlow outgoingFlow :gatewayOutgoingFlows) {
                    //取出流出条件
                    String conditionExpression = outgoingFlow.getConditionExpression();
                    //排除掉空的条件
                    if (!"".equals(conditionExpression)&& conditionExpression != null){
                        conditions.add(conditionExpression);
                    }
                }
            }
            //如果目标节点是结束节点，也可以直接结束了
            if (targetFlowElement instanceof EndEvent){
                System.out.println("endEvent："+((EndEvent) targetFlowElement).getEventDefinitions());
            }
        }

        return Result.success(parseData(conditions));
    }

    @Override
    public Result checkFormExist(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String formKey = task.getFormKey();
        if (formKey == null || "".equals(formKey)){
            return Result.failure(ResultCode.RESULT_DATA_NONE);
        }
        return Result.success(formKey);
    }

    @Override
    public Result showForm(String taskId) {
        Object taskForm = formService.getRenderedTaskForm(taskId);

        if (taskForm == null || "".equals(taskForm)){return Result.failure(ResultCode.RESULT_DATA_NONE);}

        return Result.success(taskForm);
    }

    /*#############################自定义方法区#############################*/

    /**
     * 根据执行实例Id找到 当前活动节点Id list
     *
     * @param processInstanceId 执行实例Id
     * @return 当前活动节点List
     */
    private List<String> findActivityIdsByInstanceId(String processInstanceId) {
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
     * 根据任务ID返回流程实例Id
     *
     * @param taskId 任务Id
     * @return 流程实例Id
     */
    private String findInstanceIdByTaskId(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        return task.getProcessInstanceId();
    }

    /**
     * 根据流程执行Id查询Process对象
     * @param processInstanceId 流程执行Id
     * @return process
     */
    private Process findProcessByProcessInstanceId(String processInstanceId) {
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).list().get(0);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
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

    private List<String> parseData(Set<String> conditions){
     List<String> strings = new ArrayList<>();
        for (String conditionExpression : conditions) {

            String[] split = conditionExpression.split("\\{")[1]
                    .split("=")[0].split(">")[0].split("<")[0].split(" ");
            String s = split[0];
            strings.add(s);
        }
        HashSet<String> set = new HashSet<>(strings);
        List<String> conditionList = new ArrayList<>(set);
        System.out.println(conditionList);
        return conditionList;
    }


}
