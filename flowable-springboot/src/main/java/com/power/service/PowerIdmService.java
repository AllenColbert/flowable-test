package com.power.service;


import com.power.util.Result;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author : xuyunfeng
 * @date :   2019/7/19 10:00
 */
public interface PowerIdmService {

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

    /**
     * 登陆
     * @param userId 用户名
     * @param password 密码
     * @param request HttpRequest
     * @param response HttpResponse
     * @return xx
     */
    Result login(String userId, String password, HttpServletRequest request, HttpServletResponse response);

    /**
     * 登出
     * @return xx
     */
    Object logout();

    /**
     * 查询当前session中的user
     * @return
     */
    Result checkCurrentUser();

}
