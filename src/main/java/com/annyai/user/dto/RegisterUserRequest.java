package com.annyai.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.annyai.user.model.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterUserRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 characters")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,

        UserRole role
) {

        @Override
        public String toString() {
                return "RegisterUserRequest[email=" + email + ", password=****, role=" + role + "]";
        }
}