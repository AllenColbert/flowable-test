package com.shareniu.shareniu_flowable_study.ch3;

import java.util.ArrayList;
import java.util.List;

import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.ImplementationType;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.BaseTaskListener;
import org.flowable.engine.impl.bpmn.parser.BpmnParse;
import org.flowable.engine.impl.bpmn.parser.handler.UserTaskParseHandler;

public class ShareniuUserTaskParseHandler extends UserTaskParseHandler {
	  protected void executeParse(BpmnParse bpmnParse, UserTask userTask) {
	        userTask.setBehavior(bpmnParse.getActivityBehaviorFactory().createUserTaskActivityBehavior(userTask));
	        
	        List<FlowableListener> taskListeners=new ArrayList<FlowableListener>();
	        
	        FlowableListener taskListener=new FlowableListener();
	        taskListener.setImplementation(BpmnXMLConstants.ATTRIBUTE_LISTENER_CLASS);
	        taskListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
	        taskListener.setEvent(BaseTaskListener.EVENTNAME_ALL_EVENTS);
	        taskListener.setImplementation("com.shareniu.shareniu_flowable_study.ch3.ShareniuTaskListener");
			taskListeners.add(taskListener);
		//	userTask.setExecutionListeners(executionListeners);
			userTask.setTaskListeners(taskListeners);
	    }
}
