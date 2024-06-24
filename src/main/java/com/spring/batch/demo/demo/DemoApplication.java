package com.spring.batch.demo.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
//@ComponentScan(basePackages = {
//		"com.spring.batch.demo.demo.BatchEntities",
//		"com.spring.batch.demo.demo.BatchHelper",
//		"com.spring.batch.demo.demo.BatchRepository",
//		"com.spring.batch.demo.demo.Config",
//		"com.spring.batch.demo.demo.Controller"  // Include any other packages you need
//})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
