package com.kitchensink.user.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.repository.MemberRepository;
import com.kitchensink.user.service.MemberService;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberRepository memberRepository;

	public MemberServiceImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public List<Member> getAllMembers() {
		return memberRepository.findAll();
	}

	@Override
	public Member lookupMemberById(String id) {
		Optional<Member> member = memberRepository.findById(id);
		if (member.isPresent()) {
			return member.get();
		} else {
			return null;
		}
	}
}
