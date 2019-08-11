package com.power.service.impl;

import com.power.service.PowerIdmService;
import com.power.util.Result;
import com.power.util.ResultCode;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author : xuyunfeng
 * @date :   2019/7/19 10:00
 */
@Service
public class PowerIdmServiceImpl implements PowerIdmService {

    @Autowired
    private IdmIdentityService idmIdentityService;

    @Autowired
    private HttpSession session;

/*    public PowerIdmServiceImpl(IdmIdentityService idmIdentityService) {
        this.idmIdentityService = idmIdentityService;
    }*/


    @Override
    public Result login(String userId, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = idmIdentityService.createUserQuery().userId(userId).singleResult();
        if (user == null ){
            return Result.failure(ResultCode.USER_LOGIN_ERROR);
        }
        if(idmIdentityService.checkPassword(userId, password)){
            session.setAttribute("user",user);
            return Result.success();
        }else{
            return Result.failure(ResultCode.USER_LOGIN_ERROR);
        }
    }

    @Override
    public Object logout() {
        session.removeAttribute("user");
        return "用户退出";
    }

    @Override
    public Result checkCurrentUser() {
        User user = null;
        try {
            user = (User) session.getAttribute("user");
        } catch (Exception e) {
            return Result.failure(ResultCode.RESULT_DATA_NONE);
        }

        if (user == null || user.getId() == null) {
            return Result.failure(ResultCode.USER_NOT_LOGGED_IN);
        }

        return Result.success(user.getId());
    }

    @Override
    public Object saveUser(UserEntityImpl user) {

        Object queryResult = queryUserById(user.getId());

        String wrongMsg =  "该用户Id已存在，请勿重复插入";

        if (queryResult != null){ return wrongMsg; }

        //后台判断版本为0的时候才会执行新增用户请求,否则会报错
        if (user.getRevision() != 0){ user.setRevision(0); }

        idmIdentityService.saveUser(user);

        return queryUserById(user.getId());
    }

    @Override
    public Object saveGroup(GroupEntityImpl group) {
        Object queryResult = queryGroupById(group.getId());

        String wrongMsg = "该组织ID已存在，请勿重复插入";

        if (queryResult!= null){ return wrongMsg; }

        //后台判断版本为0的时候才会执行新增请求,否则会报错
        if (group.getRevision() != 0){ group.setRevision(0); }

        idmIdentityService.saveGroup(group);

        return queryGroupById(group.getId());
    }

    @Override
    public Object queryUserById(String userId) {
        return idmIdentityService.createUserQuery().userId(userId).singleResult();
    }

    @Override
    public Object queryGroupById(String groupId) {
       return idmIdentityService.createGroupQuery().groupId(groupId).singleResult();
    }

    @Override
    public Object createMembership(String userId, String groupId) {
        idmIdentityService.createMembership(userId,groupId);
        return "用户组织成功关联";
    }

    @Override
    public Object deleteUserById(String userId) {
        idmIdentityService.deleteUser(userId);
        return "成功删除用户";
    }

    @Override
    public Object deleteGroupById(String groupId) {
        idmIdentityService.deleteGroup(groupId);
        return "成功删除组织";
    }




}
