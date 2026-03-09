package com.CropLink.User.service;

import com.CropLink.User.dto.AuthResponse;
import com.CropLink.User.dto.LoginRequest;
import com.CropLink.User.dto.RegisterRequest;

public interface UserService {

    /**
     * Validates the request and sends an OTP to the email. Returns a simple acknowledgement message.
     */
    String initiateRegistration(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}


