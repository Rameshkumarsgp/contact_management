package com.contacthub.util;

import com.contacthub.dto.ContactRequest;

public class ContactRequestBuilder {

    private String firstName = "Jane";
    private String email     = "jane@example.com";
    private String street    = "123 Main St";
    private String city      = "Sydney";

    public ContactRequestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public ContactRequestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public ContactRequestBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public ContactRequestBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public ContactRequest build() {
        return new ContactRequest(firstName, "Doe", email, "0412345678", street, city, "Australia");
    }
}
