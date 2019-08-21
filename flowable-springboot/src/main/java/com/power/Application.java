package com.power;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author xuyunfeng
 */
@SpringBootApplication
@EnableSwagger2
public class Application {

	public static void main(String[] args){
		SpringApplication.run(Application.class,args);
	}
}
