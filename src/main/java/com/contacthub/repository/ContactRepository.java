package com.contacthub.repository;

import com.contacthub.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
	//
	Optional<Contact> findByEmail(String email);

	boolean existsByEmail(String email);

	//
}
