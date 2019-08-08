package com.power.controller;


import com.power.service.PowerTaskService;
import com.power.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 16:36
 * 任务模块
 */

@Controller
@RequestMapping("task")
public class TaskController extends BaseController{

    @Autowired
    private PowerTaskService powerTaskService;

    /**
     * 结合Session，直接查询当前登陆用户的任务
     * 因为可能需要返回两个页面，所以在Controller上写了一些逻辑代码
     * @return 任务列表页面 or 错误信息页面
     */
    @GetMapping("userTask")
    public String userTask(Model model,HttpServletResponse response) {

        Result result = powerTaskService.queryCurrentUserTasks(model,response);
        if (!result.getCode().equals(SUCCESS_CODE)) {
            model.addAttribute("errorMsg", result.getMsg());
            return "errorPage";
        }
        return "taskList";
    }

    /**
     * 完成当前任务
     * @param taskId   任务Id
     * @param assignee 任务代理人
     * @return 完成标记
     */
    @PostMapping("completeTask")
    @ResponseBody
    public Result completeTask(@RequestParam String taskId,
                               @RequestParam(value = "assignee", required = false, defaultValue = "admin") String assignee) {
        Map<String, Object> vars = new HashMap<>(255);
        //这里还有完成任务，添加评论，表单填写等功能，还没想好怎么写，暂时先放着
        vars.put("userId", assignee);
        vars.put("money",1800);
        vars.put("send_apply",true);
        vars.put("assignee","ZhangSan");
        vars.put("approve",true);
        return powerTaskService.completeTask(taskId,assignee,vars);
    }

    /**
     * 直接在浏览器上显示当前任务图
     *
     * @param httpServletResponse Response
     * @param processInstanceId   processInstanceId流程Id
     * @throws Exception IOException
     */
    @GetMapping(value = "processDiagram")
    public void getProcessDiagram(HttpServletResponse httpServletResponse, String processInstanceId) throws Exception {
        powerTaskService.getProcessDiagram(httpServletResponse, processInstanceId);
    }

    /**
     * 挂起流程实例操作·
     *
     * @param processInstanceId 流程实例Id
     * @return Result
     */
    @GetMapping("suspendProcessInstanceById")
    @ResponseBody
    public Result suspendProcessInstanceById(@RequestParam String processInstanceId) {
        return powerTaskService.suspendProcessInstanceById(processInstanceId);
    }

    /**
     * 激活流程操作
     *
     * @param processInstanceId 流程实例Id
     * @return Result
     */
    @GetMapping("activateProcessInstanceById")
    @ResponseBody
    public Result activateProcessInstanceById(@RequestParam String processInstanceId) {
        return powerTaskService.activateProcessInstanceById(processInstanceId);
    }

    /**
     * 任意节点跳转操作 Flowable 6.3--- Cmd模式
     * 这里只是在普通节点之间跳转；多实例节点跳转到普通节点会出问题
     * (又测了几次，好像多实例节点也能跑的通ε=ε=ε=(~￣▽￣)~)
     *
     * @param taskId        当前任务节点ID    表act_ru_task中的ID；
     * @param targetNodeId  目标节点id   已部署的流程文件中的 <userTask id="shareniu-b"/> 标签中的Id；
     * @return Result
     */
    @GetMapping("jump")
    public Result jumpNode(@RequestParam String taskId,
                           @RequestParam String targetNodeId) {
        return powerTaskService.nodeJumpCmd(taskId, targetNodeId);
    }


}
