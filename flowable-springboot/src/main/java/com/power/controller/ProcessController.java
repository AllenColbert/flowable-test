package com.power.controller;


import com.power.cmd.GetProcessCmd;
import com.power.cmd.PowerJumpCmd;
import com.power.entity.PowerDeployEntity;
import com.power.entity.PowerDeployment;
import com.power.entity.PowerProcdef;
import com.power.service.PowerProcessService;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.runtime.Execution;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuyunfeng
 * @date 2019/7/9 14:20
 */

@Controller
@RequestMapping("process")
public class ProcessController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private PowerProcessService powerProcessService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private HttpSession session;

    /**
     * 根据本地文件名部署流程（文件全名，默认匹配路径：resources/upload/diagrams/**）
     *
     * @param fileName    流程文件全名
     * @param powerDeploy 流程部署信息，name，key。。。 等属性
     * @return 流程部署对象 deploy
     */
    @GetMapping("deploy/{fileName}")
    public ResponseEntity<Object> deploy(@PathVariable String fileName,
                                         @RequestBody PowerDeployEntity powerDeploy) {
        Object result = powerProcessService.deployProcess(fileName, powerDeploy);

        return ResponseEntity.ok(result);
    }

    /**
     * 根据流程定义Id启动流程;
     *
     * @param procDefId 流程定义ID：processDefinitionId；
     * @return 流程执行ID
     */
    @GetMapping("runProcessById/{procDefId}")
    public ResponseEntity runProcessById(@PathVariable String procDefId) {
        Map<String, Object> vars = new HashMap<>();

        User user = (User) session.getAttribute("user");
        vars.put("userId", user.getId());

        Object result = powerProcessService.startProcessInstance(procDefId, vars);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询流程部署情况
     * 通过mybatis 创建SQL语句直接从数据库表 act_re_deployment 中查询
     * 将结果封装到自定义实体类  PowerDeployment
     * TODO Pageable分页
     *
     * @return 流程部署列表 deploymentList
     */
    @GetMapping("deploymentList")
    public ResponseEntity<List<PowerDeployment>> processList() {
        List<PowerDeployment> list = powerProcessService.findProcessList();
        return ResponseEntity.ok(list);
    }

    /**
     * 查询流程定义列表 在表 act_re_procdef 中
     *
     * @return 流程定义list
     */
    @GetMapping("procdefList")
    public ResponseEntity<List<com.power.entity.PowerProcdef>> procedefList() {
        List<PowerProcdef> list = powerProcessService.findProcdefList();
        return ResponseEntity.ok(list);
    }

    /**
     * 此处使用流程部署Id deploymentId；
     * 根据流程部署Id删除流程，级联删除
     *
     * @param deploymentId  流程部署Id
     * @param concatenation 是否开启级联删除，默认开启
     * @return 删除提示
     */
    @DeleteMapping("deleteProcessById/{deploymentId}")
    public ResponseEntity<String> deleteProcessById(@PathVariable String deploymentId,
                                                    @RequestParam(defaultValue = "true") Boolean concatenation) {
        repositoryService.deleteDeployment(deploymentId, concatenation);
        return ResponseEntity.ok("删除流程成功，Id：" + deploymentId);
    }

    /**
     * 测试包含多实例节点流程启动
     * 注意  此处任务执行人列表硬编码 只是为了测试方法
     *
     * @param procDefId 流程定义Id
     * @return 标记
     */
    @GetMapping("runTest/{procDefId}")
    public ResponseEntity runMultiInstance4(@PathVariable String procDefId) {
        String[] strings = {"ZhangSan", "LiSi", "WangWu"};
        List<String> list = Arrays.asList(strings);
        Map<String, Object> vars = new HashMap<>();

        vars.put("assigneeList", list);
        Object result = powerProcessService.startProcessInstance(procDefId, vars);

        return ResponseEntity.ok(result);
    }

    /**
     * 多实例节点加签操作
     *
     * @param activityId        待加签的多实例节点Id  act_ru_task表中的 TASK_DEF_KEY_
     * @param parentExecutionId 父任务Id act_ru_task表中的PROC_INST_ID_ 或 act_ru_execution中的ID_ where PARENT_ID_ == null;
     * @return execution_Id 执行实例Id；
     */
    @GetMapping("addMultiInstance")
    public ResponseEntity addMulti(@RequestParam String activityId,
                                   @RequestParam String parentExecutionId) {
        Map<String, Object> vars = new HashMap<>();
        // String [] strings = {"ZhangSan","LiSi"};
        //vars.put("assigneeList",Arrays.asList(strings));
        vars.put("assignee", "LiSi");

        Execution execution = runtimeService.addMultiInstanceExecution(activityId, parentExecutionId, vars);

        return ResponseEntity.ok(execution.getId());
    }

    /**
     * 多实例节点减签操作
     *
     * @param executionId 多实例节点任务ID
     * @return 标记
     */
    @GetMapping("deleteMultiInstance")
    public ResponseEntity deleteMulti(@RequestParam String executionId) {
        runtimeService.deleteMultiInstanceExecution(executionId, true);
        return ResponseEntity.ok("减签");
    }

    /**
     * 任意节点跳转操作
     * 这里只是在普通节点之间跳转；多实例节点跳转到普通节点会出问题
     *
     * @param taskId       当前任务节点ID act_ru_task 表中的ID；
     * @param targetNodeId 目标节点id 已部署的流程文件中的 <userTask id="shareniu-b"/> 标签中的Id；
     * @return 标记
     */
    @GetMapping("jump")
    public ResponseEntity jumpNode(@RequestParam String taskId,
                                   @RequestParam String targetNodeId) {
        managementService.executeCommand(new PowerJumpCmd(taskId, targetNodeId));
        return ResponseEntity.ok("跳转成功");
    }


    /**
     * 普通节点之间跳转操作
     * @param procDefId 流程实例Id
     * @param currentActivityId 当前节点id  流程标签中的id属性 <userTask id="xxx"/>
     * @param newActivityId 目标节点id
     * @return 标记
     */
    @GetMapping("jump2")
    public ResponseEntity jump2(@RequestParam String procDefId,
                                @RequestParam String currentActivityId,
                                @RequestParam String newActivityId){
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(procDefId)
                .moveActivityIdTo(currentActivityId,newActivityId)
                .changeState();

        return ResponseEntity.ok("跳转成功");
    }

    /**
     * 从多实例节点跳转到普通节点
     * @param executionId 执行实例Id
     * @param activityId 跳转目标Id   <userTask id="xxx"/>；
     * @return 标记
     */
    @GetMapping("jump3")
    public ResponseEntity jump3(@RequestParam String executionId,
                                @RequestParam String activityId){
        runtimeService.createChangeActivityStateBuilder()
                .moveExecutionToActivityId(executionId, activityId)
                .changeState();
        return ResponseEntity.ok("跳转成功");
    }
    /**
     * 根据流程实例ID 判断流程中任务节点的类型
     *
     * @param procDefId 流程实例ID
     * @return 标记
     */
    @GetMapping("validateTaskNodeType")
    public ResponseEntity validateTaskNodeType(@RequestParam String procDefId) {
        Process process = managementService.executeCommand(new GetProcessCmd(procDefId));

        List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);
        for (UserTask userTask : userTasks) {
            validate(userTask);
        }
        /*runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(task.getTaskDefinitionKey(), operationContext.getTargetNodeId())
                .processVariables(operationContext.getFormData()).changeState();*/

        return ResponseEntity.ok("标记");
    }

    private void validate(UserTask userTask) {
        Object behavior = userTask.getBehavior();
        if (behavior instanceof MultiInstanceActivityBehavior) {
            System.out.println(userTask.getId() + "是多实例任务节点");
        }
        if (behavior instanceof UserTaskActivityBehavior) {
            System.out.println(userTask.getId() + "是普通任务节点");
        }
    }
}
