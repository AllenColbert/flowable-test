package com.power.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 16:36
 */

@Controller
@RequestMapping("task")
public class TaskController {

/*    @Autowired
    private PowerTaskService taskService;*/

    @GetMapping("myTask")
    public ResponseEntity findMyTask(
            @RequestParam(name = "assignee",required = false,defaultValue ="null") String assignee){
        return ResponseEntity.ok(null);    }


}
