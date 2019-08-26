package com.power.entity;

import lombok.Data;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 16:04
 * 流程定义信息实体类
 */

@Data
public class PowerProcessDefinition {

    private String id;
    private String rev;
    private String category;
    private String name;
    private String keyWord;
    private String version;
    private String deploymentId;
    private String bpmnResource;
    private String pngResource;
    private Integer suspensionState;

}
