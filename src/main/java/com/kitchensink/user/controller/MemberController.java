package com.kitchensink.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.requests.RegisterMemberRequest;
import com.kitchensink.user.service.MemberRegistrationService;
import com.kitchensink.user.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController(value = "/kitchensink")
@Tag(name = "Member Management", description = "Operations related to member registration and retrieval")
public class MemberController {

	@Autowired
	MemberService memberService;

	@Autowired
	MemberRegistrationService memberRegistrationService;

	@Operation(summary = "Get all registered members")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "List of members retrieved successfully"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/rest/members")
	public List<Member> getAllMembers() {
		return memberService.getAllMembers();
	}

	@Operation(summary = "Register a new member")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Member registered successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input or duplicate email"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PostMapping("/rest/members")
	public ResponseEntity<String> register(@Valid @RequestBody RegisterMemberRequest request) {
		try {
			memberRegistrationService.register(request);
			return ResponseEntity.status(201).body("Member registered successfully");
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			return ResponseEntity.internalServerError().body("Unexpected error occurred");
		}
	}

}
