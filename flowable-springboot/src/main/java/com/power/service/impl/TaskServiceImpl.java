package com.power.service.impl;

import com.power.entity.PowerTask;
import com.power.mapper.TaskMapper;
import com.power.service.PowerTaskService;
import com.power.util.Result;
import com.power.util.ResultCode;
import org.checkerframework.checker.units.qual.A;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
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
import java.util.List;
import java.util.Map;

/**
 * @author : xuyunfeng
 * @date :   2019/7/19 16:05
 */
@Service
public class TaskServiceImpl implements PowerTaskService {

    private final static Integer SUCCESS_CODE = 200;

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
    private TaskMapper taskMapper;


    @Override
    public Result suspendProcessInstanceById(String processInstanceId) {

        Result result = checkProcessStatusByProcessInstanceId(processInstanceId);
        if (!result.getCode().equals(SUCCESS_CODE)) {
            return result;
        }

        if (result.getData().equals(false)) {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            return Result.success();
        }
        return Result.failure(ResultCode.PROCESS_IS_SUSPENDED);
    }

    @Override
    public Result activateProcessInstanceById(String processInstanceId) {
        Result result = checkProcessStatusByProcessInstanceId(processInstanceId);

        if (!result.getCode().equals(SUCCESS_CODE)) {
            return result;
        }

        if (result.getData().equals(true)) {
            runtimeService.activateProcessInstanceById(processInstanceId);
            return Result.success();
        }
        return Result.failure(ResultCode.PROCESS_IS_ACTIVATED);
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

        System.out.println(executions);

        //得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }

        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
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
    public Result queryCurrentUserTasks() {

        User user = (User) session.getAttribute("user");

        if (user == null || user.getId() == null) {
            return Result.failure(ResultCode.USER_NOT_EXIST);
        }
        String userId = user.getId();

        List<PowerTask> tasks = taskMapper.queryUserTask(userId);

        if (tasks == null || tasks.size() == 0) {
            return Result.failure(ResultCode.TASKS_IS_NULL);
        }

        return Result.success(tasks);
    }

    @Override
    public Result completeTask(String taskId, String assignee, Map<String, Object> vars) {
        Result taskStatus = checkTaskStatus(taskId);
        if (!taskStatus.getCode().equals(SUCCESS_CODE)) {
            return taskStatus;
        }
        taskService.complete(taskId, vars);
        return Result.success();
    }

    /**
     * 根据流程实例Id判断流程是否存在和挂起状态，返回自定义Result
     * @param processInstanceId 流程实例Id
     * @return Result
     */
    private Result checkProcessStatusByProcessInstanceId(String processInstanceId) {
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if (task == null) {
            return Result.failure(ResultCode.RESULT_DATA_NONE);
        }
        return Result.success(task.isSuspended());
    }

    /**
     * 根据任务Id，判断任务状态，返回自定义Result
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
