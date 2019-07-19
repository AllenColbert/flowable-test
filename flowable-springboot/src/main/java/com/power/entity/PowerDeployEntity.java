package com.power.entity;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * @author xuyunfeng
 * @date 2019/7/9 14:25
 */

@Component
public class PowerDeployEntity implements Serializable {

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
	"formResource":["start.html","task.html","task2.html"]
    }

     */

    public Boolean getOuterForm() {
        return outerForm;
    }

    public void setOuterForm(Boolean outerForm) {
        this.outerForm = outerForm;
    }

    public List<String> getFormResource() {
        return formResource;
    }

    public void setFormResource(List<String> formResource) {
        this.formResource = formResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "PowerDeployEntity{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", key='" + key + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", outerForm=" + outerForm +
                ", formResource=" + formResource +
                '}';
    }
}
