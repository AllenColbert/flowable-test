package com.power.service.impl;


import com.power.entity.PowerDeployEntity;
import com.power.entity.PowerDeployment;
import com.power.entity.PowerProcdef;
import com.power.mapper.ProcessMapper;
import com.power.service.PowerProcessService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author xuyunfeng
 * @date 2019/7/9 14:20
 */
@Service
public class PowerProcessImpl implements PowerProcessService {

    private static final String BPMN_PREFIX = "upload/diagrams/";


    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ProcessMapper processMapper;


    @Override
    public Object deployProcess(String fileName, PowerDeployEntity powerDeploy) {
        if (!(fileName.endsWith("bpmn") || fileName.endsWith(".bpmn20.xml"))) {
            return "文件类型不符合";
        }
        if (powerDeploy == null ) {
            return "请填写流程部署信息";
        }
        //默认没有外置表单，临时属性，后期调整；
        if (powerDeploy.getOuterForm() == null) {
            powerDeploy.setOuterForm(false);
        }
        ClassPathResource classPathResource = new ClassPathResource(BPMN_PREFIX + fileName);
        //判断是否有对应的流程文件
        if (classPathResource.exists()) {
            //创建流程部署对象
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                    .category(powerDeploy.getCategory())
                    .tenantId(powerDeploy.getTenantId())
                    .name(powerDeploy.getName())
                    .key(powerDeploy.getKey())
                    .addClasspathResource(BPMN_PREFIX + fileName);
            //是否有外置表单，如果有外置表单，要在部署的时候一同加进去才能生效
            if (powerDeploy.getOuterForm()) {
                List<String> formList = powerDeploy.getFormResource();
                for (String formName : formList) {
                    deploymentBuilder.addClasspathResource(formName);
                }
            }
            return deploymentBuilder.deploy();
        } else {
            return "资源文件不存在，请重新部署";
        }
    }

    @Override
    public List<PowerDeployment> findProcessList() {

        return processMapper.findProcessList();

    }

    @Override
    public List<PowerProcdef> findProcdefList() {

        return processMapper.findProcdefList();
    }

    @Override
    public Object startProcessInstance(String procDefId) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId);
        return "流程实例Id：" + processInstance.getId();
    }

    @Override
    public Object startProcessInstance(String procDefId, Map<String, Object> vars) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, vars);
        return "流程实例Id：" + processInstance.getId();
    }

    //TODO 通过流程定义Key启动流程实例 失败
    @Override
    public Object startProcessInstanceByKey(String procDefKey, Map<String, Object> vars) {

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(procDefKey, vars);
        return "流程实例Id" + processInstance.getId() + "--启动流程的userId" + processInstance.getStartUserId();
    }


}
