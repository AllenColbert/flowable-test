package com.shareniu.v6.ch10.core;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineLifecycleListener;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;

import com.alibaba.fastjson.JSON;
import com.shareniu.v6.ch10.AddNode;
import com.shareniu.v6.ch10.model.ActCreation;
import com.shareniu.v6.ch10.model.TempActivityModel;

public class ShareniuProcessEngineLifecycleListener implements ProcessEngineLifecycleListener {

	@Override
	public void onProcessEngineBuilt(ProcessEngine processEngine) {
		System.out.println("流程引擎已经构完毕：" + processEngine);
		ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine
				.getProcessEngineConfiguration();
		SqlSessionFactory sqlSessionFactory = processEngineConfiguration.getSqlSessionFactory();
		SqlSession sqlSession = sqlSessionFactory.openSession();
		List<ActCreation> selectList = sqlSession.selectList("com.shareniu.v6.ch10.mapper.CreationMapper.findAll",
				null);
		for (ActCreation actCreation : selectList) {
			String processDefinitionId = actCreation.getProcessDefinitionId();
			String processInstanceId = actCreation.getProcessInstanceId();
			String processText = actCreation.getProcessText();
			TempActivityModel tempActivityModel = JSON.parseObject(processText, TempActivityModel.class);
			AddNode addNode = new AddNode();
			addNode.addMulitiNode(processDefinitionId, processInstanceId, processEngine,
					tempActivityModel.getActivitys(), tempActivityModel.getFirst(), tempActivityModel.getLast(),
					false,false,null,null);
		}

	}

	@Override
	public void onProcessEngineClosed(ProcessEngine processEngine) {
		System.out.println("流程引擎已经关闭了" + processEngine);
	}

}
