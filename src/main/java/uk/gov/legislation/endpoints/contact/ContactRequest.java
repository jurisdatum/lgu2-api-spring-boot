package uk.gov.legislation.endpoints.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactRequest(

    @NotBlank(message = "Full name is required")
    String fullName,

    @NotBlank(message = "Email address is required")
    @Email(message = "Email address must be valid")
    String email,

    String telephone,

    @NotBlank(message = "Address is required")
    String address,

    String additionalDetails

) {}
