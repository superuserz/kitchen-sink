package com.kitchensink.user.service;

import com.kitchensink.user.requests.RegisterMemberRequest;

public interface MemberRegistrationService {

	void register(RegisterMemberRequest request);

}
