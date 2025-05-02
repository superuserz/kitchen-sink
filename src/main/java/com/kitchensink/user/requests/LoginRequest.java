package com.kitchensink.user.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class LoginRequest {

	@NotNull
	@Email(message = "email should not be empty")
	private String email;

	@NotNull(message = "password should not be empty")
	private String password;

	public LoginRequest() {
		super();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
