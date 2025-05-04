package com.kitchensink.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.kitchensink.user.config.LoginJwtTokenProvider;
import com.kitchensink.user.service.impl.LoginService;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class LoginServiceTest {

	@InjectMocks
	private LoginService loginService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private LoginJwtTokenProvider jwtTokenProvider;

	@Mock
	private Authentication authentication;

	@Test
	void testLogin_success() {
		// Arrange
		String email = "john@example.com";
		String password = "securePass";
		String expectedToken = "mocked-jwt-token";

		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(jwtTokenProvider.generateToken(authentication)).thenReturn(expectedToken);

		// Act
		String actualToken = loginService.login(email, password);

		// Assert
		assertEquals(expectedToken, actualToken);
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(jwtTokenProvider).generateToken(authentication);
	}

	@Test
	void testLogin_invalidCredentials() {
		// Arrange
		String email = "john@example.com";
		String password = "wrongPass";

		when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

		// Act & Assert
		RuntimeException exception = assertThrows(RuntimeException.class, () -> loginService.login(email, password));

		assertEquals("Invalid email or password", exception.getMessage());
		verify(authenticationManager).authenticate(any());
		verifyNoInteractions(jwtTokenProvider);
	}
}
