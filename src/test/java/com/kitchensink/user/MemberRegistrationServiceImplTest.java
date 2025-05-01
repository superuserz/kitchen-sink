package com.kitchensink.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.kitchensink.user.entity.Member;
import com.kitchensink.user.repository.MemberRepository;
import com.kitchensink.user.requests.RegisterMemberRequest;
import com.kitchensink.user.service.impl.MemberRegistrationServiceImpl;

@SpringBootTest(classes = MemberRegistrationServiceImpl.class)
class MemberRegistrationServiceImplTest {

	@Autowired
	private MemberRegistrationServiceImpl memberRegistrationService;

	@MockBean
	private MemberRepository memberRepository;

	@Test
	void testRegister_shouldSaveMember() {
		// Arrange
		RegisterMemberRequest request = new RegisterMemberRequest();
		request.setName("Alice");
		request.setEmail("alice@example.com");
		request.setPhoneNumber("9876543210");

		// Act
		memberRegistrationService.register(request);

		// Assert
		ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
		verify(memberRepository, times(1)).save(captor.capture());

		Member saved = captor.getValue();
		assertThat(saved.getName()).isEqualTo("Alice");
		assertThat(saved.getEmail()).isEqualTo("alice@example.com");
		assertThat(saved.getPhoneNumber()).isEqualTo("9876543210");
	}

	@Test
	void testIsEmailExists_shouldReturnTrue() {
		when(memberRepository.existsByEmail("test@example.com")).thenReturn(true);

		boolean exists = memberRegistrationService.isEmailExists("test@example.com");

		assertThat(exists).isTrue();
	}

	@Test
	void testIsEmailExists_shouldReturnFalse() {
		when(memberRepository.existsByEmail("notfound@example.com")).thenReturn(false);

		boolean exists = memberRegistrationService.isEmailExists("notfound@example.com");

		assertThat(exists).isFalse();
	}
}
