package com.kitchensink.user.service;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.kitchensink.user.entity.Member;

/**
 * The Interface MemberService.
 */
public interface MemberService {

	/**
	 * Lookup member by id.
	 *
	 * @param id the id
	 * @return the member
	 */
	Member lookupMemberById(String id);

	/**
	 * List all members.
	 *
	 * @return the list
	 */
	List<Member> listAllMembers();

	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	Member getProfile();

	/**
	 * Export members report.
	 *
	 * @return the workbook
	 */
	Workbook exportMembersReport();

}
