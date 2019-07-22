package com.shareniu.v6.ch10.core;

import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;

import com.shareniu.v6.ch10.model.TaskModel;
import com.sun.javafx.tk.Toolkit.Task;

public class GenerateActivity {
	/**
	 * 生成连线信息
	 * 
	 * @param id
	 * @param source
	 * @param target
	 * @return
	 */
	public static SequenceFlow generateSequenceFlow(String id, String source, String target) {
		SequenceFlow sequenceFlow = new SequenceFlow();
		sequenceFlow.setId(id);
		sequenceFlow.setSourceRef(source);
		sequenceFlow.setTargetRef(target);
		return sequenceFlow;
	}

	/**
	 * 生成任务节点
	 * 
	 * @param id
	 * @param name
	 * @param assignee
	 * @param processEngine
	 * @return
	 */
	public static UserTask generateUserTask(String id, String name, String assignee, ProcessEngine processEngine) {
		UserTask userTask = new UserTask();
		userTask.setId(id);
		userTask.setAssignee(assignee);
		userTask.setName(name);
		userTask.setBehavior(createUserTaskBehavior(userTask, processEngine));
		return userTask;
	}
	/**
	 * 将TaskModel实例对象转换为UserTask实例对象
	 * @param taskModel
	 * @param processEngine
	 * @return
	 */
	public static UserTask transformation(TaskModel taskModel, ProcessEngine processEngine) {
		UserTask generateUserTask = generateUserTask(taskModel.getId(), taskModel.getName(), taskModel.getDoUserId(), processEngine);
		return generateUserTask;
	}

	/**
	 * 生成自定义任务模型
	 * 
	 * @param id
	 * @param name
	 * @param assignee
	 * @return
	 */
	public static TaskModel generateTaskModel(String id, String name, String assignee) {
		TaskModel userTask = new TaskModel();
		userTask.setId(id);
		userTask.setDoUserId(assignee);
		userTask.setName(name);
		return userTask;
	}

	/**
	 * 生成任务节点行为类
	 * 
	 * @param userTask
	 * @param processEngine
	 * @return
	 */
	public static Object createUserTaskBehavior(UserTask userTask, ProcessEngine processEngine) {
		ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine
				.getProcessEngineConfiguration();
		// activityBehaviorFactory
		ActivityBehaviorFactory activityBehaviorFactory = processEngineConfiguration.getActivityBehaviorFactory();
		UserTaskActivityBehavior userTaskActivityBehavior = activityBehaviorFactory
				.createUserTaskActivityBehavior(userTask);
		return userTaskActivityBehavior;
	}
}
