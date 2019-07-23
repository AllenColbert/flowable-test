package com.power.controller;


import com.power.cmd.PowerJumpCmd;
import com.power.entity.PowerDeployEntity;
import com.power.entity.PowerDeployment;
import com.power.entity.PowerProcdef;
import com.power.service.PowerProcessService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
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
    public ResponseEntity<List<com.power.entity.PowerProcdef>> ProcedefList() {
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
     * 测试动态设置 assignee
     * 注意
     *
     * @param procDefId 流程定义Id
     * @return 标记
     */
    @GetMapping("runMultiInstance4/{procDefId}")
    public ResponseEntity runMultiInstance4(@PathVariable String procDefId) {
        String[] strings = {"ZhangSan", "LiSi", "WangWu"};
        List<String> list = Arrays.asList(strings);

        Map<String, Object> vars = new HashMap<>();
        vars.put("assigneeList", list);

        Object result = powerProcessService.startProcessInstance(procDefId, vars);
        //TODO 通过流程定义key启动流程 -- 启动失败，根据key找不到对应的流程实例；

        // Object result = powerProcessService.startProcessInstanceByKey(procDefKey, vars);

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
        // String [] strings = {"ZhangSan","LiSi"};
        Map<String, Object> vars = new HashMap<>();
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
}
