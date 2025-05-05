package com.kitchensink.user.requests;

/**
 * The Class LoginResponse.
 * 
 * This response is used to return the JWT Token for API Access and Login
 * Requests.
 * 
 * @author manmeetdevgun
 * 
 */
public class LoginResponse {

	/** The token. */
	private String token;

	/**
	 * Instantiates a new login response.
	 *
	 * @param token the token
	 */
	public LoginResponse(String token) {
		super();
		this.token = token;
	}

	/**
	 * Instantiates a new login response.
	 */
	public LoginResponse() {
		super();
	}

	/**
	 * Gets the token.
	 *
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets the token.
	 *
	 * @param token the new token
	 */
	public void setToken(String token) {
		this.token = token;
	}

}
