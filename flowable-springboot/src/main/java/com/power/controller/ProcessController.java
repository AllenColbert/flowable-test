package com.power.controller;


import com.power.cmd.GetProcessCmd;
import com.power.cmd.GetProcessDefinitionCacheEntryCmd;
import com.power.cmd.PowerJumpCmd;
import com.power.entity.PowerDeployEntity;
import com.power.entity.PowerDeployment;
import com.power.entity.PowerProcessDefinition;
import com.power.entity.PowerTask;
import com.power.service.PowerProcessService;
import org.flowable.bpmn.model.Artifact;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.engine.runtime.Execution;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author xuyunfeng
 * @date 2019/7/9 14:20
 */

@Controller
@RequestMapping("process")
public class ProcessController {

    @Qualifier("processEngine")
    @Autowired
    private ProcessEngine processEngine;

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
    @GetMapping("deploy/{fileName}")
    public ResponseEntity<Object> deploy(@PathVariable String fileName,
                                         @RequestBody PowerDeployEntity powerDeploy) {
        Object result = powerProcessService.deployProcess(fileName, powerDeploy);

        return ResponseEntity.ok(result);
    }

    /**
     * 根据流程定义Id启动流程;
     * 默认当前登陆用户为流程启动人
     *
     * @param processDefinitionId 流程定义ID：processDefinitionId；
     * @return 流程执行ID
     */
    @GetMapping("runNormalById/{processDefinitionId}")
    public ResponseEntity runProcessById(@PathVariable String processDefinitionId) {
        Map<String, Object> vars = new HashMap<>(255);

        User user = (User) session.getAttribute("user");
        vars.put("userId", user.getId());

        Object result = powerProcessService.startProcessInstanceById(processDefinitionId, vars);
        return ResponseEntity.ok(result);
    }


    /**
     * 根据流程定义key启动流程 失败 --tmd 为什么？
     * 参数问题： --目前来看跟参数没关系
     * @param processDefinitionKey 流程定义key
     * @return 标记
     */
    @GetMapping("runNormalByKey")
    public ResponseEntity runNormalByKey(@RequestParam String processDefinitionKey){
        Map<String, Object> vars = new HashMap<>(255);

        User user = (User) session.getAttribute("user");
        vars.put("userId", user.getId());
        System.out.println(vars);


        Object result = powerProcessService.startProcessInstanceByKey(processDefinitionKey);

        return ResponseEntity.ok(result);
    }
    /**
     * 查询流程部署情况
     * 通过mybatis 创建SQL语句直接从数据库表 act_re_deployment 中查询
     * 将结果封装到自定义实体类  PowerDeployment
     * TODO Pageable分页
     *
     * @return 流程部署列表 processDeploymentList
     */
    @GetMapping("deploymentList")
    public ResponseEntity<List<PowerDeployment>> processList() {
        List<PowerDeployment> list = powerProcessService.findProcessList();
        return ResponseEntity.ok(list);
    }

    /**
     * 自定义mybatis --mapper 查询流程定义列表    数据在表 act_re_procdef
     *
     * @return 流程定义列表 processDefinitionList
     */
    @GetMapping("processDefinitionList")
    public ResponseEntity<List<PowerProcessDefinition>> processDefinitionList() {
        List<PowerProcessDefinition> list = powerProcessService.findProcdefList();
        return ResponseEntity.ok(list);
    }

    /**
     * 此处使用流程部署Id deploymentId；
     * 根据流程部署Id删除流程，级联删除
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
     * @param processDefinition 流程定义Id
     * @return 标记
     */
    @GetMapping("runMultiInstance/{processDefinition}")
    public ResponseEntity runMultiInstance(@PathVariable String processDefinition) {
        String[] strings = {"ZhangSan", "LiSi", "WangWu"};
        List<String> list = Arrays.asList(strings);
        Map<String, Object> vars = new HashMap<>(255);

        vars.put("assigneeList", list);
        Object result = powerProcessService.startProcessInstanceById(processDefinition, vars);

        return ResponseEntity.ok(result);
    }

