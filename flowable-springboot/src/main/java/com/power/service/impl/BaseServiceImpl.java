package com.power.service.impl;

import com.power.service.BaseService;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * @author : xuyunfeng
 * @date :   2019/8/23 10:40
 */
public class BaseServiceImpl implements BaseService {

    @Autowired
    protected RepositoryService repositoryService;

    public BpmnModel getBpmnModel(String processDefinitionId){
        return repositoryService.getBpmnModel(processDefinitionId);
    }

    public BpmnModel getBpmnModel(Task task){
        return getBpmnModel(task.getProcessDefinitionId());
    }

    public Process getProcess(String processDefinitionId){
        BpmnModel bpmnModel = getBpmnModel(processDefinitionId);
        return bpmnModel.getMainProcess();
    }

    public Collection<FlowElement> getFlowElements(String processDefinitionId){
        Process process = getProcess(processDefinitionId);
        return process.getFlowElements();
    }






}
