package com.shareniu.v6.ch10.model;

import java.io.Serializable;
import java.util.List;

public class TempActivityModel  implements Serializable{
	protected String activityIds;
	protected String first;
	protected String last;
	
	protected List<TaskModel> activitys;
	public String getActivityIds() {
		return activityIds;
	}
	public void setActivityIds(String activityIds) {
		this.activityIds = activityIds;
	}
	
	
	
	public List<TaskModel> getActivitys() {
		return activitys;
	}
	public void setActivitys(List<TaskModel> activitys) {
		this.activitys = activitys;
	}
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String getLast() {
		return last;
	}
	public void setLast(String last) {
		this.last = last;
	}
	@Override
	public String toString() {
		return "TempActivityModel [activityIds=" + activityIds + ", first=" + first + ", last=" + last + ", activitys="
				+ activitys + "]";
	}
	
	
}
