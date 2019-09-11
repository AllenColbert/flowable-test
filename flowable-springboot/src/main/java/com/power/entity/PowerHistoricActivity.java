package com.power.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户办理的任务历史
 * 注意：用Spring BeanUtils的反射方法填充数据的，不要轻易改字段名
 * @author : xuyunfeng
 * @date :   2019/9/6 10:33
 */
@Data
public class PowerHistoricActivity {

    private static final long serialVersionUID = 1L;

    protected String activityId;
    protected String activityName;
    protected String activityType;
    protected String executionId;
    protected String assignee;
    protected String taskId;
    protected String tenantId;
    protected String processInstanceId;
    protected String processDefinitionId;
    protected Date startTime;
    protected Date endTime;
    protected Long durationInMillis;
    protected String deleteReason;
    protected String id;
    protected String message;
}
