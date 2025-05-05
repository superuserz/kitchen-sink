package com.kitchensink.user.enums;

/**
 * The Enum UserRole.
 */
public enum UserRole {

	/** The user. */
	USER("USER"),
	/** The admin. */
	ADMIN("ADMIN");

	/** The role. */
	private final String role;

	/**
	 * Instantiates a new user role.
	 *
	 * @param role the role
	 */
	UserRole(String role) {
		this.role = role;
	}

	/**
	 * Gets the role.
	 *
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * From string.
	 *
	 * @param roleStr the role str
	 * @return the user role
	 */
	public static UserRole fromString(String roleStr) {
		for (UserRole role : UserRole.values()) {
			if (role.role.equalsIgnoreCase(roleStr)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Invalid role: " + roleStr);
	}
}
