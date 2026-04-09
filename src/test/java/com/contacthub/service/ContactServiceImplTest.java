package com.contacthub.service;

import com.contacthub.domain.Contact;
import com.contacthub.dto.ContactRequest;
import com.contacthub.dto.ContactResponse;
import com.contacthub.mapper.ContactMapper;
import com.contacthub.repository.ContactRepository;
import com.contacthub.service.impl.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    private ContactServiceImpl contactService;

    @BeforeEach
    void setUp() {
        contactService = new ContactServiceImpl(contactRepository, new ContactMapper());
    }

    // --- create ---

    @Test
    void create_savesContact_returnsResponse() {
        ContactRequest request = new ContactRequest(
                "Jane", "Doe", "jane@example.com",
                "0412345678", "123 Main St", "Sydney", "Australia"
        );

        when(contactRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
        when(contactRepository.save(any(Contact.class))).thenAnswer(inv -> inv.getArgument(0));

        ContactResponse result = contactService.create(request);

        assertNotNull(result);
        assertEquals("jane@example.com", result.email());
        assertEquals("Jane", result.firstName());
    }

    @Test
    void create_throwsException_whenDuplicateEmail() {
        ContactRequest request = new ContactRequest(
                "Jane", "Doe", "jane@example.com",
                "0412345678", "123 Main St", "Sydney", "Australia"
        );

        when(contactRepository.findByEmail("jane@example.com"))
                .thenReturn(Optional.of(new Contact()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> contactService.create(request));

        assertEquals("A contact with this email already exists: jane@example.com", ex.getMessage());
    }

    // --- fetch ---

    @Test
    void fetch_returnsResponse_whenEmailExists() {
        String email = "jane@example.com";

        Contact contact = new Contact();
        contact.setFirstName("Jane");
        contact.setLastName("Doe");
        contact.setEmail(email);
        contact.setPhone("0412345678");

        when(contactRepository.findByEmail(email)).thenReturn(Optional.of(contact));

        ContactResponse result = contactService.fetch(email);

        assertNotNull(result);
        assertEquals("Jane", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals(email, result.email());
        assertEquals("0412345678", result.phone());
    }

    @Test
    void fetch_throwsException_whenEmailNotFound() {
        String email = "unknown@example.com";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> contactService.fetch(email));

        assertEquals("A contact with this email doesn't exist: " + email, ex.getMessage());
    }
}
