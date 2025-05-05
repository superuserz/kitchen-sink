package com.kitchensink.user.exception;

import java.util.Map;

/**
 * The Class ValidationException.
 */
public class ValidationException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5974778341966451215L;

	/** The errors. */
	private final Map<String, String> errors;

	/**
	 * Instantiates a new validation exception.
	 *
	 * @param errors the errors
	 */
	public ValidationException(Map<String, String> errors) {
		super("Validation failed");
		this.errors = errors;
	}

	/**
	 * Gets the errors.
	 *
	 * @return the errors
	 */
	public Map<String, String> getErrors() {
		return errors;
	}
}
