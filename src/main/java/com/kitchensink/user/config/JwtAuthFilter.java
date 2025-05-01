package com.kitchensink.user.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

	@Value("${jwt.secret:c2xrYWRqbGt3cWpkb2xqZGlvd3F4bndxb25jcXdkandxO29qZGxxd2pkcW93aWQ=}")
	private String jwtSecret;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getRequestURI().equals("/auth/token")) {

			filterChain.doFilter(request, response);

		}
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String jwt = authHeader.substring(7);
			try {
				Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build()
						.parseClaimsJws(jwt).getBody();

				String username = claims.getSubject();
				if (username != null) {
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
							List.of());
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
