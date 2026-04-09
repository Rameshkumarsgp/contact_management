package com.contacthub.filter;

import com.contacthub.controller.ContactController;
import com.contacthub.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
class RequestResponseLoggingFilterTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ContactService contactService;

	@Test
	void filter_doesNotBreakRequest_whenBodyIsValid() throws Exception {
		String body = """
				{
				  "firstName": "Jane",
				  "lastName": "Doe",
				  "email": "jane@example.com",
				  "phone": "0412345678",
				  "street": "123 Main St",
				  "city": "Sydney",
				  "country": "Australia"
				}
				""";

		mockMvc.perform(post("/api/contacts").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated()); // filter should not interfere with normal flow
	}

	@Test
	void filter_doesNotBreakRequest_whenBodyIsInvalid() throws Exception {
		String body = """
				{
				  "lastName": "Doe",
				  "email": "jane@example.com"
				}
				""";

		mockMvc.perform(post("/api/contacts").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isBadRequest()); // missing firstName → 400, filter should not break this
	}
}
