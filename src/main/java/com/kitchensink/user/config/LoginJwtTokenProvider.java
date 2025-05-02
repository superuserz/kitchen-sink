package com.kitchensink.user.config;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class LoginJwtTokenProvider {

	@Value("${jwt.secret:c2xrYWRqbGt3cWpkb2xqZGlvd3F4bndxb25jcXdkandxO29qZGxxd2pkcW93aWQ=}")
	private String jwtSecret;

	@Value("${jwt.expiration:86400000}")
	private long jwtExpiration;

	public String generateToken(Authentication authentication) {
		String email = authentication.getName();
		List<String> roles = authentication.getAuthorities().stream()
				.map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toList());
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpiration);

		return Jwts.builder().setSubject(email).claim("roles", roles).setIssuedAt(new Date()).setExpiration(expiryDate)
				.signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256).compact();
	}

	public String getUsernameFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build()
				.parseClaimsJws(token).getBody().getSubject();
	}

	@SuppressWarnings("unchecked")
	public List<String> getRolesFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build()
				.parseClaimsJws(token).getBody().get("roles", List.class);
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}