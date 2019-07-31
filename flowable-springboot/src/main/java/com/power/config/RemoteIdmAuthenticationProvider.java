/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.power.config;

import org.flowable.common.engine.api.FlowableException;
import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.service.idm.RemoteIdmService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author
 * 远程身份验证提供程序
 */
@Component
public class RemoteIdmAuthenticationProvider implements AuthenticationProvider {

    private final RemoteIdmService remoteIdmService;

    public RemoteIdmAuthenticationProvider(RemoteIdmService remoteIdmService) {
        this.remoteIdmService = remoteIdmService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        RemoteUser user = remoteIdmService.authenticateUser(authentication.getPrincipal().toString(), authentication.getCredentials().toString());
        if (user == null) {
            throw new FlowableException("user not found " + authentication.getPrincipal());
        }

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String privilege : user.getPrivileges()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(privilege));
        }

        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(), grantedAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
