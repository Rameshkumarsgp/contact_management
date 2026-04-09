package com.contacthub.mapper;

import com.contacthub.dto.ContactRequest;
import com.contacthub.dto.ContactResponse;
import com.contacthub.model.Contact;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {

    public Contact toEntity(ContactRequest request) {
        Contact contact = new Contact();
        contact.setFirstName(request.firstName());
        contact.setLastName(request.lastName());
        contact.setEmail(request.email());
        contact.setPhone(request.phone());
        contact.setStreet(request.street());
        contact.setCity(request.city());
        contact.setCountry(request.country());
        return contact;
    }

    public ContactResponse toResponse(Contact contact) {
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
    }
}
