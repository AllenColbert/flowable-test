package com.shareniu.shareniu_flowable_study.ch2;

import java.util.List;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.IdmManagementService;
import org.flowable.idm.api.PrivilegeMapping;
import org.flowable.idm.api.Token;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.IdmEngine;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.IdmEngines;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.TokenEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.junit.Before;
import org.junit.Test;

public class ProcessengineTest {
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
		System.out.println(idmEngine);
	}

	/**
	 * 创建用户
	 */
	@Test
	public void saveUser() {
		String userId = "1";
		// idmIdentityService.newUser(userId);

		UserEntityImpl user = new UserEntityImpl();
		user.setId(userId);
		String email = "1@qq.com";
		user.setEmail(email);
		String pwd = "1";
		user.setPassword(pwd);
		user.setRevision(0);
		idmIdentityService.saveUser(user);
	}

	/**
	 * 创建组
	 */
	@Test
	public void saveGroup() {
		GroupEntityImpl group = new GroupEntityImpl();
		group.setId("1");
		group.setName("研发部门");
		group.setType("a");
		group.setRevision(0);
		idmIdentityService.saveGroup(group);
	}

	/**
	 * 创建用户与组的关联关系
	 */
	@Test
	public void createMembership() {
		idmIdentityService.createMembership("1", "1");
	}

	@Test
	public void deleteGroup() {
		String groupId = "1";
		idmIdentityService.deleteGroup(groupId);
	}

	/**
	 * 更新用户信息
	 */
	@Test
	public void updateUser() {
		String userId = "1";
		UserEntityImpl user = new UserEntityImpl();
		user.setId(userId);
		user.setPassword("2");
		user.setRevision(2);
		idmIdentityService.updateUserPassword(user);
	}

	/**
	 * 查询用户
	 */
	@Test
	public void selectUsers() {
		List<User> users = idmIdentityService.createUserQuery().list();
		for (User user : users) {
			System.out.println(user.getId());
		}
	}
	/**
	 * 设置token
	 */
	@Test
	public void saveToken() {
		TokenEntityImpl token=new TokenEntityImpl();
		String ipAddress="127.0.0.1";
		token.setIpAddress(ipAddress);
		token.setUserId("1");
		token.setRevision(0);
		idmIdentityService.saveToken(token);
	}
	
	
	/**
	 * 删除token
	 */
	@Test
	public void deleteToken() {
		
		String tokenId="2d01347a-2513-11e8-93ce-00e04c3600d8";
		idmIdentityService.deleteToken(tokenId);
	}
	/**
	 * 创建权限
	 */
	@Test
	public void createPrivilege() {
		
		idmIdentityService.createPrivilege("2");
	}
	/**
	 * 给用户添加权限
	 */
	@Test
	public void addUserPrivilegeMapping() {
		String privilegeId="c2bd6ba6-2513-11e8-a680-00e04c3600d8";
		String userId="2";
		idmIdentityService.addUserPrivilegeMapping(privilegeId,  userId);
	}
	/**
	 * select * from ACT_ID_PRIV_MAPPING where PRIV_ID_ = ? 
	 */
	@Test
	public void getPrivilegeMappingsByPrivilegeId() {
		String privilegeId="c2bd6ba6-2513-11e8-a680-00e04c3600d8";
		List<PrivilegeMapping> list=	idmIdentityService.getPrivilegeMappingsByPrivilegeId(privilegeId);
		for (PrivilegeMapping privilegeMapping : list) {
			System.out.println(privilegeMapping.getPrivilegeId()+"###");
			System.out.println(privilegeMapping.getUserId()+"###");
		}
	}
}
