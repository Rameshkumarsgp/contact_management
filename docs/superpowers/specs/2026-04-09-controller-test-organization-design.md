# Design: ContactControllerTest Organization with @Nested Classes

**Date:** 2026-04-09

## Context

`ContactControllerTest` currently has 6 tests covering 2 endpoints. As the remaining CRUD endpoints are added (update, delete), the test count will grow to ~15–20. Without structure, a flat list of tests becomes hard to navigate. This design establishes an organization pattern before that growth happens.

## Decision

Use JUnit 5 `@Nested` inner classes inside a single `ContactControllerTest.java` — one nested class per endpoint. No new libraries or annotations are needed; `@Nested` and `@DisplayName` are already on the classpath via `spring-boot-starter-test`.

## Structure

```
ContactControllerTest                         ← @WebMvcTest, shared fields
  @Nested CreateContact                       ← POST /api/contacts
    createContact_returnsCreated
    createContact_returnsBadRequest_whenFirstNameMissing
    createContact_returnsBadRequest_whenInvalidEmail
  @Nested FetchContact                        ← GET /api/contacts
    fetchContact_returnsOk_whenEmailExists
    fetchContact_returnsBadRequest_whenEmailParamMissing
    fetchContact_returnsBadRequest_whenInvalidEmail
```

Future endpoints follow the same pattern — add a new `@Nested` class, tests go inside it.

## Key Rules

- **Outer class** holds all shared fields: `MockMvc`, `@MockitoBean ContactService`, `ObjectMapper`. Nested classes inherit these automatically.
- **One `@Nested` class per endpoint.** Name matches the operation (e.g., `CreateContact`, `FetchContact`, `UpdateContact`, `DeleteContact`).
- **`@DisplayName`** on each nested class shows the HTTP method + path (e.g., `"POST /api/contacts"`). This makes the test report self-documenting.
- **Test method names** keep the existing convention: `verb_outcome_whenCondition` (e.g., `createContact_returnsBadRequest_whenInvalidEmail`).

## Why Not Multiple Files

`@WebMvcTest` loads a Spring test context per class. One `ContactControllerTest.java` = one context load shared across all nested classes. Multiple files = multiple context loads = slower tests. For a single controller, one test class is the standard.

## File Affected

`src/test/java/com/contacthub/controller/ContactControllerTest.java`

## Verification

Run `./mvnw test -pl . -Dtest=ContactControllerTest` — all 6 existing tests must pass after the reorganization. No test logic changes, only structural grouping.
