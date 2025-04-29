package com.kitchensink.user.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.kitchensink.user.entity.Member;

@Repository
public interface MemberRepository extends MongoRepository<Member, String> {

	Optional<Member> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<Member> findById(long id);
}
