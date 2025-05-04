package com.kitchensink.user.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kitchensink.user.enums.UserRole;
import com.kitchensink.user.requests.LoginRequest;
import com.kitchensink.user.service.impl.LoginService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

	@Value("${api.key}")
	private String configuredApiKey;

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration}") // 1 hour in milliseconds
	private long jwtExpirationMs;

	@Autowired
	private LoginService loginService;

	@Operation(summary = "Generate Authentication Token")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "JWT token generated successfully"),
			@ApiResponse(responseCode = "401", description = "Invalid API key"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/token")
	public ResponseEntity<String> getAccessToken(@RequestHeader("X-API-KEY") String apiKey) {
		if (apiKey.equals(configuredApiKey)) {
			// Generate JWT token
			String jwtToken = Jwts.builder().setSubject("api-user").claim("roles", List.of("ROLE_" + UserRole.ADMIN))
					.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
					.signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256).compact();

			return ResponseEntity.ok(jwtToken);
		} else {
			return ResponseEntity.status(401).body("Invalid API key");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
		try {
			String jwtToken = loginService.login(request.getEmail(), request.getPassword());

			Map<String, String> response = new HashMap<>();
			response.put("token", jwtToken);

			return ResponseEntity.ok(response); // 200 OK with JWT
		} catch (Exception e) {
			// Custom exception for invalid username/password
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
		}
	}
}
