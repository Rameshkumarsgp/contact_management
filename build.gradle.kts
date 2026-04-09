plugins {
    id("java")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "6.25.0"
    jacoco
}

group = "com.contacthub"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Web — REST API support (@RestController, HTTP)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Data JPA — database access with JpaRepository
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Bean Validation — @NotBlank, @Email on DTOs
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // MariaDB JDBC driver
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // Testing — JUnit 5 + MockMvc
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // H2 in-memory database for @DataJpaTest (replaces MariaDB in the test slice)
    testRuntimeOnly("com.h2database:h2")
}

tasks.register("installGitHooks") {
    description = "Configures git to use .githooks/ so hooks are shared via the repo"
    group = "setup"
    doLast {
        providers.exec { commandLine("git", "config", "core.hooksPath", ".githooks") }.result.get()
        providers.exec { commandLine("chmod", "+x", ".githooks/pre-commit") }.result.get()
        println("✅ Git hooks installed — .githooks/pre-commit is active")
    }
}

tasks.build {
    dependsOn("installGitHooks")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.75".toBigDecimal()
            }
        }
    }
}

spotless {
    java {
        eclipse()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
