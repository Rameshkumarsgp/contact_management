package com.contacthub.exception;

// A custom exception that means "the contact you asked for does not exist in the database"
//
// Why extend RuntimeException?
//   RuntimeException is "unchecked" — callers don't need to declare "throws ContactNotFoundException" in their method signature.
//   This keeps the service and controller code clean.
//
// Why not just throw RuntimeException("Contact not found")?
//   A named exception is self-documenting and lets GlobalExceptionHandler handle it specifically (→ 404 HTTP status).
//   A plain RuntimeException would be caught by the generic 500 handler instead.
public class ContactNotFoundException extends RuntimeException {

    public ContactNotFoundException(Long id) {
        super("Contact not found with id: " + id);
    }
}
