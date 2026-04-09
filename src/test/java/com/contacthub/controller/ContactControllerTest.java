package com.contacthub.controller;

import com.contacthub.dto.ContactRequest;
import com.contacthub.dto.ContactResponse;
import com.contacthub.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /api/contacts")
    class CreateContact {

        @Test
        void createContact_returnsCreated() throws Exception {
            ContactRequest request = new ContactRequest(
                    "Jane", "Doe", "jane@example.com",
                    "0412345678", "123 Main St", "Sydney", "Australia"
            );

            ContactResponse mockResponse = new ContactResponse(
                    1L, "Jane", "Doe", "jane@example.com",
                    "0412345678", "123 Main St", "Sydney", "Australia",
                    LocalDateTime.now()
            );

            when(contactService.create(any(ContactRequest.class))).thenReturn(mockResponse);

            mockMvc.perform(post("/api/contacts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("Jane"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.email").value("jane@example.com"));
        }

        @Test
        void createContact_returnsBadRequest_whenFirstNameMissing() throws Exception {
            ContactRequest invalidRequest = new ContactRequest(
                    null, "Doe", "jane@example.com",
                    null, null, null, null
            );

            mockMvc.perform(post("/api/contacts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.firstName").value("First name is required"));
        }

        @Test
        void createContact_returnsBadRequest_whenInvalidEmail() throws Exception {
            ContactRequest invalidRequest = new ContactRequest(
                    "Jane", "Doe", "not-an-email",
                    null, null, null, null
            );

            mockMvc.perform(post("/api/contacts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").value("Email must be a valid email address"));
        }
    }

    // --- GET /api/contacts?emailId= ---

    @Test
    void fetchContact_returnsOk_whenEmailExists() throws Exception {
        String emailId = "jane@example.com";

        ContactResponse mockResponse = new ContactResponse(
                1L, "Jane", "Doe", emailId,
                "0412345678", "123 Main St", "Sydney", "Australia",
                LocalDateTime.now()
        );

        when(contactService.fetch(emailId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/contacts")
                        .param("emailId", emailId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.email").value(emailId));
    }

    @Test
    void fetchContact_returnsBadRequest_whenEmailParamMissing() throws Exception {
        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fetchContact_returnsBadRequest_whenInvalidEmail() throws Exception {
        mockMvc.perform(get("/api/contacts")
                        .param("emailId", "not-an-email"))
                .andExpect(status().isBadRequest());
    }
}
