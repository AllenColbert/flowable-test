package com.power.controller;


import com.power.entity.PowerDeployEntity;
import com.power.entity.PowerDeployment;
import com.power.service.PowerProcessService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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
    private HttpSession session;

    /**
     * 根据本地文件名部署流程
     * @param fileName 文件名
     * @return deploy
     */
    @GetMapping("deploy/{fileName}")
    public ResponseEntity deploy(@PathVariable String fileName,
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
    public ResponseEntity processList(){
        List<PowerDeployment> list = powerProcessService.findProcessList();
        return ResponseEntity.ok(list);
    }


    /**
     * 查询流程定义表 act_re_procdef
     */
    @GetMapping("procdefList")
    public ResponseEntity ProcedefList(){
        return ResponseEntity.ok(powerProcessService.findProcdefList());
    }


    /**
     * TODO 从参数上分清楚流程部署ID：deployment表中的Id 和 流程定义ID procdef表中的Id的区别；
     * 根据流程部署Id删除流程，级联删除
     * @param deployId  流程部署Id
     * @param concatenation 是否开启级联删除
     */
    @DeleteMapping("delete/{deployId}")
    public ResponseEntity deleteProcessById(@PathVariable String deployId,
                                            @RequestParam(defaultValue = "true") Boolean concatenation){
        repositoryService.deleteDeployment(deployId,concatenation);
        return ResponseEntity.ok("删除流程成功，Id："+deployId);
    }


}
