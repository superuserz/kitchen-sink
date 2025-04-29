package com.kitchensink.user.exception;

import java.util.Map;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 5974778341966451215L;

	private final Map<String, String> errors;

	public ValidationException(Map<String, String> errors) {
		super("Validation failed");
		this.errors = errors;
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
