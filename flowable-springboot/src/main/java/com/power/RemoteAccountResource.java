package com.power;

import org.flowable.idm.api.User;
import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.model.UserRepresentation;
import org.flowable.ui.common.security.DefaultPrivileges;
import org.flowable.ui.common.security.SecurityUtils;
import org.flowable.ui.common.service.exception.NotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongyizhou on 2019/3/26 12:28
 * E-mail: xiongyizhou@powerpms.com
 *
 * @author xiongyizhou
 */
@RestController
@RequestMapping("app")
public class RemoteAccountResource {
    /**
     * GET /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/account", method = RequestMethod.GET, produces = "application/json")
    public UserRepresentation getAccount() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName("admin");
        userRepresentation.setLastName("admin");
        userRepresentation.setFullName("admin");
        userRepresentation.setId("admin");
        List<String> pris = new ArrayList<>();
        pris.add(DefaultPrivileges.ACCESS_MODELER);
        pris.add(DefaultPrivileges.ACCESS_IDM);
        pris.add(DefaultPrivileges.ACCESS_ADMIN);
        pris.add(DefaultPrivileges.ACCESS_TASK);
        pris.add(DefaultPrivileges.ACCESS_REST_API);
        userRepresentation.setPrivileges(pris);
        User user = new RemoteUser();
        user.setId("admin");
        user.setFirstName("admin");
        user.setLastName("admin");
        ((RemoteUser) user).setPrivileges(pris);
        SecurityUtils.assumeUser(user);
        if (userRepresentation != null) {
            return userRepresentation;
        } else {
            throw new NotFoundException();
        }
    }

    @RequestMapping(value = "/logout")
    public void logout(){
        SecurityUtils.clearAssumeUser();
    }
}
