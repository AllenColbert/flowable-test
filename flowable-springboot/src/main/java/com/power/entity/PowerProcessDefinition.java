package com.power.entity;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 16:04
 * 流程定义信息实体类
 */

public class PowerProcessDefinition {
    private static final long serialVersionUID = 1L;

    private String id;
    private String rev;
    private String category;
    private String name;
    //此处设置为key 查询时SQL语句会报错
    private String key1;
    private String version;
    private String deploymentId;
    private String bpmnResource;
    private String pngResource;

    @Override
    public String toString() {
        return "PowerProcessDefinition{" +
                "id='" + id + '\'' +
                ", rev='" + rev + '\'' +
                ", category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", key1='" + key1 + '\'' +
                ", version='" + version + '\'' +
                ", deploymentId='" + deploymentId + '\'' +
                ", bpmnResource='" + bpmnResource + '\'' +
                ", pngResource='" + pngResource + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getBpmnResource() {
        return bpmnResource;
    }

    public void setBpmnResource(String bpmnResource) {
        this.bpmnResource = bpmnResource;
    }

    public String getPngResource() {
        return pngResource;
    }

    public void setPngResource(String pngResource) {
        this.pngResource = pngResource;
    }
}
