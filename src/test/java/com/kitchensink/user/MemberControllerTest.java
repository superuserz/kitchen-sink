package com.kitchensink.user;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitchensink.user.controller.MemberController;
import com.kitchensink.user.entity.Member;
import com.kitchensink.user.requests.RegisterMemberRequest;
import com.kitchensink.user.service.MemberRegistrationService;
import com.kitchensink.user.service.MemberService;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberService memberService;

	@MockBean
	private MemberRegistrationService memberRegistrationService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testListAllMembers() throws Exception {
		when(memberService.listAllMembers()).thenReturn(Collections.emptyList());

		mockMvc.perform(MockMvcRequestBuilders.get("/rest/members")).andExpect(status().isOk());
	}

	@Test
	void testLookupMemberById_Found() throws Exception {
		Member mockMember = new Member();
		mockMember.setId("123");
		mockMember.setName("Test User");

		when(memberService.lookupMemberById("123")).thenReturn(mockMember);

		mockMvc.perform(MockMvcRequestBuilders.get("/members/123")).andExpect(status().isOk());
	}

	@Test
	void testCreateMember_Success() throws Exception {
		RegisterMemberRequest request = new RegisterMemberRequest();
		request.setName("John Doe");
		request.setEmail("john@example.com");
		request.setPhoneNumber("1234567890");

		when(memberRegistrationService.isEmailExists(request.getEmail())).thenReturn(false);

		mockMvc.perform(MockMvcRequestBuilders.post("/rest/members").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());
	}

	@Test
	void testCreateMember_EmailExists() throws Exception {
		RegisterMemberRequest request = new RegisterMemberRequest();
		request.setName("Jane");
		request.setEmail("jane@example.com");
		request.setPhoneNumber("9876543210");

		when(memberRegistrationService.isEmailExists(request.getEmail())).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.post("/rest/members").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.email").value("Email Taken"));
	}

	@Test
	public void testCreateMember_ValidationError() throws Exception {
		RegisterMemberRequest memberRequest = new RegisterMemberRequest();
		// Invalid member data that should trigger a validation error (e.g., duplicate
		// email)
		memberRequest.setEmail("duplicate@example.com");

		mockMvc.perform(
				post("/rest/members").contentType(MediaType.APPLICATION_JSON).content(asJsonString(memberRequest)))
				.andExpect(status().isConflict());
	}

	// Utility method to convert an object to a JSON string
	public static String asJsonString(Object obj) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("Failed to convert object to JSON string", e);
		}
	}
}
