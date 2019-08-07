package com.power.controller;


import com.power.cmd.GetProcessCmd;
import com.power.cmd.GetProcessDefinitionCacheEntryCmd;
import com.power.cmd.PowerJumpCmd;
import com.power.entity.PowerDeployEntity;
import com.power.entity.PowerDeployment;
import com.power.entity.PowerTask;
import com.power.service.CommonService;
import com.power.service.PowerProcessService;
import com.power.util.Result;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.Execution;
import org.flowable.idm.api.User;
import org.flowable.ui.common.service.exception.NotFoundException;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author xuyunfeng
 * @date 2019/7/9 14:20
 */

@Controller
@RequestMapping("process")
public class ProcessController extends BaseController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private PowerProcessService powerProcessService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private HttpSession session;

    @Autowired
    private ModelService modelService;

    /**
     * 获取流程模型列表
     * @param model model
     * @return 流程模型页面
     */
    @GetMapping("modelList")
    public String processModelList(Model model){

        Result result =  powerProcessService.queryProcessModelList();

        if (!result.getCode().equals(SUCCESS_CODE)){
            model.addAttribute("errorMsg",result.getMsg());
            return "errorPage";
        }
        model.addAttribute("models",result.getData());
        return  "modelList";
    }

    /**
     * 显示流程定义列表
     * @param model model对象
     * @return html文件名
     */
    @GetMapping("processList")
    public String processList(Model model){
        Result result =  powerProcessService.queryProcessDefinitionList();

        if (!result.getCode().equals(SUCCESS_CODE)){
            model.addAttribute("errorMsg",result.getMsg());
            return "errorPage";
        }

        model.addAttribute("processDefinitionList",result.getData());
        return "processList";
    }

    /**
     * 根据流程定义Id启动流程;
     * 默认当前登陆用户为流程启动人
     *
     * @param processDefinitionId 流程定义ID：processDefinitionId；
     * @return 流程执行ID
     */
    @PostMapping("startProcessById")
    @ResponseBody
    public Result startProcessById(@RequestParam String processDefinitionId) {

        //TODO 目前还不会做前端form传值，先写死，后面再改
        System.out.println("传到Controller层的processDefinitionId:"+processDefinitionId);

        Map<String, Object> vars = new HashMap<>(255);
        String userId = "admin";
        User user = (User) session.getAttribute("user");
        if (user != null) {
            userId = user.getId();
        }
        vars.put("userId", userId);

       return powerProcessService.startProcessInstanceById(processDefinitionId,vars);

    }

    /**
     * 根据流程部署Id删除流程，默认开启级联删除
     * 此处使用流程部署Id deploymentId；
     * @param deploymentId  流程部署Id
     * @param concatenation 是否开启级联删除，默认开启
     * @return result
     */
    @DeleteMapping("deleteProcessById")
    @ResponseBody
    public Result deleteProcessById(@RequestParam String deploymentId,
                                    @RequestParam(required = false,defaultValue = "true") Boolean concatenation) {
      return powerProcessService.deleteProcessByDeploymentId(deploymentId,concatenation);
    }

    /**
     * 通过流程定义Id挂起整个流程
     * @param processDefinitionId 流程定义Id
     * @param suspendProcessInstances 是否挂起所有的流程实例,默认开启
     * @param suspensionDate 流程定义将被暂停的日期，为null时，会立即激活
     * @return result
     */
    @GetMapping("suspendProcessByProcessDefinitionId")
    @ResponseBody
    public Result suspendProcessByProcessDefinitionId(@RequestParam String processDefinitionId,
                                                      @RequestParam(defaultValue = "true", required = false) Boolean suspendProcessInstances,
                                                      @RequestParam(required = false)Date suspensionDate){
        return powerProcessService.suspendProcessByProcessDefinitionId(processDefinitionId,suspendProcessInstances,suspensionDate);
    }

    /**
     *通过流程定义Id激活整个流程
     * @param processDefinitionId 流程定义Id
     * @param suspendProcessInstances 是否激活所有的流程实例，默认开启
     * @param suspensionDate 流程定义将被暂停的日期，为null时，会立即激活
     * @return Result
     */
    @GetMapping("activateProcessByProcessDefinitionId")
    @ResponseBody
    public Result activateProcessByProcessDefinitionId(@RequestParam String processDefinitionId,
                                                       @RequestParam(defaultValue = "true", required = false) Boolean suspendProcessInstances,
                                                       @RequestParam(required = false)Date suspensionDate){
        return powerProcessService.activateProcessByProcessDefinitionId(processDefinitionId,suspendProcessInstances,suspensionDate);
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

//#########################################未整理代码##############################################


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
     * 增加单个普通节点
     * @param processDefinitionId 流程定义Id
     * @param sourceRef 源节点 一般来说都是新增的节点id
     * @param targetRef 目标节点
     * @param powerTask 新增的节点
     * @return
     */
    @GetMapping("addSingleNode")
    public ResponseEntity addNode(@RequestParam String processDefinitionId,
                                  @RequestParam String sourceRef,
                                  @RequestParam String targetRef,
                                  @RequestBody PowerTask powerTask){
       /*{"id":"addNode","name":"添加节点","assignee":"ZhangSan"}*/
        //根据流程定义Id获取流程对象
        Process process = managementService.executeCommand(new GetProcessCmd(processDefinitionId));
        //创建用户任务节点
        UserTask userTask = commonService.createUserTask(powerTask);
        //创建流程输出信息
        SequenceFlow sequenceFlow = commonService.createSequenceFlow(sourceRef, targetRef, processDefinitionId);
        //将流程输出信息转化为List
        List<SequenceFlow> sequenceFlows = Collections.singletonList(sequenceFlow);
        //设置流程输出信息
        userTask.setOutgoingFlows(sequenceFlows);
        //更新Process中的信息
        process.addFlowElement(userTask);
        process.addFlowElement(sequenceFlow);
        //更新缓存中的流程对象信息
        commonService.updateProcess(process,processDefinitionId);

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
     * 根据流程模型id部署流程
     * @param modelId 流程模型ID
     * @return status
     * @throws UnsupportedEncodingException 异常
     */
    @GetMapping("deployModelById")
    public ResponseEntity deployModelById(@RequestParam String modelId) throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap<>(255);
        //错误信息
        String message;
        //状态
        Integer status;
        //获取模型
        org.flowable.ui.modeler.domain.Model modelData;
        try {
            modelData = modelService.getModel(modelId);
        } catch (NotFoundException e) {
            message = "模型数据为空，请先设计流程并成功保存，再进行发布。";
            status=404;
            map.put("message",message);
            map.put("status",status);
            return ResponseEntity.ok(map);
        }catch (Exception e){
            message = "其他错误";
            status=500;
            map.put("message",message);
            map.put("status",status);
            return ResponseEntity.ok(map);
        }


        byte[] bytes = modelService.getBpmnXML(modelData);

     if (bytes == null) {

         message = "模型数据为空，请先设计流程并成功保存，再进行发布。";
         status=404;

         map.put("message",message);

         map.put("status",status);

         return ResponseEntity.ok(map);
     }

    BpmnModel model=modelService.getBpmnModel(modelData);
    if(model.getProcesses().size()==0){
        message="数据模型不符要求，请至少设计一条主线流程。";
        status=500;

        map.put("message",message);
        map.put("status",status);

        return ResponseEntity.ok(map);
    }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deploy=  repositoryService.createDeployment()
                .name(modelData.getName())
                .addString(processName, new String(bpmnBytes, "UTF-8"))
                .deploy();

        message = "成功部署";
        status=200;

        map.put("message",message);
        map.put("status",status);
        map.put("data",deploy);
        return (ResponseEntity.ok(map));
    }


    /**
     * 根据流程模型Id删除流程模型
     * @param modelId id
     * @return mark
     */
    @GetMapping("deleteModelById")
    public ResponseEntity deleteModelById(@RequestParam String modelId){
        org.flowable.ui.modeler.domain.Model model = modelService.getModel(modelId);
        if (model == null ){
            return ResponseEntity.ok("没有对应的流程模板");

        }        modelService.deleteModel(modelId);
        return ResponseEntity.ok("成功删除");
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
    @GetMapping("deploy")
    public ResponseEntity<Object> deploy(@RequestParam String fileName,
                                         @RequestBody(required = false)PowerDeployEntity powerDeploy) {
        Object result = powerProcessService.deployProcess(fileName, powerDeploy);

        return ResponseEntity.ok(result);
    }


}
