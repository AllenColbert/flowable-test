package com.power.controller;


import com.alibaba.fastjson.JSON;
import com.power.service.PowerTaskService;
import com.power.util.Result;
import com.power.util.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 16:36
 * 任务模块
 */

@Api(value = "任务接口",tags = {"任务控制接口"})
@Controller
@RequestMapping("task")
public class TaskController {

    @Autowired
    private PowerTaskService powerTaskService;


    @ApiOperation(value ="结合Session，直接查询当前登陆用户的任务")
    @ApiResponse(code = 200,message = "我的任务列表")
    @GetMapping("userTask")
    public String userTask(Model model,HttpServletResponse response) {
        Result result = powerTaskService.queryCurrentUserTasks(model,response);
        if (!result.getCode().equals(ResultCode.SUCCESS.code())) {
            model.addAttribute("errorMsg", result.getMsg());
            return "errorPage";
        }
        return "taskList";
    }

    @ApiOperation(value ="查询流程图，返回inputStream")
    @GetMapping(value = "processDiagram")
    public void getProcessDiagram(HttpServletResponse httpServletResponse,
                                  @ApiParam(name = "processInstanceId",value ="流程实例Id") String processInstanceId) throws Exception {
        powerTaskService.getProcessDiagram(httpServletResponse, processInstanceId);
    }

    @ApiOperation(value ="挂起流程实例")
    @GetMapping("suspendProcessInstanceById")
    @ResponseBody
    public Result suspendProcessInstanceById(@ApiParam(name = "processInstanceId",value ="流程实例Id") @RequestParam String processInstanceId) {
        return powerTaskService.suspendProcessInstanceById(processInstanceId);
    }

    @ApiOperation(value ="激活流程实例")
    @GetMapping("activateProcessInstanceById")
    @ResponseBody
    public Result activateProcessInstanceById(@ApiParam(name = "processInstanceId",value ="流程实例Id") @RequestParam String processInstanceId) {
        return powerTaskService.activateProcessInstanceById(processInstanceId);
    }

    @ApiOperation(value ="任意节点跳转操作 -CMD模式")
    @GetMapping("jump")
    @ResponseBody
    public Result jumpNode(@ApiParam(name = "taskId",value ="任务Id") @RequestParam String taskId,
                           @ApiParam(name = "targetNodeId",value ="目标节点Id") @RequestParam String targetNodeId) {

        return powerTaskService.nodeJumpCmd(taskId, targetNodeId);
    }

    @ApiOperation(value ="完成任务的时候提交数据")
    @GetMapping("completeTaskWithData")
    @ResponseBody
    public Result completeTaskWithData (@ApiParam(name = "taskId",value ="任务Id") @RequestParam String taskId,
                                        @ApiParam(name = "formData",value ="表单数据") @RequestParam String formData){
        Map<String,Object> vars = (Map<String,Object>) JSON.parse(formData);

       return powerTaskService.completeTask(taskId,vars);
    }

    @ApiOperation(value ="点击 退回 时反馈给前端的数据 当前节点 & 可退回节点 & 。。。")
    @GetMapping("returnSourceNode")
    @ResponseBody
    public Result returnSourceNode(@ApiParam(name = "processInstanceId",value ="流程实例Id") @RequestParam String processInstanceId){
        return  powerTaskService.returnSourceNode(processInstanceId);
    }


    @ApiOperation(value ="执行退回操作")
    @GetMapping("executeReturn")
    @ResponseBody
    public Result executeReturn(@ApiParam(name = "processInstanceId",value ="流程实例Id") @RequestParam String processInstanceId,
                                @ApiParam(name = "targetNodeId",value ="目标节点Id") @RequestParam String targetNodeId){
        return powerTaskService.executeReturn(processInstanceId,targetNodeId);
    }

    @ApiOperation(value ="查询当前节点流出条件")
    @GetMapping("findConditionExpression")
    @ResponseBody
    public Result findConditionExpression(@ApiParam(name = "taskId",value ="任务Id") @RequestParam String taskId){
        return powerTaskService.findConditionExpression(taskId);
    }

    @ApiOperation(value ="检验表单是否存在")
    @GetMapping("checkFormExist")
    @ResponseBody
    public Result checkFormExist(@ApiParam(name = "taskId",value ="任务Id") @RequestParam String taskId){
        return powerTaskService.checkFormExist(taskId);
    }

    @ApiOperation(value ="检验表单是否存在")
    @GetMapping("showForm")
    @ResponseBody
    public Result showForm(@ApiParam(name = "taskId",value ="任务Id") @RequestParam String taskId){
        return powerTaskService.showForm(taskId);
    }

    @ApiIgnore()
    @Deprecated
    @PostMapping("completeTask")
    @ResponseBody
    public Result completeTask(@RequestParam String taskId,
                               @RequestParam(value = "assignee", required = false, defaultValue = "admin") String assignee) {
        Map<String, Object> vars = new HashMap<>(16);
        //这里还有完成任务，添加评论，表单填写等功能，还没想好怎么写，暂时先放着
        vars.put("money",1800);
        vars.put("send_apply",true);
        vars.put("approve",true);
        return powerTaskService.completeTask(taskId,assignee,vars);
    }

}
