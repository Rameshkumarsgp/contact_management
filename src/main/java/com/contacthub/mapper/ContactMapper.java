package com.contacthub.mapper;

import com.contacthub.domain.Contact;
import com.contacthub.dto.ContactRequest;
import com.contacthub.dto.ContactResponse;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {
    //
    public Contact toEntity(ContactRequest request) {
        //
        Contact contact = new Contact();
        contact.setFirstName(request.firstName());
        contact.setLastName(request.lastName());
        contact.setEmail(request.email());
        contact.setPhone(request.phone());
        contact.setStreet(request.street());
        contact.setCity(request.city());
        contact.setCountry(request.country());

        return contact;
        //
    }

    public ContactResponse toResponse(Contact contact) {
        //
        return new ContactResponse(
                contact.getId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getEmail(),
                contact.getPhone(),
                contact.getStreet(),
                contact.getCity(),
                contact.getCountry(),
                contact.getCreatedAt()
        );
        //
    }

    public void update(Contact existing, ContactRequest request) {
        existing.setFirstName(request.firstName());
        existing.setLastName(request.lastName());
        existing.setPhone(request.phone());
        existing.setStreet(request.street());
        existing.setCity(request.city());
        existing.setCountry(request.country());
    }

    //
}
