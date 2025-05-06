package com.kitchensink.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitchensink.user.exception.AuthenticationException;
import com.kitchensink.user.exception.handler.GlobalExceptionHandler;
import com.kitchensink.user.requests.LoginRequest;
import com.kitchensink.user.service.LoginService;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@InjectMocks
	private AuthController authController;

	@Mock
	private LoginService loginService;

	private MockMvc mockMvc;

	@Mock
	private HttpServletResponse response;

	@Captor
	private ArgumentCaptor<String> headerNameCaptor;

	@Captor
	private ArgumentCaptor<String> headerValueCaptor;

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
		// Arrange
		String email = "user@example.com";
		String password = "password";
		String jwtToken = "mock-jwt-token";

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail(email);
		loginRequest.setPassword(password);

		when(loginService.login(email, password)).thenReturn(jwtToken);

		// Act
		ResponseEntity<?> responseEntity = authController.login(loginRequest, response);

		// Assert
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		verify(response).addHeader(headerNameCaptor.capture(), headerValueCaptor.capture());

		String headerName = headerNameCaptor.getValue();
		String headerValue = headerValueCaptor.getValue();

		assertEquals(HttpHeaders.SET_COOKIE, headerName);
		assertTrue(headerValue.contains("auth_token=" + jwtToken));
		assertTrue(headerValue.contains("HttpOnly"));
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