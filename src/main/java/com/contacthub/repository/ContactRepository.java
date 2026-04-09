package com.contacthub.repository;

import com.contacthub.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<Contact, Long> gives you these methods FOR FREE — Spring generates the SQL automatically:
//   save(contact)          → INSERT or UPDATE
//   findAll()              → SELECT * FROM contacts
//   findById(id)           → SELECT * FROM contacts WHERE id = ?
//   deleteById(id)         → DELETE FROM contacts WHERE id = ?
//   existsById(id)         → SELECT COUNT(*) FROM contacts WHERE id = ?
//
// You do NOT write any SQL. Spring Data JPA reads the method names and generates the queries.
public interface ContactRepository extends JpaRepository<Contact, Long> {

    // Spring Data JPA generates: SELECT * FROM contacts WHERE email = ?
    // Returns Optional<Contact> because the contact might not exist — Optional forces you to handle that case
    Optional<Contact> findByEmail(String email);
}
