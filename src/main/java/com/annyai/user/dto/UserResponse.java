package com.annyai.user.dto;

import com.annyai.user.model.User;
import com.annyai.user.model.UserRole;
import com.annyai.user.model.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        UserRole role,
        UserStatus status
) {

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }
}