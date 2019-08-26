package com.power.controller;


import com.power.cmd.GetProcessCmd;
import com.power.cmd.GetProcessDefinitionCacheEntryCmd;
import com.power.entity.PowerDeployEntity;
import com.power.service.PowerProcessService;
import com.power.util.Result;
import com.power.util.ResultCode;
import io.swagger.annotations.*;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.engine.runtime.Execution;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author xuyunfeng
 * @date 2019/7/9 14:20
 */
@Api(value = "流程控制类",tags = {"流程控制接口"})
@Controller
@RequestMapping("process")
public class ProcessController {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private PowerProcessService powerProcessService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private HttpSession session;


    @ApiOperation(value = "查看流程模型列表（暂未做分页）")
    @GetMapping("modelList")
    public String processModelList(Model model){

        Result result =  powerProcessService.queryProcessModelList();

        if (!result.getCode().equals(ResultCode.SUCCESS.code())){
            model.addAttribute("errorMsg",result.getMsg());
            return "errorPage";
        }

        model.addAttribute("models",result.getData());

        return  "modelList";
    }


    @ApiOperation(value = "显示流程定义列表（暂未做分页）")
    @GetMapping("processList")
    public String processList(Model model){
        Result result =  powerProcessService.queryProcessDefinitionList();

        if (!result.getCode().equals(ResultCode.SUCCESS.code())){
            model.addAttribute("errorMsg",result.getMsg());
            return "errorPage";
        }

        model.addAttribute("processDefinitionList",result.getData());

        return "processList";
    }


    @ApiOperation(value = "根据流程定义Id启动流程,默认当前登陆用户为流程启动人")
    @PostMapping("startProcessById")
    @ResponseBody
    public Result startProcessById(@ApiParam(name = "processDefinitionId",value ="流程定义ID") @RequestParam String processDefinitionId) {
       return powerProcessService.startProcessInstanceById(processDefinitionId);
    }


    @ApiOperation(value = "根据流程部署Id删除流程，默认开启级联删除")
    @DeleteMapping("deleteProcessById")
    @ResponseBody
    public Result deleteProcessById(@ApiParam(name = "deploymentId",value ="流程部署Id") @RequestParam String deploymentId,
                                    @ApiParam(name = "concatenation",value ="是否开启级联删除") @RequestParam(required = false,defaultValue = "true") Boolean concatenation) {
      return powerProcessService.deleteProcessByDeploymentId(deploymentId,concatenation);
    }


    @ApiOperation(value = "通过流程定义Id挂起整个流程")
    @GetMapping("suspendProcessByProcessDefinitionId")
    @ResponseBody
    public Result suspendProcessByProcessDefinitionId(@ApiParam(name = "processDefinitionId",value ="流程定义Id") @RequestParam String processDefinitionId,
                                                      @ApiParam(name = "suspendProcessInstances",value ="是否挂起所有的流程实例") @RequestParam(defaultValue = "true", required = false) Boolean suspendProcessInstances,
                                                      @ApiParam(name = "suspensionDate",value ="流程挂起的日期，为null时立即挂起") @RequestParam(required = false)Date suspensionDate){
        return powerProcessService.suspendProcessByProcessDefinitionId(processDefinitionId,suspendProcessInstances,suspensionDate);
    }


    @ApiOperation(value = "通过流程定义Id激活整个流程")
    @GetMapping("activateProcessByProcessDefinitionId")
    @ResponseBody
    public Result activateProcessByProcessDefinitionId(@ApiParam(name = "processDefinitionId",value ="流程定义Id") @RequestParam String processDefinitionId,
                                                       @ApiParam(name = "suspendProcessInstances",value ="是否激活所有的流程实例") @RequestParam(defaultValue = "true", required = false) Boolean suspendProcessInstances,
                                                       @ApiParam(name = "suspensionDate",value ="流程激活的日期，为null时立即激活") @RequestParam(required = false)Date suspensionDate){
        return powerProcessService.activateProcessByProcessDefinitionId(processDefinitionId,suspendProcessInstances,suspensionDate);
    }

    @ApiOperation(value = "根据流程模型Id部署流程")
    @GetMapping("deployModelById")
    @ResponseBody
    public Result deployModelByModelId(@ApiParam(name = "modelId",value ="流程模型Id") @RequestParam String modelId){
       return powerProcessService.deployModelByModelId(modelId);
    }

    @ApiOperation(value = "根据流程模型Id删除流程模型")
    @GetMapping("deleteModelById")
    @ResponseBody
    public Result deleteModelById(@ApiParam(name = "modelId",value ="流程模型Id") @RequestParam String modelId){
        return powerProcessService.deleteModelById(modelId);
    }

    @ApiOperation(value = "查看process--JSON格式")
    @GetMapping("showProcess")
    @ResponseBody
    public Result showProcess(@ApiParam(name = "processDefinitionId",value ="流程定义Id") @RequestParam String processDefinitionId){
        return powerProcessService.showProcess(processDefinitionId);
    }

    @ApiOperation(value = "查看Model--JSON格式")
    @GetMapping("showModel")
    @ResponseBody
    public Result showModel(@ApiParam(name = "modelId",value ="流程模型Id") @RequestParam String modelId){
        return powerProcessService.showModel(modelId);
    }

//#########################################未重构代码##############################################

