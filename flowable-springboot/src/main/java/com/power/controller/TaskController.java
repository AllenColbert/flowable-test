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

import javax.servlet.http.HttpServletRequest;
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
            @RequestParam("assignee") String assignee){
      Object result =   taskService.queryUserTask(assignee);
        return ResponseEntity.ok(result);
    }

    /**
     * @return  结合Session，直接查询当前登陆用户的任务
     */
    @GetMapping("myTask")
    public ResponseEntity myTask(){
        User user = (User) session.getAttribute("user");
        Object result = taskService.queryUserTask(user.getId());
        return ResponseEntity.ok(result);

    }


    @GetMapping("completeTask")
    public ResponseEntity completeTask(@RequestParam String taskId,
                                       @RequestParam("assignee")String assignee){
        Map<String, Object> vars = new HashMap<>();
        vars.put("userId",assignee);
        Object result = taskService.completeTask(taskId,vars);
        return ResponseEntity.ok(result);
    }


    /**
     * 根据实例查询，实例结束则查询绘制的流程图
     * @param response
     * @param request
     * @param processInstanceId 流程实例Id
     * @throws IOException
     */
    @GetMapping("showActivityedimage")
    public void showActivityedimageDetailPage(HttpServletResponse response, HttpServletRequest request, String processInstanceId) throws IOException {


        String processDefinitionId ="";
        List<String> highLightedActivities=new ArrayList<String>();
        //TODO 获取当前流程线列表，即可在图中高亮显示
        List<String> highLightedFlows=new ArrayList<String>();

        Task task = taskService.queryTaskByProcessInstanceId(processInstanceId);
        if (task==null) {
            HistoricProcessInstance hp = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            processDefinitionId=hp.getProcessDefinitionId();
        }else {
            processDefinitionId = task.getProcessDefinitionId();
            highLightedActivities.add(task.getTaskDefinitionKey());

        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        String activityFontName="宋体";
        String labelFontName="宋体";
        String annotationFontName="宋体";
        ClassLoader customClassLoader=null;
        double scaleFactor=1.0D;
        boolean drawSequenceFlowNameWithNoLabelDI=true;
        DefaultProcessDiagramGenerator defaultProcessDiagramGenerator = new 	DefaultProcessDiagramGenerator();

        InputStream in = defaultProcessDiagramGenerator.generateDiagram(bpmnModel,"PNG",activityFontName,labelFontName,annotationFontName,customClassLoader,scaleFactor,drawSequenceFlowNameWithNoLabelDI);
        //TODO 生成到指定目录下
        File file =new File("C:\\DemoSpace\\GitHub\\flowable-shareniu\\flowable-springboot\\src\\main\\resources\\upload\\html\\test.png");
        OutputStream out=new FileOutputStream(file,true);
       // OutputStream out= response.getOutputStream();
        IOUtils.copy(in, out);
    }

}
