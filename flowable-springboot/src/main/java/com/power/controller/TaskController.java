package com.power.controller;

import com.power.service.PowerTaskService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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
    private PowerTaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HttpSession session;

    @GetMapping("queryTask")
    public ResponseEntity findMyTask(
            @RequestParam("assignee") String assignee) {
        Object result = taskService.queryUserTask(assignee);
        return ResponseEntity.ok(result);
    }

    /**
     * @return 结合Session，直接查询当前登陆用户的任务
     */
    @GetMapping("myTask")
    public ResponseEntity myTask() {
        User user = (User) session.getAttribute("user");
        Object result = taskService.queryUserTask(user.getId());
        return ResponseEntity.ok(result);

    }


    @GetMapping("completeTask")
    public ResponseEntity completeTask(@RequestParam String taskId,
                                       @RequestParam(value = "assignee", required = false, defaultValue = "admin") String assignee) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("userId", assignee);
        Object result = taskService.completeTask(taskId, vars);
        return ResponseEntity.ok(result);
    }


    /**
     * 根据实例查询，实例结束则查询绘制的流程图
     *
     * @param processInstanceId 流程实例Id
     * @throws IOException Io流报错
     */
    @GetMapping("showImage")
    public void showActivityImageDetailPage(String processInstanceId) throws IOException {
        //TODO 逻辑有问题 设值之后又清空，怎么写出来的？
        String processDefinitionId = "";
        List<String> highLightedActivities = new ArrayList<String>();
        //TODO 怎么获取当前执行中的流程线集合？
        List<String> highLightedFlows = new ArrayList<String>();

        Task task = taskService.queryTaskByProcessInstanceId(processInstanceId);
        if (task == null) {
            HistoricProcessInstance hp = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            processDefinitionId = hp.getProcessDefinitionId();
        } else {
            processDefinitionId = task.getProcessDefinitionId();
            highLightedActivities.add(task.getTaskDefinitionKey());

        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        DefaultProcessDiagramGenerator defaultProcessDiagramGenerator = new DefaultProcessDiagramGenerator();

        InputStream in = defaultProcessDiagramGenerator
                .generateDiagram(bpmnModel, "PNG", "宋体", "宋体", "宋体", null, 1.0D, true);

        //TODO 动态生成到项目目录下
        File file = new File("C:\\FFOutput\\test2.png");
        OutputStream out = new FileOutputStream(file, true);
        IOUtils.copy(in, out);
    }

}
