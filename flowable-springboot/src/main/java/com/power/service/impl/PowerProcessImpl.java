package com.power.service.impl;


import com.power.entity.PowerDeployEntity;
import com.power.entity.PowerDeployment;
import com.power.entity.PowerProcdef;
import com.power.mapper.ProcessMapper;
import com.power.service.PowerProcessService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.DeploymentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private ProcessMapper processMapper;

    @Override
    public Object deployProcess(String fileName, PowerDeployEntity powerDeploy) {
        if (powerDeploy == null) {return "请填写流程部署信息";}

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


}
