package com.contacthub.controller;

import com.contacthub.dto.ContactRequest;
import com.contacthub.dto.ContactResponse;
import com.contacthub.service.ContactService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
@Validated  // Enables validation on method parameters (@Email, @NotBlank on @RequestParam)
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ContactResponse> create(@Valid @RequestBody ContactRequest request) {
        ContactResponse response = contactService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ContactResponse> fetch(
            @RequestParam @Email(message = "Email must be a valid email address") String emailId) {
        ContactResponse response = contactService.fetch(emailId);
        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //
}
