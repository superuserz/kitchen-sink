package com.kitchensink.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitchensink.user.controller.AuthController;
import com.kitchensink.user.enums.UserRole;
import com.kitchensink.user.exception.AuthenticationException;
import com.kitchensink.user.exception.handler.GlobalExceptionHandler;
import com.kitchensink.user.requests.LoginRequest;
import com.kitchensink.user.service.LoginService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@InjectMocks
	private AuthController authController;

	@Mock
	private LoginService loginService;

	private MockMvc mockMvc;

	private final String jwtSecret = "ThisIsASecretKeyForJwtThatIsLongEnough";
	private final long jwtExpirationMs = 3600000L;
	private final String configuredApiKey = "my-secret-api-key";

	@BeforeEach
	void setUp() {
		// Inject private @Value fields
		ReflectionTestUtils.setField(authController, "jwtSecret", jwtSecret);
		ReflectionTestUtils.setField(authController, "jwtExpirationMs", jwtExpirationMs);
		ReflectionTestUtils.setField(authController, "configuredApiKey", configuredApiKey);

		mockMvc = MockMvcBuilders.standaloneSetup(authController).setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	void testGetAccessToken_validApiKey_returnsToken() throws Exception {
		mockMvc.perform(get("/api/token").header("X-API-KEY", configuredApiKey)).andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}

	@Test
	void testGetAccessToken_invalidApiKey_returns401() throws Exception {
		mockMvc.perform(get("/api/token").header("X-API-KEY", "wrong-key")).andExpect(status().isUnauthorized());
	}

	@Test
	void testLogin_validCredentials_returnsToken() throws Exception {
		LoginRequest request = new LoginRequest();
		request.setEmail("test@example.com");
		request.setPassword("Secure@2021");

		String jwt = Jwts.builder().setSubject("test@example.com").claim("roles", List.of("ROLE_" + UserRole.USER))
				.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
				.signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes())).compact();

		when(loginService.login(request.getEmail(), request.getPassword())).thenReturn(jwt);

		mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value(jwt));
	}

	@Test
	void testLogin_invalidCredentials_returns401() throws Exception {
		LoginRequest request = new LoginRequest();
		request.setEmail("wrong@example.com");
		request.setPassword("wrongPass");

		when(loginService.login(any(), any())).thenThrow(new AuthenticationException("Invalid credentials"));

		mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(request))).andExpect(status().isUnauthorized());
	}
}