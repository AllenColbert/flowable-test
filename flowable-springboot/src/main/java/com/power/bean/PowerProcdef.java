package com.power.bean;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 16:04
 * 流程定义信息
 */
public class PowerProcdef {
    private static final long serialVersionUID = 1L;

    private String id;
    private String rev;
    private String category;
    private String name;
    private String key1;
    private String version;
    private String deploymentId;
    private String bpmnResource;
    private String PngResource;

    @Override
    public String toString() {
        return "PowerProcdef{" +
                "id='" + id + '\'' +
                ", rev='" + rev + '\'' +
                ", category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", key1='" + key1 + '\'' +
                ", version='" + version + '\'' +
                ", deploymentId='" + deploymentId + '\'' +
                ", bpmnResource='" + bpmnResource + '\'' +
                ", PngResource='" + PngResource + '\'' +
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
        return PngResource;
    }

    public void setPngResource(String pngResource) {
        PngResource = pngResource;
    }
}
