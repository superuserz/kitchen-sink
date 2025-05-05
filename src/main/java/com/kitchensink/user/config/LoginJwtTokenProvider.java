package com.kitchensink.user.config;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

/**
 * The Class LoginJwtTokenProvider.
 */
@Component
public class LoginJwtTokenProvider {

	/** The jwt secret. */
	@Value("${jwt.secret}")
	private String jwtSecret;

	/** The jwt expiration. */
	@Value("${jwt.expiration:3600000}")
	private long jwtExpiration;

	/** The Constant Logger. */
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LoginJwtTokenProvider.class);

	/**
	 * Generate token.
	 *
	 * @param authentication the authentication
	 * @return the string
	 */
	public String generateToken(Authentication authentication) {
		String email = authentication.getName();
		List<String> roles = authentication.getAuthorities().stream()
				.map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toList());
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpiration);

		return Jwts.builder().setSubject(email).claim("roles", roles).setIssuedAt(new Date()).setExpiration(expiryDate)
				.signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256).compact();
	}

	/**
	 * Gets the username from token.
	 *
	 * @param token the token
	 * @return the username from token
	 */
	public String getUsernameFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build()
				.parseClaimsJws(token).getBody().getSubject();
	}

	/**
	 * Gets the roles from token.
	 *
	 * @param token the token
	 * @return the roles from token
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRolesFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build()
				.parseClaimsJws(token).getBody().get("roles", List.class);
	}

	/**
	 * Validate token.
	 *
	 * @param token the token
	 * @return true, if successful
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build().parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException e) {
			LOGGER.error("JWT expired: ", e.getMessage());
		} catch (UnsupportedJwtException e) {
			LOGGER.error("Unsupported JWT: ", e.getMessage());
		} catch (MalformedJwtException e) {
			LOGGER.error("Malformed JWT: ", e.getMessage());
		} catch (IllegalArgumentException e) {
			LOGGER.error("JWT claims string is empty: ", e.getMessage());
		}
		return false;
	}
}