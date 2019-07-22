package com.shareniu.v6.ch8;

import org.flowable.engine.common.impl.AbstractEngineConfiguration;
import org.flowable.engine.common.impl.EngineConfigurator;

public class ShareniuEngineConfigurator3 implements EngineConfigurator {

	@Override
	public void beforeInit(AbstractEngineConfiguration engineConfiguration) {
		//databaseSchemaUpdate
	
	
		System.out.println("ShareniuEngineConfigurator3:beforeInit");
	}

	@Override
	public void configure(AbstractEngineConfiguration engineConfiguration) {
		System.out.println("ShareniuEngineConfigurator3:configure");
	}

	@Override
	public int getPriority() {
		return 3;
	}

}
