package com.kitchensink.user.exception;

/**
 * The Class AuthenticationException.
 * 
 * Exception class to handle Authentication related Exception
 * 
 */
public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new authentication exception.
	 *
	 * @param message the message
	 */
	public UserNotFoundException(String message) {
		super(message);
	}
}
