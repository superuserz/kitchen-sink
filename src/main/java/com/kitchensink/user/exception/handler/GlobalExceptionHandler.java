package com.kitchensink.user.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kitchensink.user.exception.AuthenticationException;
import com.kitchensink.user.exception.UserNotFoundException;
import com.kitchensink.user.exception.ValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<?> handleValidationException(ValidationException ex) {
		return ResponseEntity.badRequest().body(ex.getErrors());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGenericException(Exception ex) {
		return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<String> handleAuthException(AuthenticationException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFoundException(AuthenticationException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
}
