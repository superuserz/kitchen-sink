package com.kitchensink.user.service;

import com.kitchensink.user.requests.RegisterMemberRequest;

/**
 * The Interface MemberRegistrationService.
 */
public interface MemberRegistrationService {

	/**
	 * Register.
	 *
	 * @param request the request
	 */
	void register(RegisterMemberRequest request);

	/**
	 * Checks if is email exists.
	 *
	 * @param email the email
	 * @return true, if is email exists
	 */
	boolean isEmailExists(String email);

}
