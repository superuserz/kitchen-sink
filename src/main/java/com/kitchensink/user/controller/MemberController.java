package com.kitchensink.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.exception.ValidationException;
import com.kitchensink.user.requests.RegisterMemberRequest;
import com.kitchensink.user.service.MemberRegistrationService;
import com.kitchensink.user.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Member Management", description = "Operations related to member registration and retrieval")
@SecurityRequirement(name = "Keycloak")
public class MemberController {

	@Autowired
	MemberService memberService;

	@Autowired
	MemberRegistrationService memberRegistrationService;

	@Operation(summary = "Get all registered members")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "List of members retrieved successfully"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/rest/members")
	@PreAuthorize("hasRole('ADMIN')")
	public List<Member> listAllMembers() {
		return memberService.listAllMembers();
	}

	@Operation(summary = "Lookup Member by ID", description = "Retrieve a member by their ID.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved member"),
			@ApiResponse(responseCode = "404", description = "Member not found") })
	@GetMapping("/members/{id}")
	public Member lookupMemberById(@PathVariable String id) {
		Member member = memberService.lookupMemberById(id);
		if (member == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
		}
		return member;
	}

	@Operation(summary = "Register a new member")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Member registered successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input or duplicate email"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PostMapping("/rest/members")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> createMember(@Valid @RequestBody RegisterMemberRequest member,
			BindingResult bindingResult) {
		try {

			validateRequest(bindingResult, member);
			memberRegistrationService.register(member);
			return ResponseEntity.ok().build();
		} catch (ValidationException e) {
			Map<String, String> response = new HashMap<>();
			e.getErrors().entrySet().forEach(entry -> {
				response.put(entry.getKey(), entry.getValue());
			});
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("error", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

	private void validateRequest(BindingResult bindingResult, RegisterMemberRequest member) {
		Map<String, String> errors = new HashMap<>();
		if (bindingResult.hasErrors()) {
			bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
			throw new ValidationException(errors);
		} else {
			errors.put("email", "Email Taken");
			if (memberRegistrationService.isEmailExists(member.getEmail())) {
				throw new ValidationException(errors);
			}
		}
	}

}
