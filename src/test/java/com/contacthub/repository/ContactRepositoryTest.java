package com.contacthub.repository;

import com.contacthub.model.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest loads only the JPA slice (repositories, entities) — no controllers, no services.
// H2 in-memory database automatically replaces MariaDB for the test run.
@DataJpaTest
class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void save_persistsContact_andCanBeFoundByEmail() {
        Contact contact = new Contact();
        contact.setFirstName("Jane");
        contact.setLastName("Doe");
        contact.setEmail("jane@example.com");

        contactRepository.save(contact);

        Optional<Contact> found = contactRepository.findByEmail("jane@example.com");
        assertTrue(found.isPresent());
        assertEquals("Jane", found.get().getFirstName());
        assertEquals("Doe", found.get().getLastName());
    }

    @Test
    void save_withDuplicateEmail_throwsException() {
        Contact first = new Contact();
        first.setFirstName("Jane");
        first.setLastName("Doe");
        first.setEmail("jane@example.com");
        contactRepository.save(first);

        Contact duplicate = new Contact();
        duplicate.setFirstName("John");
        duplicate.setLastName("Smith");
        duplicate.setEmail("jane@example.com");

        // saveAndFlush forces the INSERT immediately — without flush, H2 may not hit the unique constraint
        assertThrows(Exception.class, () -> contactRepository.saveAndFlush(duplicate));
    }
}
