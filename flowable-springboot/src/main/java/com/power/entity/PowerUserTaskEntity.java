package com.power.entity;

import lombok.Data;

/**
 * 新增节点的时候添加的实体类
 * @author xuyunfeng
 * @date 2019/8/11 17:51
 */
@Data
public class PowerUserTaskEntity {

    private String id;
    private String name;
    private String assignee;
    private String sourceRef;
    private String targetRef;
    private String sequenceFlowId;
    private String conditionExpression;

}
