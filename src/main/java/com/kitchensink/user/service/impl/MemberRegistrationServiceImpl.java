package com.kitchensink.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.enums.UserRole;
import com.kitchensink.user.repository.MemberRepository;
import com.kitchensink.user.requests.RegisterMemberRequest;
import com.kitchensink.user.service.MemberRegistrationService;

/**
 * The Class MemberRegistrationServiceImpl.
 */
@Service
public class MemberRegistrationServiceImpl implements MemberRegistrationService {

	/** The member repository. */
	@Autowired
	MemberRepository memberRepository;

	/** The password encoder. */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Register.
	 *
	 * @param request the request
	 */
	@Override
	public void register(RegisterMemberRequest request) {

		Member member = new Member();
		member.setName(request.getName());
		member.setEmail(request.getEmail());
		member.setPhoneNumber(request.getPhoneNumber());

		// Hash the password using BCrypt before storing it
		String encodedPassword = passwordEncoder.encode(request.getPassword());
		member.setPassword(encodedPassword);
		// set default Role
		member.setRoles(List.of(UserRole.USER));

		// Save the member with the hashed password
		memberRepository.save(member);
	}

	/**
	 * Checks if is email exists.
	 *
	 * @param email the email
	 * @return true, if is email exists
	 */
	@Override
	public boolean isEmailExists(String email) {
		return memberRepository.existsByEmail(email);
	}

}
