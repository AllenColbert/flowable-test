package com.shareniu.v6.ch10.core;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.flowable.engine.common.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.common.api.delegate.event.FlowableEvent;
import org.flowable.engine.common.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;

import com.shareniu.v6.ch10.mapper.CreationMapper;

public class ShareniuEventListener implements FlowableEventListener {

	protected ProcessEngineConfigurationImpl processEngineConfigurationImpl;
	
	
	@Override
	public void onEvent(FlowableEvent event) {
		System.out.println(processEngineConfigurationImpl);
		FlowableEngineEventType  type = (FlowableEngineEventType) event.getType();
		System.out.println(type);
		switch (type) {
		case PROCESS_COMPLETED:
			System.out.println(event);
			FlowableEntityEventImpl flowableEntityEventImpl=(FlowableEntityEventImpl) event;
			String processInstanceId = flowableEntityEventImpl.getProcessInstanceId();
			System.out.println(processInstanceId);
			SqlSessionFactory sqlSessionFactory = processEngineConfigurationImpl.getSqlSessionFactory();
			SqlSession sqlSession = sqlSessionFactory.openSession();
			CreationMapper mapper = sqlSession.getMapper(CreationMapper.class);
			mapper.updateState(processInstanceId);
			sqlSession.commit();
			sqlSession.close();
			break;

		default:
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
	public void setProcessEngineConfigurationImpl(ProcessEngineConfigurationImpl processEngineConfigurationImpl) {
		this.processEngineConfigurationImpl = processEngineConfigurationImpl;
	}
	public ProcessEngineConfigurationImpl getProcessEngineConfigurationImpl() {
		return processEngineConfigurationImpl;
	}

}
