package com.shareniu.shareniu_flowable_study.bpmn.ch4.core;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;

import com.alibaba.fastjson.JSON;
import com.shareniu.shareniu_flowable_study.bpmn.ch4.APP;
import com.shareniu.shareniu_flowable_study.bpmn.ch4.model.TempActivityModel;

/**
 * 流程启动加载自定义的流程后动创建
 */
public class LoadActCreation implements StartEngineEventListener {

	@Override
	public void after(ProcessEngineConfigurationImpl conf, ProcessEngine processEngine) {
		SqlSessionFactory sqlSessionFactory = conf.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		List<ActCreation> selectList = sqlSession
				.selectList("com.shareniu.shareniu_flowable_study.bpmn.ch4.mapper.CreationMapper.findAll", null);
		System.out.println(selectList);
		if (selectList.size()>0) {
			for (ActCreation actCreation : selectList) {
				String processDefinitionId = actCreation.getProcessDefinitionId();
				String processInstanceId = actCreation.getProcessInstanceId();
				String processText = actCreation.getProcessText();
				TempActivityModel tempActivityModel = JSON.parseObject(processText, TempActivityModel.class);
				APP.repositoryService=processEngine.getRepositoryService();
				APP.runtimeService = processEngine.getRuntimeService();
				APP.taskService = processEngine.getTaskService();
				APP.historyService = processEngine.getHistoryService();
				APP.deploymentManager = conf.getDeploymentManager();
				APP.managementService = processEngine.getManagementService();
				APP.activityBehaviorFactory = conf.getActivityBehaviorFactory();
				APP.addMultipleTask(processInstanceId, processDefinitionId, tempActivityModel.getFirst(),
						tempActivityModel.getLast(), tempActivityModel.getActivityIds(), false, tempActivityModel.getActivitys(), false,null);
			}
		}
		
	}

	@Override
	public void before(ProcessEngineConfigurationImpl conf) {

	}

}