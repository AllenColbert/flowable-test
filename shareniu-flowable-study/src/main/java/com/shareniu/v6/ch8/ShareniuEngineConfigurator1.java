package com.shareniu.v6.ch8;

import org.flowable.engine.common.impl.AbstractEngineConfiguration;
import org.flowable.engine.common.impl.EngineConfigurator;

public class ShareniuEngineConfigurator1 implements EngineConfigurator {

	@Override
	public void beforeInit(AbstractEngineConfiguration engineConfiguration) {
		//databaseSchemaUpdate
	
		engineConfiguration.setDatabaseSchemaUpdate("true");
		
		String databaseSchemaUpdate = engineConfiguration.getDatabaseSchemaUpdate();
		if (databaseSchemaUpdate.equals("false")) {
			throw new RuntimeException("databaseSchemaUpdate属性必须为true");
		}
		System.out.println("ShareniuEngineConfigurator1:beforeInit");
	}

	@Override
	public void configure(AbstractEngineConfiguration engineConfiguration) {
		System.out.println("ShareniuEngineConfigurator1:configure");
	}

	@Override
	public int getPriority() {
		return 1;
	}

}
