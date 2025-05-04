package com.kitchensink.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitchensink.user.controller.MemberController;
import com.kitchensink.user.entity.Member;
import com.kitchensink.user.requests.RegisterMemberRequest;
import com.kitchensink.user.service.MemberRegistrationService;
import com.kitchensink.user.service.MemberService;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false) // 🔓 disables Spring Security for test
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberService memberService;

	@MockBean
	private MemberRegistrationService memberRegistrationService;

	@Autowired
	private ObjectMapper objectMapper;

	private Member sampleMember;

	@BeforeEach
	void setup() {
		sampleMember = new Member();
		sampleMember.setName("Test User");
		sampleMember.setEmail("test@example.com");
		sampleMember.setPhoneNumber("1234567890");
	}

	@Test
	void testListAllMembers() throws Exception {
		List<Member> members = List.of(sampleMember);
		when(memberService.listAllMembers()).thenReturn(members);

		mockMvc.perform(get("/api/members")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email").value("test@example.com"));
	}

	@Test
	void testLookupMemberById_found() throws Exception {
		when(memberService.lookupMemberById("123")).thenReturn(sampleMember);

		mockMvc.perform(get("/api/members/123")).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Test User"));
	}

	@Test
	void testGetProfile_found() throws Exception {
		when(memberService.getProfile()).thenReturn(sampleMember);

		mockMvc.perform(get("/api/profile")).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("test@example.com"));
	}

	@Test
	void testRegister_success() throws Exception {
		RegisterMemberRequest request = new RegisterMemberRequest();
		request.setName("Test");
		request.setEmail("new@example.com");
		request.setPhoneNumber("1111111111");
		request.setPassword("securePassword");

		when(memberRegistrationService.isEmailExists("new@example.com")).thenReturn(false);

		mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());
	}

	@Test
	void testRegister_duplicateEmail() throws Exception {
		RegisterMemberRequest request = new RegisterMemberRequest();
		request.setName("Test");
		request.setEmail("existing@example.com");
		request.setPhoneNumber("9999999999");
		request.setPassword("securePassword");

		when(memberRegistrationService.isEmailExists("existing@example.com")).thenReturn(true);

		mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.email").value("Email Taken"));
	}

	@Test
	void testRegister_genericException() throws Exception {
		RegisterMemberRequest request = new RegisterMemberRequest();
		request.setName("Test");
		request.setEmail("error@example.com");
		request.setPhoneNumber("9999999999");
		request.setPassword("securePassword");

		when(memberRegistrationService.isEmailExists("error@example.com")).thenReturn(false);
		doThrow(new RuntimeException("Something went wrong")).when(memberRegistrationService).register(any());

		mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Something went wrong"));
	}
}
