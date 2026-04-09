# Contact Management Module — Design Spec
**Date:** 2026-04-07
**Status:** Approved

---

## Context

This is a Spring Boot learning project. The developer has basic Java knowledge and wants to learn Spring Boot by building a real contact management application. This spec covers the first module: **Contact CRUD** (Create, Read, Update, Delete).

The goal is to teach Spring Boot's core layers (controller, service, repository, entity) through a practical, well-structured example that applies clean code principles, SOLID, and constructor-based Dependency Injection from the start.

---

## Architecture

**Pattern:** Layered Architecture (N-Tier) with Clean Code and SOLID principles.

**Why this pattern:** It is the standard Spring Boot pattern used in all tutorials and documentation. Each layer teaches a distinct Spring concept. It is beginner-friendly while still being production-ready in structure.

**Key principles applied:**
- **Constructor Injection** (not field `@Autowired`) — testable, immutable dependencies
- **Service Interface + Implementation** — Dependency Inversion (SOLID "D"), makes services mockable in tests
- **Custom Exceptions** — clean, meaningful error signalling instead of raw `RuntimeException`
- **Global Exception Handler** — centralised HTTP error response mapping (`@ControllerAdvice`)
- **DTO separation** — the API contract is independent of the database schema

---

## Project Structure

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── ContactManagementApplication.java
│   │   ├── controller/
│   │   │   └── ContactController.java
│   │   ├── service/
│   │   │   ├── ContactService.java           ← interface
│   │   │   └── impl/
│   │   │       └── ContactServiceImpl.java   ← implementation
│   │   ├── repository/
│   │   │   └── ContactRepository.java
│   │   ├── model/
│   │   │   └── Contact.java
│   │   ├── dto/
│   │   │   ├── ContactRequest.java
│   │   │   └── ContactResponse.java
│   │   └── exception/
│   │       ├── ContactNotFoundException.java
│   │       └── GlobalExceptionHandler.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/org/example/
        └── controller/
            └── ContactControllerTest.java
```

**Folder responsibilities:**

| Folder | Spring Annotation | Role |
|---|---|---|
| `controller/` | `@RestController` | Receives HTTP requests, delegates to service, returns JSON responses. No business logic here. |
| `service/` | `@Service` | Business logic and rules. Knows nothing about HTTP or SQL. |
| `repository/` | `@Repository` | Database access. Spring Data JPA auto-generates SQL from method names. |
| `model/` | `@Entity` | Java class that maps 1:1 to a MariaDB table. Each field = a column. |
| `dto/` | (none) | Input/output shapes for the API. Decouples API contract from DB schema. |
| `exception/` | `@ControllerAdvice` | Custom exceptions + centralised HTTP error response handler. |
| `resources/` | — | `application.properties`: MariaDB connection, port, JPA config. |

---

## Data Model

**Entity:** `Contact` → maps to MariaDB table `contacts`

| Java Field | DB Column | Type | Constraints |
|---|---|---|---|
| `id` | `id` | `BIGINT` | Primary key, auto-generated |
| `firstName` | `first_name` | `VARCHAR(100)` | Not null |
| `lastName` | `last_name` | `VARCHAR(100)` | Not null |
| `email` | `email` | `VARCHAR(255)` | Not null, unique |
| `phone` | `phone` | `VARCHAR(20)` | Optional |
| `street` | `street` | `VARCHAR(255)` | Optional |
| `city` | `city` | `VARCHAR(100)` | Optional |
| `country` | `country` | `VARCHAR(100)` | Optional |
| `createdAt` | `created_at` | `DATETIME` | Auto-set on creation |

Spring Boot will auto-create/update this table via `spring.jpa.hibernate.ddl-auto=update`.

---

## REST API Endpoints

Base path: `/api/contacts`

| Method | Path | Description | Success Status |
|---|---|---|---|
| `POST` | `/api/contacts` | Create a new contact | `201 Created` |
| `GET` | `/api/contacts` | List all contacts | `200 OK` |
| `GET` | `/api/contacts/{id}` | Get a contact by ID | `200 OK` |
| `PUT` | `/api/contacts/{id}` | Update a contact | `200 OK` |
| `DELETE` | `/api/contacts/{id}` | Delete a contact | `204 No Content` |

**Request body (POST / PUT):**
```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane@example.com",
  "phone": "0412345678",
  "street": "123 Main St",
  "city": "Sydney",
  "country": "Australia"
}
```

**Response body (GET / POST / PUT):**
```json
{
  "id": 1,
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane@example.com",
  "phone": "0412345678",
  "street": "123 Main St",
  "city": "Sydney",
  "country": "Australia",
  "createdAt": "2026-04-07T10:00:00"
}
```

**Error responses:**
- `404 Not Found` — contact ID does not exist (thrown by `ContactNotFoundException`)
- `400 Bad Request` — validation failed (e.g. missing required fields, invalid email format)
- `500 Internal Server Error` — unexpected errors

---

## Dependency Injection Pattern

Constructor injection is used throughout. Example:

```java
@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }
}
```

Spring detects the single constructor and injects the matching bean automatically. The `final` field ensures the dependency is immutable after construction.

The `ContactController` depends on the `ContactService` **interface**, not `ContactServiceImpl`. This is Dependency Inversion — the high-level layer (controller) does not know about the low-level detail (which implementation is used).

---

## Service Interface Pattern

```java
// ContactService.java (interface)
public interface ContactService {
    ContactResponse create(ContactRequest request);
    List<ContactResponse> findAll();
    ContactResponse findById(Long id);
    ContactResponse update(Long id, ContactRequest request);
    void delete(Long id);
}

