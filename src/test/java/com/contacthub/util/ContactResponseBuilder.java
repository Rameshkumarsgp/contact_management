package com.contacthub.util;

import com.contacthub.dto.ContactResponse;

import java.time.LocalDateTime;

public class ContactResponseBuilder {

    private String street = "123 Main St";
    private String city   = "Sydney";

    public ContactResponseBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public ContactResponseBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public ContactResponse build() {
        return new ContactResponse(
                1L, "Jane", "Doe", "jane@example.com",
                "0412345678", street, city, "Australia",
                LocalDateTime.now());
    }
}
