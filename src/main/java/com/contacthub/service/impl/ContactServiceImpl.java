package com.contacthub.service.impl;

import com.contacthub.dto.ContactRequest;
import com.contacthub.dto.ContactResponse;
import com.contacthub.mapper.ContactMapper;
import com.contacthub.repository.ContactRepository;
import com.contacthub.service.ContactService;
import org.springframework.stereotype.Service;

@Service
public class ContactServiceImpl implements ContactService {
    //
    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    public ContactServiceImpl(ContactRepository contactRepository, ContactMapper contactMapper) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
    }

    @Override
    public ContactResponse create(ContactRequest request) {
        //
        if (contactRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "A contact with this email already exists: " + request.email());
        }

        var contact = contactMapper.toEntity(request);
        var saved = contactRepository.save(contact);
        return contactMapper.toResponse(saved);
    }

    @Override
    public ContactResponse fetch(String email) {
        //
        return contactRepository.findByEmail(email)
                .map(contactMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException(
                        "A contact with this email doesn't exist: " + email));
    }

    @Override
    public ContactResponse update(ContactRequest request) {
        //
        var existing = contactRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException(
                        "A contact with this email doesn't exist: " + request.email()));

        contactMapper.update(existing, request);
        var saved = contactRepository.save(existing);
        return contactMapper.toResponse(saved);
        //
    }

    //
}
