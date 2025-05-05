package com.kitchensink.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.enums.UserRole;
import com.kitchensink.user.repository.MemberRepository;
import com.kitchensink.user.requests.MemberCriteriaRequest;
import com.kitchensink.user.service.impl.MemberServiceImpl;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private MongoTemplate mongoTemplate;

	@InjectMocks
	private MemberServiceImpl memberService;

	private Member testMember;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		testMember = new Member();
		testMember.setId("123");
		testMember.setEmail("test@example.com");
		testMember.setName("Test User");
		testMember.setPhoneNumber("9999999999");
		testMember.setRegisteredOn(LocalDateTime.now());
		testMember.setRoles(List.of(UserRole.USER));
	}

	@Test
	void testLookupMemberById_found() {
		when(memberRepository.findById("123")).thenReturn(Optional.of(testMember));
		Member result = memberService.lookupMemberById("123");
		assertNotNull(result);
		assertEquals("test@example.com", result.getEmail());
	}

	@Test
	void testLookupMemberById_notFound() {
		when(memberRepository.findById("123")).thenReturn(Optional.empty());
		Member result = memberService.lookupMemberById("123");
		assertNull(result);
	}

	@Test
	void testGetProfile() {
		// Mock Security Context
		Authentication auth = mock(Authentication.class);
		when(auth.getPrincipal()).thenReturn("test@example.com");

		SecurityContext context = mock(SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		SecurityContextHolder.setContext(context);

		when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testMember));

		Member profile = memberService.getProfile();
		assertEquals("test@example.com", profile.getEmail());
	}

	@Test
	void testDeleteMemberById_found() {
		when(memberRepository.findById("123")).thenReturn(Optional.of(testMember));
		boolean result = memberService.deleteMemberById("123");
		assertTrue(result);
		verify(memberRepository).deleteById("123");
	}

	@Test
	void testDeleteMemberById_notFound() {
		when(memberRepository.findById("999")).thenReturn(Optional.empty());
		boolean result = memberService.deleteMemberById("999");
		assertFalse(result);
		verify(memberRepository, never()).deleteById(anyString());
	}

	@Test
	void testSearchMembers_withNameCriteria() {
		MemberCriteriaRequest criteria = new MemberCriteriaRequest();
		criteria.setPage(0);
		criteria.setSize(10);
		criteria.setSortBy("name");
		criteria.setDirection("asc");
		criteria.setName("Test");

		List<Member> memberList = List.of(testMember);
		Query expectedQuery = new Query().with(PageRequest.of(0, 10, Sort.by("name").ascending()));
		expectedQuery.addCriteria(Criteria.where("name").regex("Test", "i"));

		when(mongoTemplate.find(any(Query.class), eq(Member.class))).thenReturn(memberList);
		when(mongoTemplate.count(any(Query.class), eq(Member.class))).thenReturn(1L);

		Page<Member> result = memberService.searchMembers(criteria);

		assertEquals(1, result.getTotalElements());
		assertEquals("Test User", result.getContent().get(0).getName());
	}

	@Test
	void testSearchMembers_withEmailCriteria() {
		MemberCriteriaRequest criteria = new MemberCriteriaRequest();
		criteria.setPage(0);
		criteria.setSize(10);
		criteria.setSortBy("name");
		criteria.setDirection("asc");
		criteria.setEmail("test");

		when(mongoTemplate.find(any(Query.class), eq(Member.class))).thenReturn(List.of(testMember));
		when(mongoTemplate.count(any(Query.class), eq(Member.class))).thenReturn(1L);

		Page<Member> result = memberService.searchMembers(criteria);
		assertEquals(1, result.getTotalElements());
	}

	@Test
	void testSearchMembers_withRoleCriteria() {
		MemberCriteriaRequest criteria = new MemberCriteriaRequest();
		criteria.setPage(0);
		criteria.setSize(10);
		criteria.setSortBy("name");
		criteria.setDirection("asc");
		criteria.setRole("USER");

		when(mongoTemplate.find(any(Query.class), eq(Member.class))).thenReturn(List.of(testMember));
		when(mongoTemplate.count(any(Query.class), eq(Member.class))).thenReturn(1L);

		Page<Member> result = memberService.searchMembers(criteria);
		assertEquals(1, result.getTotalElements());
	}

	@Test
	void testSearchMembers_withAllCriteria() {
		MemberCriteriaRequest criteria = new MemberCriteriaRequest();
		criteria.setPage(0);
		criteria.setSize(10);
		criteria.setSortBy("name");
		criteria.setDirection("asc");
		criteria.setName("Test");
		criteria.setEmail("test@example.com");
		criteria.setRole("USER");

		when(mongoTemplate.find(any(Query.class), eq(Member.class))).thenReturn(List.of(testMember));
		when(mongoTemplate.count(any(Query.class), eq(Member.class))).thenReturn(1L);

		Page<Member> result = memberService.searchMembers(criteria);
		assertEquals(1, result.getTotalElements());
	}

	@Test
	void testSearchMembers_withNoFilters() {
		MemberCriteriaRequest criteria = new MemberCriteriaRequest();
		criteria.setPage(0);
		criteria.setSize(10);
		criteria.setSortBy("name");
		criteria.setDirection("asc");

		when(mongoTemplate.find(any(Query.class), eq(Member.class))).thenReturn(List.of(testMember));
		when(mongoTemplate.count(any(Query.class), eq(Member.class))).thenReturn(1L);

		Page<Member> result = memberService.searchMembers(criteria);
		assertEquals(1, result.getTotalElements());
	}

	@Test
	void testExportMembersReport_generatesWorkbookCorrectly() {
		// Arrange
		Member member = new Member();
		member.setName("Alice");
		member.setEmail("alice@example.com");
		member.setPhoneNumber("1234567890");
		member.setRegisteredOn(LocalDateTime.of(2023, 5, 1, 10, 0));
		member.setRoles(List.of(UserRole.ADMIN));

		when(memberRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(List.of(member));

		// Act
		Workbook workbook = memberService.exportMembersReport();

		// Assert
		assertNotNull(workbook);
		Sheet sheet = workbook.getSheet("Members");
		assertNotNull(sheet);

		// Header row
		Row headerRow = sheet.getRow(0);
		assertEquals("Name", headerRow.getCell(0).getStringCellValue());
		assertEquals("Email", headerRow.getCell(1).getStringCellValue());

		// Data row
		Row dataRow = sheet.getRow(1);
		assertEquals("Alice", dataRow.getCell(0).getStringCellValue());
		assertEquals("alice@example.com", dataRow.getCell(1).getStringCellValue());
		assertEquals("1234567890", dataRow.getCell(2).getStringCellValue());
		assertTrue(dataRow.getCell(4).getStringCellValue().contains("ADMIN"));
	}
}