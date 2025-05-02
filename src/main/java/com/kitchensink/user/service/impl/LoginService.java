package com.kitchensink.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.kitchensink.user.config.LoginJwtTokenProvider;

@Service
public class LoginService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private LoginJwtTokenProvider jwtTokenProvider;

	public String login(String email, String password) {

		// Authenticate the user from Database
		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(email, password));

			// Generate JWT token
			return jwtTokenProvider.generateToken(authentication);

		} catch (BadCredentialsException e) {
			throw new RuntimeException("Invalid email or password");
		}
	}
}
