package com.kitchensink.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	JwtAuthConverter jwtAuthConverter;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(t -> t.disable());
		http.authorizeHttpRequests(authorize -> {
			authorize.requestMatchers(HttpMethod.GET, "/rest/members").permitAll()
					.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll().anyRequest().authenticated();
		});

		http.oauth2ResourceServer(t -> {
			t.jwt(configurer -> configurer.jwtAuthenticationConverter(jwtAuthConverter));
		});

		http.sessionManagement(t -> {
			t.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		});
		return http.build();
	}
}