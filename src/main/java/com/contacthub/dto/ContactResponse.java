package com.contacthub.dto;

import java.time.LocalDateTime;

public record ContactResponse(
		//
		Long id, String firstName, String lastName, String email, String phone, String street, String city,
		String country, LocalDateTime createdAt
//
) {
}
