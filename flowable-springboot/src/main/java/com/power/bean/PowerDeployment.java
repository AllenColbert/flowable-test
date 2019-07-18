package com.power.bean;

import java.util.Date;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 15:28
 * @描述 ：流程部署信息,目前来看没什么用，
 * TODO 测试结束后，可以删除此类及相关代码
 */
public class PowerDeployment {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String category;
    private String key1;
    private String tenantId;
    private Date deployTime;

    @Override
    public String toString() {
        return "PowerDeployment{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", key='" + key1 + '\'' +
                ", tenant_id='" + tenantId + '\'' +
                ", deploy_time=" + deployTime +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return key1;
    }

    public void setKey(String key) {
        this.key1 = key;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenant_id) {
        this.tenantId = tenant_id;
    }

    public Date getDeployTime() {
        return deployTime;
    }

    public void setDeploy_time(Date deploy_time) {
        this.deployTime = deploy_time;
    }
}
