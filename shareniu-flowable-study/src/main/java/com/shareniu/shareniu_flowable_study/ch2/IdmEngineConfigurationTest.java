package com.shareniu.shareniu_flowable_study.ch2;

import java.io.InputStream;

import org.flowable.idm.engine.IdmEngine;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.impl.cfg.StandaloneIdmEngineConfiguration;
import org.junit.Test;

public class IdmEngineConfigurationTest {

	@Test
	public void buildIdmEngine1() {
		InputStream inputStream = IdmEngineConfigurationTest.class.getClassLoader()
				.getResourceAsStream("com/shareniu/shareniu_flowable_study/ch2/flowable.idm.cfg.xml");
		IdmEngineConfiguration idmEngineConfigurationFromInputStream = IdmEngineConfiguration.createIdmEngineConfigurationFromInputStream(inputStream);
		IdmEngine buildIdmEngine = idmEngineConfigurationFromInputStream.buildIdmEngine();
		System.out.println(buildIdmEngine);
	}
	
	@Test
	public void buildIdmEngine2() {
		InputStream inputStream = IdmEngineConfigurationTest.class.getClassLoader()
				.getResourceAsStream("com/shareniu/shareniu_flowable_study/ch2/flowable.idm.cfg.xml");
		IdmEngineConfiguration idmEngineConfigurationFromInputStream = IdmEngineConfiguration.
				createIdmEngineConfigurationFromInputStream(inputStream, "idmEngineConfiguration");
		IdmEngine buildIdmEngine = idmEngineConfigurationFromInputStream.buildIdmEngine();
		System.out.println(buildIdmEngine);
	}
	
	
	@Test
	public void buildIdmEngine3() {
		String resource="com/shareniu/shareniu_flowable_study/ch2/resource.xml";
		IdmEngineConfiguration idmEngineConfigurationFromInputStream = IdmEngineConfiguration.
				createIdmEngineConfigurationFromResource(resource);
		IdmEngine buildIdmEngine = idmEngineConfigurationFromInputStream.buildIdmEngine();
		System.out.println(buildIdmEngine);
	}
	
	@Test
	public void buildIdmEngin4() {
		String resource="com/shareniu/shareniu_flowable_study/ch2/resource2.xml";
		IdmEngineConfiguration idmEngineConfigurationFromInputStream = IdmEngineConfiguration.
				createIdmEngineConfigurationFromResource(resource, "idmEngineConfiguration1");
		IdmEngine buildIdmEngine = idmEngineConfigurationFromInputStream.buildIdmEngine();
		System.out.println(buildIdmEngine);
	}
	
	@Test
	public void buildIdmEngin5() {
		IdmEngineConfiguration ic = IdmEngineConfiguration.
				createStandaloneIdmEngineConfiguration();
		//====  new StandaloneIdmEngineConfiguration() 
		ic.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/idm?useUnicode=true&amp;characterEncoding=UTF-8");
		ic.setJdbcDriver("com.mysql.jdbc.Driver");
		ic.setJdbcPassword("123");
		ic.setJdbcUsername("root");
		IdmEngine buildIdmEngine = ic.buildIdmEngine();
		System.out.println(buildIdmEngine);
	}
}
