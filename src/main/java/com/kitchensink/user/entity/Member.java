package com.kitchensink.user.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kitchensink.user.enums.UserRole;

/**
 * The Class Member.
 */
@Document(collection = "members")
public class Member {

	/** The id. */
	@Id
	private String id;

	/** The name. */
	private String name;

	/** The email. */
	private String email;

	/** The phone number. */
	private String phoneNumber;

	/** The password. */
	@JsonIgnore
	private String password;

	/** The registered on. */
	@CreatedDate
	private LocalDateTime registeredOn;

	/** The roles. */
	private List<UserRole> roles;

	/**
	 * Instantiates a new member.
	 */
	public Member() {
		super();
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	// Getters and setters
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
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
	public List<UserRole> getRoles() {
		return roles;
	}

	/**
	 * Sets the roles.
	 *
	 * @param roles the new roles
	 */
	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}

	/**
	 * Gets the registered on.
	 *
	 * @return the registered on
	 */
	public LocalDateTime getRegisteredOn() {
		return registeredOn;
	}

	/**
	 * Sets the registered on.
	 *
	 * @param registeredOn the new registered on
	 */
	public void setRegisteredOn(LocalDateTime registeredOn) {
		this.registeredOn = registeredOn;
	}
}
