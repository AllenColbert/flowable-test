package com.shareniu.flowable;

import org.flowable.app.properties.FlowableModelerAppProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
        exclude = {
                org.flowable.spring.boot.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                LiquibaseAutoConfiguration.class
        }

)
@ComponentScan(basePackages = {"com.shareniu", "org.flowable.app"})
@MapperScan(basePackages = {"com.shareniu.flowable.dao"})
public class ShareniuFlowableSpringbootWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShareniuFlowableSpringbootWebApplication.class, args);
    }


    @Bean
    public FlowableModelerAppProperties flowableModelerAppProperties() {
        FlowableModelerAppProperties flowableModelerAppProperties = new FlowableModelerAppProperties();
        return flowableModelerAppProperties;
    }

}
