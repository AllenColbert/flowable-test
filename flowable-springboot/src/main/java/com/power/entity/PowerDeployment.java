package com.power.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 15:28
 * @描述 ：流程部署信息,目前来看没什么用，用最多就只是deploymentId
 */
@Getter
@Setter
public class PowerDeployment {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String category;
    private String keyWord;
    private String tenantId;
    private Date deployTime;

    @Override
    public String toString() {
        return "PowerDeployment{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", key='" + keyWord + '\'' +
                ", tenant_id='" + tenantId + '\'' +
                ", deploy_time=" + deployTime +
                '}';
    }

}
