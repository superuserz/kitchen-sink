package com.kitchensink.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.repository.MemberRepository;
import com.kitchensink.user.service.MemberService;

@SpringBootTest
class MemberServiceImplTest {

	@Autowired
	private MemberService memberService;

	@MockBean
	private MemberRepository memberRepository;

	@Test
	void testLookupMemberById_found() {
		Member member = new Member();
		member.setId("123");
		member.setName("Test User");

		when(memberRepository.findById("123")).thenReturn(Optional.of(member));

		Member result = memberService.lookupMemberById("123");

		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("Test User");
	}

	@Test
	void testLookupMemberById_notFound() {
		when(memberRepository.findById("999")).thenReturn(Optional.empty());

		Member result = memberService.lookupMemberById("999");

		assertThat(result).isNull();
	}

	@Test
	void testListAllMembers_sortedByName() {
		Member member1 = new Member();
		member1.setName("Alice");

		Member member2 = new Member();
		member2.setName("Bob");

		List<Member> members = Arrays.asList(member1, member2);

		when(memberRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(members);

		List<Member> result = memberService.listAllMembers();

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getName()).isEqualTo("Alice");
	}
}