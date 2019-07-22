package com.shareniu.shareniu_flowable_study.bpmn.ch4.core;

import java.util.List;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;

public class ProcessEngineConfigurationEx extends StandaloneProcessEngineConfiguration {
	private List<StartEngineEventListener> startEngineEventListener;
	@Override
	public ProcessEngine buildProcessEngine() {
		 ProcessEngine pe = super.buildProcessEngine();
		 System.out.println(startEngineEventListener);
		 for (StartEngineEventListener listener : startEngineEventListener) {
				listener.after(this, pe);
			}
		return pe;
	}
	public List<StartEngineEventListener> getStartEngineEventListener() {
		return startEngineEventListener;
	}
	public void setStartEngineEventListener(List<StartEngineEventListener> startEngineEventListener) {
		this.startEngineEventListener = startEngineEventListener;
	}
	
	
	
	
}
