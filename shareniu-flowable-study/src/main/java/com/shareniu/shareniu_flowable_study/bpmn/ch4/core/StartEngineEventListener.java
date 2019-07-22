package com.shareniu.shareniu_flowable_study.bpmn.ch4.core;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;


/**
 * 流程引擎启动监听器
 */
public interface StartEngineEventListener
{

	void after(ProcessEngineConfigurationImpl conf,ProcessEngine processEngine);
	void before(ProcessEngineConfigurationImpl conf);

}
