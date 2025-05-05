package com.kitchensink.user.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (request.getRequestURI().startsWith("/actuator/health")
				|| request.getRequestURI().startsWith("/api/register")
				|| request.getRequestURI().startsWith("/api/login") || request.getRequestURI().startsWith("/api/token")
				|| request.getRequestURI().startsWith("/swagger-ui")
				|| request.getRequestURI().startsWith("/v3/api-docs")) {
			filterChain.doFilter(request, response);
			return;
		}
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String jwt = authHeader.substring(7);
			try {
				Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build()
						.parseClaimsJws(jwt).getBody();

				List<String> roles = claims.get("roles", List.class);
				List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new)

						.collect(Collectors.toList());

				String username = claims.getSubject();
				if (username != null) {
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
							authorities);
					SecurityContextHolder.getContext().setAuthentication(auth);
				}

			} catch (JwtException e) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
				return;
			}
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
			return;
		}

		filterChain.doFilter(request, response);
	}
}
