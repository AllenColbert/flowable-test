package com.shareniu.shareniu_flowable_study.bpmn.ch4.model;

public class FlowableCreation {
	private int nodeType=1;
	private String id;
	private String name;
	private String assignee;
	private int sort;
	private String processInstanceId;
	public int getNodeType() {
		return nodeType;
	}
	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
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
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	@Override
	public String toString() {
		return "FlowableCreation [nodeType=" + nodeType + ", id=" + id + ", name=" + name + ", assignee=" + assignee
				+ ", sort=" + sort + ", processInstanceId=" + processInstanceId + "]";
	}

}
