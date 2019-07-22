package com.shareniu.v6.ch9;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.ProcessEngineLifecycleListener;

public class ShareniuProcessEngineLifecycleListener implements ProcessEngineLifecycleListener {

	@Override
	public void onProcessEngineBuilt(ProcessEngine processEngine) {
		System.out.println("流程引擎已经构完毕：" + processEngine);
		ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
		System.out.println(processEngineConfiguration);
	}

	@Override
	public void onProcessEngineClosed(ProcessEngine processEngine) {
		System.out.println("流程引擎已经关闭了" + processEngine);
	}

}
