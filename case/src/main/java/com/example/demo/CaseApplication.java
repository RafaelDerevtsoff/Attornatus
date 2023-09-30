package com.example.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@OpenAPIDefinition(info = @Info(title = "Attornatus",version = "3.0.0"))
@ComponentScan("com")
public class CaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaseApplication.class, args);
	}

}
