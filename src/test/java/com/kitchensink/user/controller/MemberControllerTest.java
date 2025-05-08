package com.kitchensink.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitchensink.user.entity.Member;
import com.kitchensink.user.exception.AuthenticationException;
import com.kitchensink.user.exception.handler.GlobalExceptionHandler;
import com.kitchensink.user.requests.RegisterMemberRequest;
import com.kitchensink.user.service.MemberRegistrationService;
import com.kitchensink.user.service.MemberService;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

	@InjectMocks
	private MemberController memberController;

	@Mock
	private MemberService memberService;

	@Mock
	private MemberRegistrationService memberRegistrationService;

	private MockMvc mockMvc;

	private Member testMember;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {

		testMember = new Member();
		testMember.setId("1");
		testMember.setName("Test User");
		testMember.setEmail("test@example.com");
		testMember.setPhoneNumber("1234567890");
		mockMvc = MockMvcBuilders.standaloneSetup(memberController).setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	void testExportMembersReport_returnsExcelStream() throws Exception {
		// Arrange: Mock the workbook returned by the service
		Workbook workbook = new XSSFWorkbook(); // empty but valid
		when(memberService.exportMembersReport()).thenReturn(workbook);

		// Act + Assert
		mockMvc.perform(get("/api/members/export")).andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=members.xlsx"))
				.andExpect(header().string("Content-Type", "application/octet-stream"));
	}

	@Test
	void testRegister_success() throws Exception {
		RegisterMemberRequest request = new RegisterMemberRequest();
		request.setName("Test User");
		request.setEmail("test@example.com");
		request.setPhoneNumber("8989898989");
		request.setPassword("Secure@123");

		when(memberRegistrationService.isEmailExists("test@example.com")).thenReturn(false);

		mockMvc.perform(post("/api/member/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());

		verify(memberRegistrationService).register(any(RegisterMemberRequest.class));
	}

	@Test
	void testRegister_duplicateEmail_conflict() throws Exception {
		RegisterMemberRequest request = new RegisterMemberRequest();
		request.setName("Test User");
		request.setEmail("taken@example.com");
		request.setPhoneNumber("8989898989");
		request.setPassword("Secure@123");

		when(memberRegistrationService.isEmailExists("taken@example.com")).thenReturn(true);

		mockMvc.perform(post("/api/member/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.email").value("Email Taken"));
	}

	@Test
	void testRegister_invalidInput_badRequest() throws Exception {
		RegisterMemberRequest request = new RegisterMemberRequest(); // empty/invalid

		mockMvc.perform(post("/api/member/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict());
	}

	@Test
	void testListAllMembers_returnsList() throws Exception {
		when(memberService.listAllMembers()).thenReturn(List.of(testMember));

		mockMvc.perform(get("/api/members")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email").value("test@example.com"));
	}

	@Test
	void testLookupMemberById_found() throws Exception {
		when(memberService.lookupMemberById("1")).thenReturn(testMember);

		mockMvc.perform(get("/api/members/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Test User"));
	}

	@Test
	void testDeleteMemberById_success() throws Exception {
		when(memberService.deleteMemberById("1")).thenReturn(true);

		mockMvc.perform(delete("/api/members/1")).andExpect(status().isOk());
	}

	@Test
	void testDeleteMemberById_notFound() throws Exception {
		when(memberService.deleteMemberById("999")).thenReturn(false);

		mockMvc.perform(delete("/api/members/999")).andExpect(status().isNotFound());
	}

	@Test
	void testGetProfile_found() throws Exception {
		when(memberService.getProfile()).thenReturn(testMember);

		mockMvc.perform(get("/api/member/me")).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("test@example.com"));
	}

	@Test
	void testGetProfile_notFound() throws Exception {
		// Arrange: throw custom AuthenticationException from service
		when(memberService.getProfile()).thenThrow(new AuthenticationException("Member not found"));

		// Act & Assert
		mockMvc.perform(get("/api/member/me")).andExpect(status().isUnauthorized());

	}
}