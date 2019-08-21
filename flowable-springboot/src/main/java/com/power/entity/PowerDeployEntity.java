package com.power.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author xuyunfeng
 * @date 2019/7/9 14:25
 */
@ApiModel
@Data
public class PowerDeployEntity  {

    /**
     * 流程部署的时候填的信息，
     *  name 流程名
     *  category 分类
     *  tenantId 租户id
     *  key 关键字
     *  outerForm 是否有外置表单
     *  formResource 外表单地址集合
     */
    private String name;
    private String category;
    private String key;
    private String tenantId;
    private Boolean outerForm;
    private List<String> formResource;

    /*
    JSON示例：

    {
	"name":"外置表单测试",
	"category":"测试类",
	"key":"外置表单",
	"tenantId":"11",
	"outerForm":true,
	"formResource":["start22.html","task22.html","task2.html"]
    }

     */
}
