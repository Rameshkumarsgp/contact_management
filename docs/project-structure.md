# Project Structure Guide

## Full Tree

```
contact_management/
├── src/
│   ├── main/
│   │   ├── java/com/contacthub/
│   │   │   ├── ContactManagementApplication.java
│   │   │   ├── controller/
│   │   │   │   └── ContactController.java
│   │   │   ├── service/
│   │   │   │   ├── ContactService.java          ← interface
│   │   │   │   └── impl/
│   │   │   │       └── ContactServiceImpl.java  ← implementation
│   │   │   ├── repository/
│   │   │   │   └── ContactRepository.java
│   │   │   ├── model/
│   │   │   │   └── Contact.java
│   │   │   ├── dto/
│   │   │   │   ├── ContactRequest.java
│   │   │   │   └── ContactResponse.java
│   │   │   └── exception/
│   │   │       ├── ContactNotFoundException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/contacthub/
│           └── controller/
│               └── ContactControllerTest.java
├── gradle/
├── build/
├── build.gradle.kts
└── settings.gradle.kts
```

---

## How a Request Flows Through the Layers

```
Client (Postman / browser / app)
        │
        │  HTTP Request (POST /api/contacts + JSON body)
        ▼
┌─────────────────────────────┐
│        controller/          │  Receives request, validates input, delegates to service
│  ContactController.java     │  Annotation: @RestController
│                             │  Knows about: HTTP, JSON, DTOs
│                             │  Does NOT know about: business rules, SQL
└──────────────┬──────────────┘
               │  calls contactService.create(request)
               ▼
┌─────────────────────────────┐
│         service/            │  Business logic and rules (e.g. no duplicate emails)
│  ContactService.java        │  Annotation: @Service
│  ContactServiceImpl.java    │  Knows about: DTOs, entities, business rules
│                             │  Does NOT know about: HTTP, SQL
└──────────────┬──────────────┘
               │  calls contactRepository.save(contact)
               ▼
┌─────────────────────────────┐
│        repository/          │  Database access — Spring writes the SQL for you
│  ContactRepository.java     │  Annotation: @Repository (auto-applied by Spring Data)
│                             │  Knows about: entities, database
│                             │  Does NOT know about: HTTP, business rules
└──────────────┬──────────────┘
               │
               ▼
         MariaDB Database
```

---

## Folder Reference

### `controller/`

**Purpose:** The front door of your app. Receives HTTP requests and sends JSON responses.

**Rule:** No business logic here. If you find yourself writing an `if` statement about data rules in a controller, move it to the service.

**Spring annotation:** `@RestController`

**What it handles:**
- Mapping URLs to Java methods (`@PostMapping`, `@GetMapping`, etc.)
- Deserializing JSON request bodies into DTOs (`@RequestBody`)
- Triggering input validation (`@Valid`)
- Returning the right HTTP status code (`ResponseEntity`)

---

### `service/`

**Purpose:** The brain of your app. All business logic and rules live here.

**Why split into interface + impl?**
- `ContactService.java` (interface) defines *what* the service does — its contract
- `ContactServiceImpl.java` (impl) defines *how* it does it
- The controller depends on the interface, not the implementation. This is the **Dependency Inversion Principle** (SOLID "D")
- In tests, you can swap the implementation for a fake (mock) without touching any other code

**Spring annotation:** `@Service` (on the impl class)

---

### `repository/`

**Purpose:** Database access layer. Talks to MariaDB.

**How it works:** You declare an interface extending `JpaRepository<Contact, Long>` and Spring Data JPA auto-generates all SQL:

| Method | Generated SQL |
|---|---|
| `save(contact)` | `INSERT INTO contacts ...` or `UPDATE contacts ...` |
| `findAll()` | `SELECT * FROM contacts` |
| `findById(id)` | `SELECT * FROM contacts WHERE id = ?` |
| `deleteById(id)` | `DELETE FROM contacts WHERE id = ?` |
| `findByEmail(email)` | `SELECT * FROM contacts WHERE email = ?` |

