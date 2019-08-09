package com.power.controller;

import com.power.service.PowerModelService;
import com.power.util.Result;
import org.flowable.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * model控制
 * @author : xuyunfeng
 * @date :   2019/8/9 13:05
 */
@Controller
@RequestMapping("model")
public class ModelController {

    @Autowired
    private PowerModelService powerModelService;

    @GetMapping("addSingleNode")
    @ResponseBody
    public Result addSingleNode(@RequestParam String processDefinitionId,
                                @RequestBody UserTask userTask){
        return powerModelService.addSingleNode(processDefinitionId,userTask);
    }

    @GetMapping("userTaskView")
    @ResponseBody
    public Result userTaskView(@RequestParam String processDefinitionId,String activityId){
        return powerModelService.userTaskView(processDefinitionId,activityId);
    }

}
