package com.shareniu.v6.ch8;

import org.flowable.engine.common.impl.AbstractEngineConfiguration;
import org.flowable.engine.common.impl.EngineConfigurator;

public class ShareniuEngineConfigurator2 implements EngineConfigurator {

	@Override
	public void beforeInit(AbstractEngineConfiguration engineConfiguration) {
		System.out.println("ShareniuEngineConfigurator2:beforeInit");
	}

	@Override
	public void configure(AbstractEngineConfiguration engineConfiguration) {
		System.out.println("ShareniuEngineConfigurator2:configure");
	}

	@Override
	public int getPriority() {
		return 2;
	}

}
