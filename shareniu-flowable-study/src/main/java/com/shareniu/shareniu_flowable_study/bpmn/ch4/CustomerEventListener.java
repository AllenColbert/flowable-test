package com.shareniu.shareniu_flowable_study.bpmn.ch4;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.flowable.engine.common.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.common.api.delegate.event.FlowableEvent;
import org.flowable.engine.common.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;

import com.shareniu.shareniu_flowable_study.bpmn.ch4.core.ProcessEngineConfigurationEx;
import com.shareniu.shareniu_flowable_study.bpmn.ch4.mapper.CreationMapper;

public class CustomerEventListener implements FlowableEventListener {
	protected ProcessEngineConfigurationEx processEngineConfiguration;
	@Override
	public void onEvent(FlowableEvent event) {
		FlowableEngineEventType engineEventType = (FlowableEngineEventType) event.getType();
		System.out.println("#################################################"+engineEventType);
		System.out.println("#################################################processEngineConfiguration:"+processEngineConfiguration);
		switch (engineEventType) {
		case PROCESS_COMPLETED:
			System.out.println(event);
			FlowableEntityEventImpl flowableEntityEventImpl=(org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl) event;
			DelegateExecution execution = flowableEntityEventImpl.getExecution();
			String executionId = flowableEntityEventImpl.getExecutionId();
			String processDefinitionId = flowableEntityEventImpl.getProcessDefinitionId();
			String processInstanceId = flowableEntityEventImpl.getProcessInstanceId();
			System.out.println(processInstanceId);
			Object entity = flowableEntityEventImpl.getEntity();
			SqlSessionFactory sqlSessionFactory = processEngineConfiguration.getSqlSessionFactory();
			SqlSession sqlSession = sqlSessionFactory.openSession();
			CreationMapper creationMapper = sqlSession.getMapper(CreationMapper.class);
			creationMapper.updateState(processInstanceId);
			sqlSession.commit();
			sqlSession.close();
			break;
		}
	}

	@Override
	public boolean isFailOnException() {
		return false;
	}

	@Override
	public boolean isFireOnTransactionLifecycleEvent() {
		return false;
	}

	@Override
	public String getOnTransaction() {
		return null;
	}

	public ProcessEngineConfigurationEx getProcessEngineConfiguration() {
		return processEngineConfiguration;
	}

	public void setProcessEngineConfiguration(ProcessEngineConfigurationEx processEngineConfiguration) {
		this.processEngineConfiguration = processEngineConfiguration;
	}
	
	
}
