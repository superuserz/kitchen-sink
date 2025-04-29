package com.kitchensink.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.repository.MemberRepository;
import com.kitchensink.user.requests.RegisterMemberRequest;
import com.kitchensink.user.service.MemberRegistrationService;

@Service
public class MemberRegistrationServiceImpl implements MemberRegistrationService {

	@Autowired
	MemberRepository memberRepository;

	@Override
	public void register(RegisterMemberRequest request) {

		Member member = new Member();
		member.setName(request.getName());
		member.setEmail(request.getEmail());
		member.setPhoneNumber(request.getPhoneNumber());

		memberRepository.save(member);
	}

}
