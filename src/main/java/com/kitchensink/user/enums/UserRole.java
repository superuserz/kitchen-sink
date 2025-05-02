package com.kitchensink.user.enums;

public enum UserRole {
	USER("USER"), ADMIN("ADMIN");

	private final String role;

	UserRole(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public static UserRole fromString(String roleStr) {
		for (UserRole role : UserRole.values()) {
			if (role.role.equalsIgnoreCase(roleStr)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Invalid role: " + roleStr);
	}
}
