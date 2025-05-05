package com.kitchensink.user.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * The Class LoginRequest.
 * 
 * @author manmeetdevgun
 */
public class LoginRequest {

	/** The email. */
	@NotNull
	@Email(message = "email should not be empty")
	private String email;

	/** The password. */
	@NotNull(message = "password should not be empty")
	private String password;

	/**
	 * Instantiates a new login request.
	 */
	public LoginRequest() {
		super();
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
