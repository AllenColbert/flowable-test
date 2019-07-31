package com.power.config;

import org.flowable.ui.common.properties.FlowableRestAppProperties;
import org.flowable.ui.common.security.ActuatorRequestMatcher;
import org.flowable.ui.common.security.DefaultPrivileges;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Created by xiongyizhou on 2019/3/26 12:16
 * E-mail: xiongyizhou@powerpms.com
 *
 * @author xiongyizhou
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfiguration.class);

    public static final String REST_ENDPOINTS_PREFIX = "/app/rest";

    private final RemoteIdmAuthenticationProvider authenticationProvider;

    public SecurityConfiguration(RemoteIdmAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {

        // Default auth (database backed)
        try {
            auth.authenticationProvider(authenticationProvider);
        } catch (Exception e) {
            LOGGER.error("Could not configure authentication mechanism:", e);
        }
    }

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        final FlowableRestAppProperties restAppProperties;
        final FlowableModelerAppProperties modelerAppProperties;

        public ApiWebSecurityConfigurationAdapter(FlowableRestAppProperties restAppProperties,
                                                  FlowableModelerAppProperties modelerAppProperties) {
            this.restAppProperties = restAppProperties;
            this.modelerAppProperties = modelerAppProperties;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf()
                    .disable();

            http.antMatcher("/api/**").authorizeRequests().antMatchers("/api/**").permitAll();


        }
    }

    @ConditionalOnClass(EndpointRequest.class)
    @Configuration
    @Order(5)
    public static class ActuatorWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf()
                    .disable();

            http.requestMatcher(new ActuatorRequestMatcher())
                    .authorizeRequests()
                    .requestMatchers(EndpointRequest.to(InfoEndpoint.class, HealthEndpoint.class)).authenticated()
                    .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAnyAuthority(DefaultPrivileges.ACCESS_ADMIN)
                    .and()
                    .httpBasic();
        }
    }

}
