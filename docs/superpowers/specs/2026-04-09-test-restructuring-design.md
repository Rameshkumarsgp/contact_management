# Test Restructuring with @Nested — Design Spec
**Date:** 2026-04-09
**Status:** Approved

---

## Problem

ContactControllerTest has 6 tests for 2 endpoints. As CRUD endpoints grow (PUT, DELETE, GET-by-ID, GET-all), this file will reach 20+ tests in a flat list — hard to navigate, debug, and maintain.

## Solution

Use JUnit 5 `@Nested` inner classes to group tests by endpoint/method. Shared setup stays in the outer class; each inner class groups related tests.

## Scope

- **Restructure:** `ContactControllerTest.java`, `ContactServiceImplTest.java`
- **Keep as-is:** `ContactRepositoryTest.java` (only 2 tests, no benefit from nesting)

## Target Structure

### ContactControllerTest

```
ContactControllerTest (outer)
  ├── @Autowired MockMvc, ObjectMapper
  ├── @MockitoBean ContactService
  │
  ├── @Nested CreateContact
  │   ├── returnsCreated()
  │   ├── returnsBadRequest_whenFirstNameMissing()
  │   └── returnsBadRequest_whenInvalidEmail()
  │
  └── @Nested FetchContact
      ├── returnsOk_whenEmailExists()
      ├── returnsBadRequest_whenEmailParamMissing()
      └── returnsBadRequest_whenInvalidEmail()
```

### ContactServiceImplTest

```
ContactServiceImplTest (outer)
  ├── @Mock ContactRepository
  ├── @BeforeEach setUp()
  │
  ├── @Nested Create
  │   ├── savesContact_returnsResponse()
  │   └── throwsException_whenDuplicateEmail()
  │
  └── @Nested Fetch
      ├── returnsResponse_whenEmailExists()
      └── throwsException_whenEmailNotFound()
```

## Naming Convention Change

Test method names drop the endpoint prefix since `@Nested` class name provides it:

| Before | After |
|---|---|
| `createContact_returnsCreated()` | `CreateContact > returnsCreated()` |
| `fetchContact_returnsOk_whenEmailExists()` | `FetchContact > returnsOk_whenEmailExists()` |
| `create_savesContact_returnsResponse()` | `Create > savesContact_returnsResponse()` |

## Rules for Adding New Endpoints

When adding PUT, DELETE, or other endpoints:
1. Add a new `@Nested` class (e.g., `@Nested class UpdateContact`)
2. All tests for that endpoint go inside the nested class
3. Shared fields from the outer class are automatically accessible

## Files to Modify

| File | Action |
|---|---|
| `src/test/java/com/contacthub/controller/ContactControllerTest.java` | Wrap tests in @Nested classes |
| `src/test/java/com/contacthub/service/ContactServiceImplTest.java` | Wrap tests in @Nested classes |

## Verification

- Run `./gradlew test` — all 12 tests pass
- IntelliJ test runner shows nested tree structure
