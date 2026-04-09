package com.contacthub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication turns on:
//   - @Configuration       : this class can define Spring beans
//   - @EnableAutoConfiguration : Spring Boot auto-configures JPA, web server, etc.
//   - @ComponentScan       : Spring scans this package and sub-packages for @Component, @Service, @Repository, @Controller
@SpringBootApplication
public class ContactManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContactManagementApplication.class, args);
    }
}
