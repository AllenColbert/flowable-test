package com.power.service.impl;


import com.power.entity.PowerDeployEntity;
import com.power.entity.PowerDeployment;
import com.power.entity.PowerProcessDefinition;
import com.power.mapper.ProcessMapper;
import com.power.service.PowerProcessService;
import com.power.util.Result;
import com.power.util.ResultCode;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xuyunfeng
 * @date 2019/7/9 14:20
 */
@Service
public class PowerProcessImpl implements PowerProcessService {

    private final static Integer SUCCESS_CODE = 200;
    private final static Integer PROCESS_IS_SUSPENDED = 30002;

    /**
     * 默认存放bpmn20.xml文件的位置
     */
    private static final String BPMN_PREFIX = "upload/diagrams/";

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private ProcessMapper processMapper;

    @Autowired
    private ManagementService managementService;

    @Override
    public Result queryProcessModelList() {
        List<AbstractModel> models = modelService.getModelsByModelType(0);

        if (models == null || models.size() == 0){
            return Result.failure(ResultCode.RESULT_DATA_NONE);
        }

        return Result.success(models);
    }

    @Override
    public Result queryProcessDefinitionList() {
        List<PowerProcessDefinition> processDefinitionList = processMapper.queryProcessDefinitionList();
        if (processDefinitionList == null || processDefinitionList.size() == 0){
            return Result.failure(ResultCode.RESULT_DATA_NONE);
        }
        return Result.success(processDefinitionList);

    }

    @Override
    public Result startProcessInstanceById(String processDefinitionId, Map<String, Object> vars) {
        //这里需要先对流程状态进行判断，1.判断流程是否存在，2.流程是否挂起；
        Result result = checkStatusByProcessDefinitionId(processDefinitionId);
        if (!result.getCode().equals(SUCCESS_CODE)){
            return result;
        }
        //前端GET请求 直接传流程定义Id时，会将 ':'编码为'%3A',这里加个判断自动适应GET请求或POST请求
        String specialCharacters  = "%3A";
        String replacementCharacter =":";
        if (processDefinitionId .contains(specialCharacters)){
            processDefinitionId  = processDefinitionId.replaceAll(specialCharacters, replacementCharacter);
        }

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, vars);
        return Result.success(processInstance.getId());
    }

    @Override
    public Result deleteProcessByDeploymentId(String deploymentId, Boolean concatenation){
        //删前判断一下流程状态
        Result result = checkStatusByProcessDeploymentId(deploymentId);
        if (!result.getCode().equals(SUCCESS_CODE)){
            return result;
        }
        repositoryService.deleteDeployment(deploymentId,concatenation);
        return Result.success();
    }

    @Override
    public Result suspendProcessByProcessDefinitionId(String processDefinitionId,
                                                      Boolean suspendProcessInstances,
                                                      Date suspensionDate){
        Result result = checkStatusByProcessDefinitionId(processDefinitionId);
        if (!result.getCode().equals(SUCCESS_CODE)){
            return result;
        }
        repositoryService.suspendProcessDefinitionById(processDefinitionId,suspendProcessInstances,suspensionDate);
        return Result.success();
    }

    @Override
    public  Result activateProcessByProcessDefinitionId(String processDefinitionId,
                                                        Boolean suspendProcessInstances,
                                                        Date suspensionDate){
        Result result = checkStatusByProcessDefinitionId(processDefinitionId);

        //只有流程被挂起时才能进行激活操作
        if (result.getCode().equals(PROCESS_IS_SUSPENDED)){
            repositoryService.activateProcessDefinitionById(processDefinitionId,suspendProcessInstances,suspensionDate);
            return Result.success();
        }
        //其他任何状态对于激活操作来说都是异常
        return Result.failure(ResultCode.PROCESS_STATUS_EXCEPTION,result.getMsg());

    }

    @Override
    public Object deployProcess(String fileName, PowerDeployEntity powerDeploy) {
        String fileType1 = "bpmn";
        String fileType2 = "xml";
        if (!(fileName.endsWith(fileType1) || fileName.endsWith(fileType2))) {
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
        return processMapper.queryProcessDeploymentList();
    }

    @Override
    public Object startProcessInstanceByKey(String processDefinitionKey) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        return "流程实例Id：" + processInstance.getId();
    }


    //方法区

    private Result checkStatusByProcessDefinitionId(String processDefinitionId){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        //判断流程是否存在
        if (processDefinition == null ){
            return Result.failure(ResultCode.RESULT_DATA_NONE);
        }
        //判断流程是否被挂起
        if (processDefinition.isSuspended()){
            return Result.failure(ResultCode.PROCESS_IS_SUSPENDED);
        }
        return Result.success();
    }

    private Result checkStatusByProcessDeploymentId(String processDeploymentId){
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(processDeploymentId).singleResult();
        if (deployment == null){
            return Result.failure(ResultCode.RESULT_DATA_NONE);
        }
        return Result.success();

    }

/*
    public void testCmd(){
        managementService.executeCommand(new AddMultiInstanceExecutionCmd(activityDefId, procId, variables));
    }
*/

}
