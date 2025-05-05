package com.kitchensink.user.ai;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class GenAITestController {

	@Autowired
	private GenAIService service;

	@Operation(summary = "Generate Test Classes With Spring GEN AI")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Test Class Content Created Successfully"),
			@ApiResponse(responseCode = "401", description = "Invalid API key"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/api/generate/tests")
	public ResponseEntity<String> createTestClass() throws IOException {
		service.generateTestClass();
		return ResponseEntity.ok().body("Request Submitted");
	}

}
