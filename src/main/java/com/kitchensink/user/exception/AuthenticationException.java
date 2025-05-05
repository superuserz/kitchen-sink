package com.kitchensink.user.exception;

/**
 * The Class AuthenticationException.
 * 
 * Exception class to handle Authentication related Exception
 * 
 */
public class AuthenticationException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 721810209229508690L;

	/**
	 * Instantiates a new authentication exception.
	 *
	 * @param message the message
	 */
	public AuthenticationException(String message) {
		super(message);
	}
}