You never write SQL manually for standard operations.

**Spring annotation:** `@Repository` (automatically applied by Spring Data)

---

### `model/`

**Purpose:** Java representation of your database table.

Each class in `model/` maps to one table in MariaDB. Each field maps to one column. JPA/Hibernate uses annotations to know how to convert between a Java object and a database row.

**Key annotations:**
- `@Entity` — this class is a DB table
- `@Table(name = "contacts")` — sets the table name
- `@Id` — marks the primary key
- `@GeneratedValue(strategy = IDENTITY)` — auto-increment (1, 2, 3...)
- `@Column(nullable = false, unique = true)` — column constraints
- `@PrePersist` — runs a method automatically before first save (used to set `createdAt`)

---

### `dto/`

**Purpose:** Defines the shape of data that crosses the API boundary (in and out).

DTO = **Data Transfer Object**. Two files:

| File | Direction | Purpose |
|---|---|---|
| `ContactRequest.java` | Client → Server | What the client sends in the request body |
| `ContactResponse.java` | Server → Client | What the server sends back |

**Why not use the `Contact` entity directly?**
- The entity has fields you don't want clients to set (e.g. `id`, `createdAt`)
- You control exactly what is exposed in the API, independently of the DB schema
- Validation annotations (`@NotBlank`, `@Email`) belong on the request DTO, not on the entity

---

### `exception/`

**Purpose:** Centralised, clean error handling.

Two files:

| File | Role |
|---|---|
| `ContactNotFoundException.java` | Custom exception for "contact not found". Thrown in the service, returns 404. |
| `GlobalExceptionHandler.java` | Intercepts all exceptions from all controllers and maps them to proper HTTP responses. |

**Why a global handler?**
Without it, every controller method would need its own `try/catch` block to return the right HTTP status code. The `@RestControllerAdvice` handler does this in one place for the entire app.

**HTTP status mapping:**

| Exception | HTTP Status |
|---|---|
| `ContactNotFoundException` | `404 Not Found` |
| `MethodArgumentNotValidException` (validation failure) | `400 Bad Request` |
| `IllegalArgumentException` (e.g. duplicate email) | `400 Bad Request` |
| Any other `Exception` | `500 Internal Server Error` |

---

### `resources/application.properties`

**Purpose:** App configuration. No Java code — just key=value settings.

This is where you configure:
- Server port (`server.port`)
- Database connection URL, username, password
- Whether JPA should auto-create/update tables (`spring.jpa.hibernate.ddl-auto`)
- Whether to print SQL queries to console (`spring.jpa.show-sql`)

You can have different `application.properties` files per environment (dev, test, prod) using Spring profiles.

---

### `test/`

**Purpose:** Automated tests. Mirror of `src/main/` but never included in the built app.

`ContactControllerTest.java` uses:
- `@WebMvcTest` — loads only the web layer (no database, no real service)
- `MockMvc` — sends fake HTTP requests to the controller
- `@MockBean` — replaces the real service with a controllable fake

This means tests run fast and don't need MariaDB running.

---

### `build.gradle.kts`

**Purpose:** Project build configuration (Kotlin DSL).

Declares:
- Which Gradle plugins to use (Spring Boot, Java)
- Which dependencies (libraries) to download from Maven Central
- Java version target
- Test configuration

---

### `gradle/` + `gradlew` + `gradlew.bat`

**Purpose:** Gradle wrapper — ensures everyone uses the same Gradle version.

`./gradlew` downloads and runs Gradle automatically. No manual Gradle installation needed. `gradlew.bat` is the Windows equivalent.

Common commands:
```bash
./gradlew bootRun      # start the app
./gradlew build        # compile + run tests + package
./gradlew test         # run tests only
./gradlew dependencies # download and list all dependencies
```

---

### `build/`

**Purpose:** Gradle output. Auto-generated, never edit.

Contains compiled `.class` files, test reports, and the packaged `.jar`. Listed in `.gitignore`. Deleted and recreated on every `./gradlew build`.
