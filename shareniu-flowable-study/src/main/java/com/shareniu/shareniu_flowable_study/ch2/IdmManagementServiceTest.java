package com.shareniu.shareniu_flowable_study.ch2;

import java.util.Map;
import java.util.Set;

import org.flowable.engine.common.api.management.TableMetaData;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.IdmManagementService;
import org.flowable.idm.api.Token;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.IdmEngine;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.IdmEngines;
import org.junit.Before;
import org.junit.Test;


public class IdmManagementServiceTest {
	IdmEngine idmEngine = null;
	IdmEngineConfiguration idmEngineConfiguration;
	IdmManagementService idmManagementService;
	IdmIdentityService idmIdentityService;

	@Before
	public void init() {
		IdmEngine idmEngine = IdmEngines.getDefaultIdmEngine();
		idmEngineConfiguration = idmEngine.getIdmEngineConfiguration();
		idmManagementService = idmEngine.getIdmManagementService();
		idmIdentityService = idmEngine.getIdmIdentityService();
	}
	@Test
	public void getTableCount() {
		Map<String, Long> tableCount = idmManagementService.getTableCount();
		Set<String> keySet = tableCount.keySet();
		for (String string : keySet) {
			System.out.println("#####"+keySet+",value:"+tableCount.get(string));
			
		}
		//System.out.println(tableCount);
	}
	
	@Test
	public void getTableName() {
		String tableName = idmManagementService.getTableName(User.class);
		System.out.println(tableName);
		tableName =idmManagementService.getTableName(Token.class);
		System.out.println(tableName);
	}
	
	@Test
	public void getTableMetaData() {
		TableMetaData tmd = idmManagementService.getTableMetaData("ACT_ID_USER");
		System.out.println(tmd.getTableName());
		System.out.println(tmd.getColumnNames());
		System.out.println(tmd.getColumnTypes());
	}
	
	/**
	 * 这个是flowable框架的一个bug ，
	 */
	@Test
	public void getProperties() {
		Map<String, String> tmd = idmManagementService.getProperties();
		System.out.println(tmd);
	}
	
	
	
	
	
	
	
	
	
}
