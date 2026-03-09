package com.CropLink.User.dto;

import com.CropLink.User.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String userId;
    private String email;
    private UserRole role;
    private String message;
}
