package com.power.controller;


import com.power.entity.PowerDeployment;
import com.power.entity.PowerDeployEntity;
import com.power.service.PowerProcessService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 根据流程部署Id启动流程定义 ;
     * @param deployId 流程部署ID
     * @return 流程执行ID
     */
    @GetMapping("run/{deployId}")
    public ResponseEntity runProcessById(@PathVariable String deployId){
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(deployId);
        return ResponseEntity.ok("processInstance:"+processInstance);
    }


    /**
     * 查询流程部署情况
     * 通过mybatis 创建SQL语句直接从数据库中查询，封装到自定义实体类
     */
    @GetMapping("list")
    public ResponseEntity processList(){
        List<PowerDeployment> list = powerProcessService.findProcessList();
        return ResponseEntity.ok(list);
    }


    /**
     * 查询流程部署表 act_re_procdef
     */
    @GetMapping("procdefList")
    public ResponseEntity ProcedefList(){
        return ResponseEntity.ok(powerProcessService.findProcdefList());
    }


    /**
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
