package com.kitchensink.user.service;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.kitchensink.user.entity.Member;

public interface MemberService {

	Member lookupMemberById(String id);

	List<Member> listAllMembers();

	Member getProfile();

	Workbook exportMembersReport();

}
