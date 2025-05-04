package com.kitchensink.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class KitchenSinkUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(KitchenSinkUserApplication.class, args);
	}

}
