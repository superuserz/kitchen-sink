package com.kitchensink.user.service;

import com.kitchensink.user.exception.AuthenticationException;

/**
 * The Interface LoginService.
 * 
 * @author manmeetdevgun
 */
public interface LoginService {

	/**
	 * Login.
	 *
	 * @param email    the email
	 * @param password the password
	 * @return the string
	 * @throws AuthenticationException the authentication exception
	 */
	String login(String email, String password) throws AuthenticationException;

}
