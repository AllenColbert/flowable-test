package com.power.service;


import com.power.util.Result;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;


/**
 * @author : xuyunfeng
 * @date :   2019/7/19 10:00
 */
public interface PowerIdmService {

    /**
     * 登陆
     * @param userId 用户名
     * @param password 密码
     * @return resukt
     */
    Result login(String userId, String password);

    /**
     * 登出
     * @return result
     */
    Result logout();

    /**
     * 查询当前session中的user
     * @return result
     */
    Result checkCurrentUser();

/*#######################################################################################*/
    /**
     * 新增用户信息
     * @param user 用户实例
     * @return xx
     */
    Object saveUser(UserEntityImpl user);

    /**新增组织信息
     * @param group 组织
     * @return xx
     */
    Object saveGroup(GroupEntityImpl group);

    /**
     * 根据用户Id查询用户
     * @param userId 用户Id
     * @return xx
     */
    Object queryUserById(String userId);

    /**
     * 根据Id查询组织
     * @param groupId 组织Id
     * @return xx
     */
    Object queryGroupById(String groupId);

    /**
     * 创建用户和组织关系
     * @param userId 用户id
     * @param groupId 组织id
     * @return xx
     */
    Object createMembership(String userId,String groupId);

    /**
     * 根据Id删除用户
     * @param userId 用户id
     * @return xx
     */
    Object deleteUserById(String userId);

    /**
     * 根据Id删除组织
     * @param groupId 组织Id
     * @return xx
     */
    Object deleteGroupById(String groupId);



}
