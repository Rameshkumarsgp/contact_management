package com.contacthub.service;

import com.contacthub.dto.ContactRequest;
import com.contacthub.dto.ContactResponse;

public interface ContactService {
    //
    ContactResponse create(ContactRequest request);

    ContactResponse fetch(String email);

    //
}
