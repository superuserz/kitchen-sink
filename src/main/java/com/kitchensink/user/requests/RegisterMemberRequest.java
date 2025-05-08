package com.kitchensink.user.requests;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The Class RegisterMemberRequest.
 * 
 * @author manmeetdevgun
 */
public class RegisterMemberRequest {

	/** The name. */
	@NotNull
	@Size(min = 1, max = 25)
	@Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
	private String name;

	/** The email. */
	@NotNull
	@NotEmpty
	@Email
	private String email;

	/** The phone number. */
	@NotNull
	@Size(min = 10, max = 12)
	@Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid mobile number")
	private String phoneNumber;

	/** The password. */
	@NotNull
	@Size(min = 7)
	private String password;

	/** The roles. */
	private List<String> roles;

	/**
	 * Instantiates a new register member request.
	 */
	public RegisterMemberRequest() {
		super();

	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Gets the phone number.
	 *
	 * @return the phone number
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Sets the phone number.
	 *
	 * @param phoneNumber the new phone number
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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

	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	public List<String> getRoles() {
		return roles;
	}

	/**
	 * Sets the roles.
	 *
	 * @param roles the new roles
	 */
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
