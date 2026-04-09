package com.contacthub.service;

import com.contacthub.dto.ContactRequest;
import com.contacthub.dto.ContactResponse;

// This is an INTERFACE — it defines the CONTRACT (what operations exist) but not HOW they work.
//
// Why use an interface here?
//   1. Dependency Inversion (SOLID "D"): ContactController depends on this interface, not the concrete implementation.
//      This means you could swap ContactServiceImpl for a different implementation without touching the controller.
//   2. Testability: In tests, you can provide a mock (fake) ContactService without needing a real database.
//   3. Clarity: The interface documents what the service CAN do, separately from how it does it.
public interface ContactService {

    // Creates a new contact and returns the saved contact as a ContactResponse
    ContactResponse create(ContactRequest request);

    ContactResponse fetch(String emailId);
}
