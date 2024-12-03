package com.clozex.shop.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @Email(message = "Email is not valid",
                regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        @NotBlank(message = "Email cannot be empty")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, max = 20, message = "Minimum size is 8. Maximum size is 20")
        String password
) {
}
