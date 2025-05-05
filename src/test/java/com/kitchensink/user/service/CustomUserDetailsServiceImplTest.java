package com.kitchensink.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.enums.UserRole;
import com.kitchensink.user.repository.MemberRepository;
import com.kitchensink.user.service.impl.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceImplTest {

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private CustomUserDetailsService userDetailsService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void loadUserByUsername_whenUserExists_returnsUserDetails() {
		// Arrange
		Member member = new Member();
		member.setEmail("test@example.com");
		member.setPassword("encodedPassword");
		member.setRoles(List.of(UserRole.USER, UserRole.ADMIN)); // assuming Role is an enum

		when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));

		// Act
		UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

		// Assert
		assertEquals("test@example.com", userDetails.getUsername());
		assertEquals("encodedPassword", userDetails.getPassword());
		assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
		assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
	}

	@Test
	void loadUserByUsername_whenUserDoesNotExist_throwsException() {
		// Arrange
		when(memberRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(UsernameNotFoundException.class,
				() -> userDetailsService.loadUserByUsername("missing@example.com"));
	}
}
