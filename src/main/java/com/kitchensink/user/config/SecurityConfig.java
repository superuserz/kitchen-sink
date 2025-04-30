package com.kitchensink.user.config;

import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableWebSecurity
public class SecurityConfig {

//	@Bean
//	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http.csrf(t -> t.disable());
//		http.authorizeHttpRequests(authorize -> {
//			authorize.requestMatchers(HttpMethod.GET, "/kitchensink/rest/members").permitAll()
//					.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll().anyRequest().authenticated();
//		});
//		http.oauth2ResourceServer(t -> {
//			t.jwt(Customizer.withDefaults());
//		});
//
//		http.sessionManagement(t -> {
//			t.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//		});
//		return http.build();
//	}

//	@Bean
//	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http.csrf(t -> t.disable());
//		http.authorizeHttpRequests(authorize -> {
//			authorize.requestMatchers(HttpMethod.GET, "/kitchensink/rest/members").permitAll()
//					.requestMatchers("/swagger-ui/**").permitAll().anyRequest().authenticated();
//		});
//		http.oauth2ResourceServer(t -> {
//			t.jwt(Customizer.withDefaults());
//		});
//
//		http.sessionManagement(t -> {
//			t.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//		});
//		return http.build();
//	}

//	@Bean
//	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http.csrf(t -> t.disable());
//		http.authorizeHttpRequests(authorize -> {
//			authorize.anyRequest().authenticated();
//		});
//		http.oauth2ResourceServer(t -> {
//			t.jwt(Customizer.withDefaults());
//		});
//
//		http.sessionManagement(t -> {
//			t.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//		});
//		return http.build();
//	}
}