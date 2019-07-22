package com.power.controller;


import com.power.entity.PowerDeployEntity;
import com.power.entity.PowerDeployment;
import com.power.service.PowerProcessService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private PowerProcessService powerProcessService;

    @Autowired
    private HttpSession session;

    /**
     * 根据本地文件名部署流程
     * @param fileName 文件名
     * @return deploy
     */
    @GetMapping("deploy/{fileName}")
    public ResponseEntity<Object> deploy(@PathVariable String fileName,
                                         @RequestBody(required = false) PowerDeployEntity powerDeploy) {
        Object result =  powerProcessService.deployProcess(fileName,powerDeploy);

        return ResponseEntity.ok(result);
    }

    /**
     * 根据流程定义Id启动流程;
     * @param procDefId 流程定义ID
     * @return 流程执行ID
     */
    @GetMapping("run/{procDefId}")
    public ResponseEntity runProcessById(@PathVariable String procDefId){
        Map<String, Object> vars = new HashMap<>();

        User user = (User) session.getAttribute("user");
        vars.put("userId",user.getId());
        Object result =  powerProcessService.startProcessInstance(procDefId,vars);
        //ProcessInstance processInstance = runtimeService.startProcessInstanceById(deployId);
        return ResponseEntity.ok(result);
    }




    /**
     * 查询流程部署情况
     * 通过mybatis 创建SQL语句直接从数据库中查询，封装到自定义实体类
     */
    @GetMapping("deploymentList")
    public ResponseEntity<List<PowerDeployment>> processList(){
        List<PowerDeployment> list = powerProcessService.findProcessList();
        return ResponseEntity.ok(list);
    }


    /**
     * 查询流程定义表 act_re_procdef
     */
    @GetMapping("procdefList")
    public ResponseEntity<List<com.power.entity.PowerProcdef>> ProcedefList(){
        return ResponseEntity.ok(powerProcessService.findProcdefList());
    }


    /**
     *  从参数上分清楚流程部署ID：deployment表中的Id 和 流程定义ID procdef表中的Id的区别；
     *  此处使用流程部署Id deploymentId；
     * 根据流程部署Id删除流程，级联删除
     * @param deployId  流程部署Id
     * @param concatenation 是否开启级联删除，默认开启
     */
    @DeleteMapping("delete/{deployId}")
    public ResponseEntity<String> deleteProcessById(@PathVariable String deployId,
                                                    @RequestParam(defaultValue = "true") Boolean concatenation){
        repositoryService.deleteDeployment(deployId,concatenation);
        return ResponseEntity.ok("删除流程成功，Id："+deployId);
    }


    /**
     * 测试动态设置 assignee
     * 目前 数据库中设置的都是 List的编号0，1，2，而不是对应的Value
     * @param procDefId
     * @return
     */
    @GetMapping("runMultiInstance4/{procDefId}")
    public ResponseEntity runMultiInstance4(@PathVariable String procDefId){
        String [] strings = {"ZhangSan","LiSi","WangWu"};

        Map<String,Object> vars = new HashMap<>();
        vars.put("assigneeList", Arrays.asList(strings));

        Object result = powerProcessService.startProcessInstance(procDefId, vars);

        // Object result = powerProcessService.startProcessInstanceByKey(procDefKey, vars);

        return ResponseEntity.ok(result);
    }

    @GetMapping("addMulti")
    public ResponseEntity addMulti(){
       // String [] strings = {"ZhangSan","LiSi"};
        Map<String, Object> vars =new HashMap<>();
        String activityId = "usertask1";
        String parentExecutionId ="35aca5d2-ac57-11e9-9144-c8f7502bee8b";
        //vars.put("assigneeList",Arrays.asList(strings));
        vars.put("assignee","LiSi");

        Execution execution = runtimeService.addMultiInstanceExecution(activityId, parentExecutionId, vars);

        return ResponseEntity.ok(execution.getId());
    }

    @GetMapping("deleteMulti")
    public ResponseEntity deleteMulti(@RequestParam String executionId){
        runtimeService.deleteMultiInstanceExecution(executionId,true);
        return ResponseEntity.ok("减签");
    }
}
