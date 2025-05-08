package com.kitchensink.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kitchensink.user.config.JwtAuthFilter;
import com.kitchensink.user.utils.TestUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

	@InjectMocks
	private JwtAuthFilter jwtAuthFilter;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	private String jwtSecret = "verySecretKeyThatIsAtLeast256BitsLongForHS256Algo";

	private String jwt;

	@BeforeEach
	void setUp() {
		jwtAuthFilter = new JwtAuthFilter();
		TestUtils.setFields(jwtAuthFilter, "jwtSecret", jwtSecret);

		jwt = Jwts.builder().setSubject("testuser").claim("roles", List.of("ROLE_USER")).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 1000000))
				.signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256).compact();
		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldSkipPublicPaths() throws Exception {
		when(request.getRequestURI()).thenReturn("/api/member/register");

		jwtAuthFilter.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void shouldRejectWhenTokenMissing() throws Exception {
		when(request.getRequestURI()).thenReturn("/api/secure");
		when(request.getHeader("Authorization")).thenReturn(null);
		when(request.getCookies()).thenReturn(null);

		jwtAuthFilter.doFilter(request, response, filterChain);

		verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token");
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void shouldAcceptValidAuthHeaderToken() throws Exception {
		when(request.getRequestURI()).thenReturn("/api/secure");
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);

		jwtAuthFilter.doFilter(request, response, filterChain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldAcceptValidCookieToken() throws Exception {
		when(request.getRequestURI()).thenReturn("/api/secure");
		when(request.getHeader("Authorization")).thenReturn(null);
		when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("auth_token", jwt) });

		jwtAuthFilter.doFilter(request, response, filterChain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldRejectInvalidToken() throws Exception {
		when(request.getRequestURI()).thenReturn("/api/secure");
		when(request.getHeader("Authorization")).thenReturn("Bearer invalid.jwt.token");

		jwtAuthFilter.doFilter(request, response, filterChain);

		verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}
}