    /**
     * 多实例节点加签操作
     *
     * @param activityId        待加签的多实例节点Id  act_ru_task表中的 TASK_DEF_KEY_
     * @param parentExecutionId 父任务Id act_ru_task表中的PROC_INST_ID_ 或 act_ru_execution中的ID_ where PARENT_ID_ == null;
     * @return execution_Id     多实例节点任务ID；
     */
    @ApiIgnore()
    @GetMapping("addMultiInstanceNode")
    public ResponseEntity addMulti(@RequestParam String activityId,
                                   @RequestParam String parentExecutionId) {
        Map<String, Object> vars = new HashMap<>(16);
        vars.put("assignee", "LiSi");

        Execution execution = runtimeService.addMultiInstanceExecution(activityId, parentExecutionId, vars);

        return ResponseEntity.ok("多实例节点任务ID："+execution.getId());
    }

    /**
     * 多实例节点减签操作
     *
     * @param executionId 多实例节点任务ID
     * @return 标记
     */
    @ApiIgnore()
    @GetMapping("deleteMultiInstanceNode")
    public ResponseEntity deleteMulti(@RequestParam String executionId) {
        runtimeService.deleteMultiInstanceExecution(executionId, true);
        return ResponseEntity.ok("完成多实例节点减签操作");
    }



    /**
     * 普通节点之间跳转操作，flowable 6.4更新后提供的方法
     * @param procInstanceId       流程实例Id  act_ru_task 表中的 PROC_INST_ID_字段
     * @param currentActivityId    当前节点id  流程标签中的id属性 <userTask id="xxx"/>
     * @param newActivityId        目标节点id
     * @return 标记
     */
    @ApiIgnore()
    @GetMapping("jump2")
    public ResponseEntity jump2(@RequestParam String procInstanceId,
                                @RequestParam String currentActivityId,
                                @RequestParam String newActivityId){
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(procInstanceId)
                .moveActivityIdTo(currentActivityId,newActivityId)
                .changeState();

        return ResponseEntity.ok("跳转成功");
    }

    /**
     * 从多实例节点跳转到普通节点  flowable 6.4更新后提供的方法
     * @param executionId 执行实例Id  act_ru_execution表中最上层执行实例的Id
     * @param activityId 跳转目标Id   <userTask id="xxx"/>标签中的节点Id
     * @return 标记
     */
    @ApiIgnore()
    @GetMapping("jump3")
    public ResponseEntity jump3(@RequestParam String executionId,
                                @RequestParam String activityId){
        runtimeService.createChangeActivityStateBuilder()
                .moveExecutionToActivityId(executionId, activityId)
                .changeState();
        return ResponseEntity.ok("跳转成功");
    }

    /**
     * 根据流程实例ID获取任务节点列表，并判断流程中任务节点的类型
     *
     * @param procDefId 流程实例ID
     * @return 标记
     */
    @ApiIgnore()
    @GetMapping("validateTaskNodeType")
    public ResponseEntity validateTaskNodeType(@RequestParam String procDefId) {
        Process process = managementService.executeCommand(new GetProcessCmd(procDefId));

        List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);
        List<String> list = new ArrayList<>();
        for (UserTask userTask : userTasks) {
            Object behavior = userTask.getBehavior();
            if (behavior instanceof MultiInstanceActivityBehavior) {
                list.add("任务Id为"+userTask.getId() + "的任务，是多实例节点");
            }
            if (behavior instanceof UserTaskActivityBehavior) {
                list.add("任务Id为"+userTask.getId() + "的任务，是普通节点");
            }
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 这里修改的是全局流程实例模板 -/.\-！
     * 一个流程实例被修改后，从他这里启动的所有流程都会被修改--
     * @param processDefinitionId 流程定义Id
     * @param targetNodeId 目标节点Id
     * @return xx
     */
    @ApiIgnore()
    @GetMapping("deleteNode")
    public ResponseEntity deleteNode(@RequestParam String processDefinitionId,
                                     @RequestParam String targetNodeId){
        Process process = managementService.executeCommand(new GetProcessCmd(processDefinitionId));
        //移除节点;
        process.removeFlowElementFromMap(targetNodeId);
        process.removeFlowElement(targetNodeId);
        //获取ProcessCache缓存管理对象
        ProcessDefinitionCacheEntry processCacheEntry = managementService
                .executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId));

        //设置缓存
        processCacheEntry.setProcess(process);

        return ResponseEntity.ok(process);
    }


    /**
     * 根据本地文件名部署流程（文件全名，默认匹配路径：resources/upload/diagrams/**）
     *
     * @param fileName    流程文件全名 如 test.bpmn20.xml 或者 test.bpmn
     * @param powerDeploy 流程部署信息，name，key。。。 等属性
    JSON示例：
    {
    "name":"XXX测试",
    "category":"测试类",
    "key":"XXX",
    "tenantId":"1",
    "outerForm":false,
    "formResource":["XXX","XXX","XXX"]
    }
     * @return 流程部署对象 deploy属性  or 其他提示信息;
     */
    @ApiIgnore()
    @GetMapping("deploy")
    public ResponseEntity<Object> deploy(@RequestParam String fileName,
                                         @RequestBody(required = false)PowerDeployEntity powerDeploy) {
        Object result = powerProcessService.deployProcess(fileName, powerDeploy);

        return ResponseEntity.ok(result);
    }


    /**
     * 根据流程定义key启动流程 失败 --tmd 为什么？
     * 参数问题： --目前来看跟参数没关系
     * @param processDefinitionKey 流程定义key
     * @return 标记
     */
    @ApiIgnore()
    @GetMapping("runNormalByKey")
    public ResponseEntity runNormalByKey(@RequestParam String processDefinitionKey){
        Map<String, Object> vars = new HashMap<>(16);

        User user = (User) session.getAttribute("user");
        vars.put("userId", user.getId());
        System.out.println(vars);


        Object result = powerProcessService.startProcessInstanceByKey(processDefinitionKey);

        return ResponseEntity.ok(result);
    }


}
