package com.kitchensink.user.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.kitchensink.user.entity.Member;

/**
 * The Interface MemberRepository.
 */
@Repository
public interface MemberRepository extends MongoRepository<Member, String> {

	/**
	 * Find by email.
	 *
	 * @param email the email
	 * @return the optional
	 */
	Optional<Member> findByEmail(String email);

	/**
	 * Exists by email.
	 *
	 * @param email the email
	 * @return true, if successful
	 */
	boolean existsByEmail(String email);

	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the optional
	 */
	Optional<Member> findById(long id);
}
