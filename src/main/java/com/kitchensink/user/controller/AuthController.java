package com.kitchensink.user.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Controller
public class AuthController {

	@Value("${api.key:bXlhcGlrZXk=}")
	private String configuredApiKey;

	@Value("${jwt.secret:c2xrYWRqbGt3cWpkb2xqZGlvd3F4bndxb25jcXdkandxO29qZGxxd2pkcW93aWQ=}")
	private String jwtSecret;

	@Value("${jwt.expiration:3600000}") // 1 hour in milliseconds
	private long jwtExpirationMs;

	@Operation(summary = "Generate Authentication Token")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "JWT token generated successfully"),
			@ApiResponse(responseCode = "401", description = "Invalid API key"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/auth/token")
	public ResponseEntity<String> getAccessToken(@RequestHeader("X-API-KEY") String apiKey) {
		if (apiKey.equals(configuredApiKey)) {
			// Generate JWT token
			String jwtToken = Jwts.builder().setSubject("api-user").setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
					.signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256).compact();

			return ResponseEntity.ok(jwtToken);
		} else {
			return ResponseEntity.status(401).body("Invalid API key");
		}
	}
}
