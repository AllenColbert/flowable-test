package com.power.controller;

import com.power.service.PowerTaskService;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 16:36
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

    @GetMapping("queryAllTask")
    public ResponseEntity queryAllTask(
            @RequestParam(value = "assignee",required = false) String assignee) {
        Object result = powerTaskService.queryAllTask(assignee);
        return ResponseEntity.ok(result);
    }

    /**
     * @return 结合Session，直接查询当前登陆用户的任务
     */
    @GetMapping("myTask")
    public ResponseEntity myTask() {
        User user = (User) session.getAttribute("user");
        Object result = powerTaskService.queryUserTask(user.getId());

        return ResponseEntity.ok(result);

    }

    @GetMapping("completeTask")
    public ResponseEntity completeTask(@RequestParam String taskId,
                                       @RequestParam(value = "assignee", required = false, defaultValue = "admin") String assignee) {
        Map<String, Object> vars = new HashMap<>(255);
        vars.put("userId", assignee);
        Object result = powerTaskService.completeTask(taskId, vars);
        return ResponseEntity.ok(result);
    }


    /**
     * 根据实例查询，实例结束则查询绘制的流程图
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

}
