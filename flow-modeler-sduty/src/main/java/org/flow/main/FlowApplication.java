package org.flow.main;

import org.flow.config.ApplicationConfiguration;
import org.flow.servlet.AppDispatcherServletConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Description:
 * @Author: Bruce.liu
 * @Since:9:18 2019/1/19
 */
@Import({
        ApplicationConfiguration.class,
        AppDispatcherServletConfiguration.class
})
@SpringBootApplication
@ComponentScan(basePackages = {"org.flow"})
@EnableTransactionManagement
public class FlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowApplication.class, args);
        System.out.print("程序正在运行。。。。。。。。。。。。。");
    }
}
