package com.power.controller;

import com.power.entity.PowerTask;
import com.power.service.PowerTaskService;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 16:36
 * 任务模块
 */

@Controller
@RequestMapping("task")
public class TaskController {

    @Autowired
    private PowerTaskService powerTaskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HttpSession session;

    @Autowired
    private RuntimeService runtimeService;

    @Qualifier("processEngine")
    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private TaskService taskService;

    /**
     * 查询assignee的全部任务
     * @param assignee  任务代理人
     * @return 任务列表
     */
    @Deprecated
    @GetMapping("queryAllTask")
    public ResponseEntity queryAllTask(
            @RequestParam(value = "assignee",required = false,defaultValue = "admin") String assignee) {
        Object result = powerTaskService.queryAllTask(assignee);
        return ResponseEntity.ok(result);
    }

    /**
     * @return 结合Session，直接查询当前登陆用户的任务
     */
    @GetMapping("myTask")
    public String myTask(Model model) {
        User user = (User) session.getAttribute("user");

        if(user == null){
            String msg = "任务代理人不能为空";
            model.addAttribute("errorMsg",msg);
            return "errorPage";
        }
        List<PowerTask> result = powerTaskService.queryUserTask(user.getId());
        if (result == null || result.size()==0){
            String msg  = "当前用户没有任务";
            model.addAttribute("errorMsg",msg);
            return "errorPage";
        }

        model.addAttribute("taskList",result);
        return "taskList";

    }

    /**
     * 完成当前任务
     * 完成任务前需要检测一下任务是否被挂起
     * @param taskId 任务Id
     * @param assignee 任务代理人
     * @return 完成标记
     */
    @GetMapping("completeTask")
    public ResponseEntity completeTask(@RequestParam String taskId,
                                       @RequestParam(value = "assignee", required = false, defaultValue = "admin") String assignee) {
        Map<String, Object> vars = new HashMap<>(255);
        vars.put("userId", assignee);
        if (powerTaskService.checkTaskStatus(taskId)){
            return ResponseEntity.ok("任务已经被挂起");
        }

        Object result = powerTaskService.completeTask(taskId, vars);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据实例查询，实例结束则查询绘制的流程图，并保存在本地
     * 执行没问题，老是在结束的时候报错是什么鬼？
     * 跟拦截器有关--测一下
     *
     * @param processInstanceId 流程实例Id
     * @throws IOException Io流报错
     */
    @GetMapping("showImage")
    public ResponseEntity showActivityImageDetailPage(@RequestParam String processInstanceId) throws IOException {
        //通过流程实例Id查询当前执行中的任务对象，如果没有的话就去历史记录中查询，再通过返回结果查询流程定义Id
        //通过流程定义Id获取BpmnModel对象
        String processDefinitionId = "";
        List<String> highLightedActivities = new ArrayList<String>();
        //TODO 怎么获取当前执行的task中的流程线段？
        List<String> highLightedFlows = new ArrayList<String>();

        Task task = powerTaskService.queryTaskByProcessInstanceId(processInstanceId);
        if (task == null) {
            HistoricProcessInstance hp = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            processDefinitionId = hp.getProcessDefinitionId();
        } else {
            processDefinitionId = task.getProcessDefinitionId();
            highLightedActivities.add(task.getTaskDefinitionKey());
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        DefaultProcessDiagramGenerator defaultProcessDiagramGenerator =
                new DefaultProcessDiagramGenerator();

        InputStream in = defaultProcessDiagramGenerator
                .generateDiagram(bpmnModel, "PNG",highLightedActivities,highLightedFlows, "宋体", "宋体", "宋体", null, 1.0D, true);

        System.out.println(in);
        //TODO 如何修改路径让它动态生成到项目目录下
        File file = new File("C:\\FFOutput\\test1.png");
        FileOutputStream out = new FileOutputStream(file);
        byte [] b = new byte[1024];
        while (in.read(b) != -1) {
            out.write(b);
        }
        out.close();
        in.close();
        return ResponseEntity.ok("标记");
    }

    /**
     * 直接在浏览器上显示当前任务图
     * @param httpServletResponse Response
     * @param processInstanceId processInstanceId流程Id
     * @throws Exception IOException
     */
    @GetMapping(value = "processDiagram")
    public void genProcessDiagram(HttpServletResponse httpServletResponse, String processInstanceId) throws Exception {
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
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows,"宋体", "宋体", "宋体", null,1.0D,true);
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


    @GetMapping("suspendProcessInstanceById")
    public void suspendProcessInstanceById(@RequestParam String processInstanceId){
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    @GetMapping("activateProcessInstanceById")
    public void activateProcessInstanceById(@RequestParam String processInstanceId){
        runtimeService.activateProcessInstanceById(processInstanceId);
    }
}
