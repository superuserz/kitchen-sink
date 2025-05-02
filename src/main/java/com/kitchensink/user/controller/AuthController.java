package com.kitchensink.user.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
public class AuthController {

	@Value("${api.key:bXlhcGlrZXk=}")
	private String configuredApiKey;

	@Value("${jwt.secret:c2xrYWRqbGt3cWpkb2xqZGlvd3F4bndxb25jcXdkandxO29qZGxxd2pkcW93aWQ=}")
	private String jwtSecret;

	@Value("${jwt.expiration:3600000}") // 1 hour in milliseconds
	private long jwtExpirationMs;

	@Autowired
	private LoginService loginService;

	@Operation(summary = "Generate Authentication Token")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "JWT token generated successfully"),
			@ApiResponse(responseCode = "401", description = "Invalid API key"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/api/token")
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

	@PostMapping("/auth/login")
	public String login(@RequestBody @Valid LoginRequest request) {
		return loginService.login(request.getEmail(), request.getPassword());
	}
}
