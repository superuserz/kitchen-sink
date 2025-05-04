package com.kitchensink.user.service;

import java.util.List;

import com.kitchensink.user.entity.Member;

public interface MemberService {

	Member lookupMemberById(String id);

	List<Member> listAllMembers();

	Member getProfile();

}