// ContactServiceImpl.java (implementation)
@Service
public class ContactServiceImpl implements ContactService {
    // constructor-injected ContactRepository
    // all methods implemented here
}
```

---

## Error Handling

`ContactNotFoundException` extends `RuntimeException`:
```java
public class ContactNotFoundException extends RuntimeException {
    public ContactNotFoundException(Long id) {
        super("Contact not found with id: " + id);
    }
}
```

`GlobalExceptionHandler` maps exceptions to HTTP responses:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ContactNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ContactNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
```

---

## Input Validation

`ContactRequest` uses Bean Validation annotations:
- `@NotBlank` on `firstName`, `lastName`, `email`
- `@Email` on `email`
- Controller uses `@Valid` on the request body parameter

---

## Dependencies (build.gradle.kts)

Replace the current build file with Spring Boot Gradle plugin and these starters:

| Dependency | Purpose |
|---|---|
| `spring-boot-starter-web` | REST API support (`@RestController`, HTTP) |
| `spring-boot-starter-data-jpa` | JPA/Hibernate ORM + `JpaRepository` |
| `org.mariadb.jdbc:mariadb-java-client` | MariaDB JDBC driver |
| `spring-boot-starter-validation` | Bean Validation (`@NotBlank`, `@Email`) |
| `spring-boot-starter-test` | JUnit 5 + MockMvc for tests |

---

## Database Configuration (application.properties)

```properties
# Server
server.port=8080

# MariaDB
spring.datasource.url=jdbc:mariadb://localhost:3306/contact_management
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
```

---

## Testing Approach

A `ContactControllerTest` will use Spring's `MockMvc` to test each endpoint without starting a real server:
- POST creates a contact and returns 201
- GET returns list of contacts
- GET by ID returns the contact
- GET by non-existent ID returns 404
- PUT updates the contact
- DELETE returns 204

---

## What You'll Learn Building This Module

| Concept | Where you'll see it |
|---|---|
| `@SpringBootApplication` | `ContactManagementApplication.java` |
| `@RestController`, `@RequestMapping` | `ContactController.java` |
| `@Entity`, `@Id`, `@GeneratedValue` | `Contact.java` |
| `JpaRepository` | `ContactRepository.java` |
| `@Service`, interface + impl pattern | `ContactService` / `ContactServiceImpl` |
| Constructor-based DI | All classes |
| `@ControllerAdvice`, `@ExceptionHandler` | `GlobalExceptionHandler.java` |
| `@Valid`, `@NotBlank`, `@Email` | `ContactRequest.java` |
| `ResponseEntity` | `ContactController.java` |
| `application.properties` | MariaDB config |