    /**
     * 多实例节点加签操作
     *
     * @param activityId        待加签的多实例节点Id  act_ru_task表中的 TASK_DEF_KEY_
     * @param parentExecutionId 父任务Id act_ru_task表中的PROC_INST_ID_ 或 act_ru_execution中的ID_ where PARENT_ID_ == null;
     * @return execution_Id     多实例节点任务ID；
     */
    @GetMapping("addMultiInstanceNode")
    public ResponseEntity addMulti(@RequestParam String activityId,
                                   @RequestParam String parentExecutionId) {
        Map<String, Object> vars = new HashMap<>(255);
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
    @GetMapping("deleteMultiInstanceNode")
    public ResponseEntity deleteMulti(@RequestParam String executionId) {
        runtimeService.deleteMultiInstanceExecution(executionId, true);
        return ResponseEntity.ok("完成多实例节点减签操作");
    }

    /**
     * 任意节点跳转操作 Flowable 6.3--- Cmd模式
     * 这里只是在普通节点之间跳转；多实例节点跳转到普通节点会出问题
     * (又测了几次，好像多实例节点也能跑的通ε=ε=ε=(~￣▽￣)~)
     *
     * @param taskId        当前任务节点ID    表act_ru_task中的ID；
     * @param targetNodeId  目标节点id   已部署的流程文件中的 <userTask id="shareniu-b"/> 标签中的Id；
     * @return 标记
     */
    @GetMapping("jump")
    public ResponseEntity jumpNode(@RequestParam String taskId,
                                   @RequestParam String targetNodeId) {
        managementService.executeCommand(new PowerJumpCmd(taskId, targetNodeId));
        return ResponseEntity.ok("跳转成功");
    }


    /**
     * 普通节点之间跳转操作，flowable 6.4更新后提供的方法
     * @param procInstanceId       流程实例Id  act_ru_task 表中的 PROC_INST_ID_字段
     * @param currentActivityId    当前节点id  流程标签中的id属性 <userTask id="xxx"/>
     * @param newActivityId        目标节点id
     * @return 标记
     */
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
     * 增加节点
     * @param processDefinitionId 流程定义Id
     * @return
     */
    @GetMapping("addNode")
    public ResponseEntity addNode(@RequestParam String processDefinitionId,
                                  @RequestBody PowerTask powerTask){
        Process process = managementService.executeCommand(new GetProcessCmd(processDefinitionId));

        //创建任务节点
        UserTask userTask = new UserTask();
        userTask.setId(powerTask.getId());
        userTask.setName(powerTask.getName());
        userTask.setAssignee(powerTask.getAssignee());
        userTask.setBehavior(createUserTaskBehavior(userTask));
        /*
        {
        "id":"addNode",
        "name":"添加节点",
        "assignee":"ZhangSan"
        }
         */
        //先将添加的节点写入process缓存中
        process.addFlowElementToMap(userTask);

        //设置写入流程sequenceFlow1 本次测试：从userTask1流向addNode
        SequenceFlow sequenceFlow1 = new SequenceFlow();
        String sourceRef1 = "userTask1";
        String targetRef1 = powerTask.getId();
        sequenceFlow1.setId(powerTask.getId()+"Flow_In");
        sequenceFlow1.setSourceRef(sourceRef1);
        sequenceFlow1.setSourceFlowElement(process.getFlowElement(sourceRef1));
        sequenceFlow1.setTargetRef(targetRef1);
        sequenceFlow1.setTargetFlowElement(process.getFlowElement(targetRef1));
        userTask.setIncomingFlows(Collections.singletonList(sequenceFlow1));

        //设置写出流程sequenceFlow2，本次测试：从addNode流向userTask2
        SequenceFlow sequenceFlow2 = new SequenceFlow();
        String sourceRef2 = powerTask.getId();
        String targetRef2 = "userTask2";
        sequenceFlow2.setId(powerTask.getId()+"Flow_Out");
        sequenceFlow2.setSourceRef(sourceRef2);
        sequenceFlow2.setSourceFlowElement(process.getFlowElement(sourceRef2));
        sequenceFlow2.setTargetRef(targetRef2);
        sequenceFlow2.setTargetFlowElement(process.getFlowElement(targetRef2));
        //将流程顺序写入userTask中
        userTask.setOutgoingFlows(Collections.singletonList(sequenceFlow2));
        //更新Process中的信息
        process.addFlowElement(userTask);
        process.addFlowElement(sequenceFlow1);
        process.addFlowElement(sequenceFlow2);

        //获取ProcessCache缓存管理对象
        ProcessDefinitionCacheEntry processCacheEntry = managementService
                        .executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId));

        //设置缓存
        processCacheEntry.setProcess(process);

        Collection<Artifact> artifacts = process.getArtifacts();
        System.out.println(artifacts);
        return ResponseEntity.ok(process);
    }

    /**
     * 这里修改的是全局流程实例模板 -/.\-！
     * 风险太大，不能用，需要将其改成正在执行中的执行实例模板
     * @param processDefinitionId 流程定义Id
     * @param targetNodeId 目标节点Id
     * @return
     */
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
     * 检查Process缓存
     * @param processDefinitionId 流程定义Id
     * @return process
     */
    @GetMapping("checkProcess")
    public ResponseEntity checkProcess(@RequestParam String processDefinitionId){
        ProcessDefinitionCacheEntry processCacheEntry = managementService
                .executeCommand(new GetProcessDefinitionCacheEntryCmd(processDefinitionId));
        Process process = processCacheEntry.getProcess();
        return ResponseEntity.ok(process);
    }

    /**
     * 创建任务节点行为类
     * 可能会复用，单独抽出来
     *
     * @param userTask 用户任务节点
     * @return 任务节点行为类
     */
    private Object createUserTaskBehavior(UserTask userTask) {
        //获取流程引擎配置类
        ProcessEngineConfigurationImpl engineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        // 获取活动行为工厂
        ActivityBehaviorFactory activityBehaviorFactory = engineConfiguration.getActivityBehaviorFactory();
        return activityBehaviorFactory.createUserTaskActivityBehavior(userTask);
    }
}
