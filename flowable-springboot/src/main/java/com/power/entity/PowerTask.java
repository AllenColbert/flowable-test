package com.power.entity;

import java.util.Date;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 16:42
 */


public class PowerTask {

    private String id;
    private String executionId;
    private String processInstanceId;
    private String processDefinitionId;
    private String name;
    private String taskDefinitionKey;
    private String assignee;
    private Date createTime;
    private Integer suspensionState;
    private String tenantId;
    private String formKey;

    @Override
    public String toString() {
        return "PowerTask{" +
                "id='" + id + '\'' +
                ", executionId='" + executionId + '\'' +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", processDefinitionId='" + processDefinitionId + '\'' +
                ", name='" + name + '\'' +
                ", taskDefinitionKey='" + taskDefinitionKey + '\'' +
                ", assignee='" + assignee + '\'' +
                ", createTime=" + createTime +
                ", suspensionState=" + suspensionState +
                ", tenantId='" + tenantId + '\'' +
                ", formKey='" + formKey + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getSuspensionState() {
        return suspensionState;
    }

    public void setSuspensionState(Integer suspensionState) {
        this.suspensionState = suspensionState;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }
}
