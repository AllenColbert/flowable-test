package com.power.service;

import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;

/**
 * @author : xuyunfeng
 * @date :   2019/7/19 10:00
 */
public interface IdmService {

    Object saveUser(UserEntityImpl user);

    Object saveGroup(GroupEntityImpl group);

    Object queryUserById(String userId);

    Object queryGroupById(String groupId);

    Object createMembership(String userId,String groupId);

    Object deleteUserById(String userId);

    Object deleteGroupById(String groupId);

    Object login(String userId, String password);

    Object logout();
}
