package com.shareniu.v6.ch10.model;

/**
 * 运行期的活动节点mode实体
 */
public class ActCreation {
	String id;
	String processDefinitionId;
	String douUerId;
	String actId;
	private String processInstanceId;
	private String processText;
	private Integer state;

	// 任务节点id
	public String getActId() {
		return actId;
	}

	public void setActId(String actId) {
		this.actId = actId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getDouUerId() {
		return douUerId;
	}

	public void setDouUerId(String douUerId) {
		this.douUerId = douUerId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getProcessText() {
		return processText;
	}

	public void setProcessText(String processText) {
		this.processText = processText;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "ActCreation [id=" + id + ", processDefinitionId=" + processDefinitionId + ", douUerId=" + douUerId
				+ ", actId=" + actId + ", processInstanceId=" + processInstanceId + ", processText=" + processText
				+ ", state=" + state + "]";
	}

}
