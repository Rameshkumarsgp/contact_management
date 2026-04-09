package com.contacthub.controller;

import com.contacthub.dto.ContactRequest;
import com.contacthub.service.ContactService;
import com.contacthub.util.ContactRequestBuilder;
import com.contacthub.util.ContactResponseBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
            // given
            when(contactService.create(any(ContactRequest.class)))
                    .thenReturn(new ContactResponseBuilder().build());

            // when // then
            mockMvc.perform(post("/api/contacts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new ContactRequestBuilder().build())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("Jane"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.email").value("jane@example.com"));
        }

        @Test
        void createContact_returnsBadRequest_whenFirstNameMissing() throws Exception {
            // given // when // then
            mockMvc.perform(post("/api/contacts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new ContactRequestBuilder().withFirstName(null).build())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.firstName").value("First name is required"));
        }

        @Test
        void createContact_returnsBadRequest_whenInvalidEmail() throws Exception {
            // given // when // then
            mockMvc.perform(post("/api/contacts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new ContactRequestBuilder().withEmail("not-an-email").build())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").value("Email must be a valid email address"));
        }
        //
    }

    @Nested
    @DisplayName("GET /api/contacts")
    class FetchContacts {

        @Test
        void fetchContact_returnsOk_whenEmailExists() throws Exception {
            // given
            String email = "jane@example.com";

            when(contactService.fetch(email))
                    .thenReturn(new ContactResponseBuilder().build());

            // when // then
            mockMvc.perform(get("/api/contacts")
                            .param("email", email))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("Jane"))
                    .andExpect(jsonPath("$.email").value(email));
        }

        @Test
        void fetchContact_returnsBadRequest_whenEmailParamMissing() throws Exception {
            // given // when // then
            mockMvc.perform(get("/api/contacts"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void fetchContact_returnsBadRequest_whenInvalidEmail() throws Exception {
            // given // when // then
            mockMvc.perform(get("/api/contacts")
                            .param("email", "not-an-email"))
                    .andExpect(status().isBadRequest());
        }
        //
    }

    @Nested
    @DisplayName("PUT /api/contacts")
    class UpdateContact {

        @Test
        void updateContact_returnsOk_whenContactExists() throws Exception {
            // given
            when(contactService.update(any(ContactRequest.class)))
                    .thenReturn(new ContactResponseBuilder()
                            .withCity("Melbourne")
                            .withStreet("456 New St")
                            .build());

            // when // then
            mockMvc.perform(put("/api/contacts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new ContactRequestBuilder()
                                            .withCity("Melbourne")
                                            .withStreet("456 New St")
                                            .build())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("jane@example.com"))
                    .andExpect(jsonPath("$.city").value("Melbourne"))
                    .andExpect(jsonPath("$.street").value("456 New St"));
        }

        @Test
        void updateContact_returnsBadRequest_whenFirstNameMissing() throws Exception {
            // given // when // then
            mockMvc.perform(put("/api/contacts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new ContactRequestBuilder().withFirstName(null).build())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.firstName").value("First name is required"));
        }

        @Test
        void updateContact_returnsBadRequest_whenInvalidEmail() throws Exception {
            // given // when // then
            mockMvc.perform(put("/api/contacts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new ContactRequestBuilder().withEmail("not-an-email").build())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.email").value("Email must be a valid email address"));
        }
    }
    //
}
