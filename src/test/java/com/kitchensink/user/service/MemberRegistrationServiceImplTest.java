package com.kitchensink.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.enums.UserRole;
import com.kitchensink.user.repository.MemberRepository;
import com.kitchensink.user.requests.RegisterMemberRequest;
import com.kitchensink.user.service.impl.MemberRegistrationServiceImpl;

@ExtendWith(MockitoExtension.class)
class MemberRegistrationServiceImplTest {

	@InjectMocks
	private MemberRegistrationServiceImpl service;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testRegister_success() {
		RegisterMemberRequest request = new RegisterMemberRequest();
		request.setName("John Doe");
		request.setEmail("john@example.com");
		request.setPhoneNumber("1234567890");
		request.setPassword("rawPassword");

		String encodedPassword = "encodedPassword";
		when(passwordEncoder.encode("rawPassword")).thenReturn(encodedPassword);

		service.register(request);

		ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
		verify(memberRepository, times(1)).save(memberCaptor.capture());

		Member savedMember = memberCaptor.getValue();
		assertEquals("John Doe", savedMember.getName());
		assertEquals("john@example.com", savedMember.getEmail());
		assertEquals("1234567890", savedMember.getPhoneNumber());
		assertEquals(encodedPassword, savedMember.getPassword());
		assertEquals(List.of(UserRole.USER), savedMember.getRoles());
	}

	@Test
	void testIsEmailExists_true() {
		String email = "exists@example.com";
		when(memberRepository.existsByEmail(email)).thenReturn(true);

		assertTrue(service.isEmailExists(email));
		verify(memberRepository).existsByEmail(email);
	}

	@Test
	void testIsEmailExists_false() {
		String email = "new@example.com";
		when(memberRepository.existsByEmail(email)).thenReturn(false);

		assertFalse(service.isEmailExists(email));
		verify(memberRepository).existsByEmail(email);
	}
}
