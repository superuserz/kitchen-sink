package com.kitchensink.user.controller;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kitchensink.user.enums.UserRole;
import com.kitchensink.user.exception.AuthenticationException;
import com.kitchensink.user.requests.LoginRequest;
import com.kitchensink.user.requests.LoginResponse;
import com.kitchensink.user.service.LoginService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * The Class AuthController.
 * 
 * @author manmeetdevgun
 * 
 */
@RestController
@RequestMapping("/api")
public class AuthController {

	/** The configured api key. */
	@Value("${api.key}")
	private String configuredApiKey;

	/** The jwt secret. */
	@Value("${jwt.secret}")
	private String jwtSecret;

	/** The jwt expiration ms. */
	@Value("${jwt.expiration}") // 1 hour in milliseconds
	private long jwtExpirationMs;

	/** The login service. */
	@Autowired
	private LoginService loginService;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	/**
	 * Gets the access token.
	 *
	 * @param apiKey the api key
	 * @return the access token
	 */
	@Operation(summary = "Generate Authentication Token")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "JWT token generated successfully"),
			@ApiResponse(responseCode = "401", description = "Invalid API key"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/token")
	public ResponseEntity<?> getAccessToken(@RequestHeader("X-API-KEY") String apiKey) {
		if (apiKey.equals(configuredApiKey)) {
			// Generate JWT token
			String jwtToken = Jwts.builder().setSubject("api-user").claim("roles", List.of("ROLE_" + UserRole.ADMIN))
					.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
					.signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256).compact();

			return ResponseEntity.ok(new LoginResponse(jwtToken));
		} else {
			throw new AuthenticationException("Invalid API Key");
		}
	}

	/**
	 * Login.
	 *
	 * @param request the request
	 * @return the response entity
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
		try {
			String jwtToken = loginService.login(request.getEmail(), request.getPassword());

			// Create secure cookie
			ResponseCookie cookie = ResponseCookie.from("auth_token", jwtToken).httpOnly(true).path("/")
					.maxAge(Duration.ofDays(1)).build();

			response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

			return ResponseEntity.ok().build();
		} catch (AuthenticationException e) {
			LOGGER.error("Authentication Failure", e.getMessage());
			throw e;
		}
	}
}
