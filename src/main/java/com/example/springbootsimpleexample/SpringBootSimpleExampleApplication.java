package com.example.springbootsimpleexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.example.springbootsimpleexample.repository")
@EntityScan("com.example.springbootsimpleexample.model")
@SpringBootApplication
public class SpringBootSimpleExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSimpleExampleApplication.class, args);
	}
}
