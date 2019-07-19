package com.power.controller;

import com.power.service.PowerTaskService;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
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

}
