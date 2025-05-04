package com.kitchensink.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@Configuration
@EnableMongoAuditing
public class KitchenSinkUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(KitchenSinkUserApplication.class, args);
	}

}
