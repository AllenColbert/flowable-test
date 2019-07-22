package com.shareniu.v6.ch10.model;

import java.io.Serializable;
//自定义任务模型
public class TaskModel implements Serializable {
	private String id;//任务ID
	private String doUserId;//任务的处理人
	private String name;//任务的名称
	private int type=1;//类型 1是任务节点；2，服务节点
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDoUserId() {
		return doUserId;
	}
	public void setDoUserId(String doUserId) {
		this.doUserId = doUserId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public TaskModel(String id, String doUserId, String name, int type) {
		this.id = id;
		this.doUserId = doUserId;
		this.name = name;
		this.type = type;
	}
	public TaskModel() {
	}
	
	
}
