package com.power.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 16:42
 */

@Data
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

}
