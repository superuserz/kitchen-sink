package com.kitchensink.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.kitchensink.user.config.LoginJwtTokenProvider;
import com.kitchensink.user.exception.AuthenticationException;
import com.kitchensink.user.service.LoginService;

/**
 * The Class LoginServiceImpl.
 *
 * @author manmeetdevgun
 * 
 *         The Class LoginService.
 * 
 *         Authenticates a user against spring-security user name and password
 *         authentication. On successful authentication, return a short-lives
 *         JWT Token.
 */
@Service
public class LoginServiceImpl implements LoginService {

	/** The authentication manager. */
	@Autowired
	private AuthenticationManager authenticationManager;

	/** The jwt token provider. */
	@Autowired
	private LoginJwtTokenProvider jwtTokenProvider;

	/**
	 * Login.
	 *
	 * @param email    the email
	 * @param password the password
	 * @return the string
	 * @throws AuthenticationException the authentication exception
	 */
	@Override
	public String login(String email, String password) throws AuthenticationException {

		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(email, password));

			return jwtTokenProvider.generateToken(authentication);
		} catch (BadCredentialsException e) {
			throw new AuthenticationException("Invalid email or password");
		}
	}
}
