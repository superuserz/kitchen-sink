package com.kitchensink.user.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.exception.ValidationException;
import com.kitchensink.user.requests.MemberCriteriaRequest;
import com.kitchensink.user.requests.RegisterMemberRequest;
import com.kitchensink.user.service.MemberRegistrationService;
import com.kitchensink.user.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * The Class MemberController.
 * 
 * @author manmeetdevgun
 */
@RestController
@Tag(name = "Member Management", description = "Operations related to member registration and retrieval")
@RequestMapping("/api")
public class MemberController {

	/** The member service. */
	@Autowired
	MemberService memberService;

	/** The member registration service. */
	@Autowired
	MemberRegistrationService memberRegistrationService;

	/**
	 * List all members.
	 *
	 * @return the list
	 */
	@Operation(summary = "Get all registered members")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "List of members retrieved successfully"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/members")
	@PreAuthorize("hasRole('ADMIN')")
	public List<Member> listAllMembers() {
		return memberService.listAllMembers();
	}

	/**
	 * Lookup member by id.
	 *
	 * @param id the id
	 * @return the member
	 */
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

	/**
	 * Delete member by ID.
	 *
	 * @param id the id of the member to delete
	 * @return the response entity
	 */
	@Operation(summary = "Delete Member by ID", description = "Delete a member using their ID.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully deleted member"),
			@ApiResponse(responseCode = "404", description = "Member not found") })
	@DeleteMapping("/members/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteMemberById(@PathVariable String id) {
		boolean deleted = memberService.deleteMemberById(id);
		if (deleted) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

	}

	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	@Operation(summary = "Fetch User Profile", description = "Fetch User Profile")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved member"),
			@ApiResponse(responseCode = "404", description = "Member not found") })
	@GetMapping("/member/me")
	public Member getProfile() {
		return memberService.getProfile();
	}

	/**
	 * Register.
	 *
	 * @param member        the member
	 * @param bindingResult the binding result
	 * @return the response entity
	 */
	@Operation(summary = "Register a new member")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Member registered successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input or duplicate email"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PostMapping("/member/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterMemberRequest member, BindingResult bindingResult) {
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

	/**
	 * Validate request.
	 *
	 * @param bindingResult the binding result
	 * @param member        the member
	 */
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

	/**
	 * Export members report.
	 *
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@GetMapping("/members/export")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<InputStreamResource> exportMembersReport() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=members.xlsx");
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		ByteArrayOutputStream savedReportStream = new ByteArrayOutputStream();

		Workbook workbook = memberService.exportMembersReport();

		try {
			workbook.write(savedReportStream);
			workbook.close();
		} catch (IOException e) {
			throw new Exception("Error while writing in output stream");
		}
		InputStream inputStream = new ByteArrayInputStream(savedReportStream.toByteArray());
		InputStreamResource reportStream = new InputStreamResource(inputStream);
		return ResponseEntity.ok().headers(headers).body(reportStream);
	}

	/**
	 * List members by criteria.
	 *
	 * @param criteria the criteria
	 * @return the page
	 */

	@Operation(summary = "Search members with filters, pagination and sorting")
	@ApiResponse(responseCode = "200", description = "Successful search")
	@PostMapping("/members/search")
	@PreAuthorize("hasRole('ADMIN')")
	public Page<Member> listMembersByCriteria(@RequestBody MemberCriteriaRequest criteria) {
		return memberService.searchMembers(criteria);
	}
}
