package com.CropLink.User.service;

import com.CropLink.User.dto.AuthResponse;
import com.CropLink.User.dto.LoginRequest;
import com.CropLink.User.dto.RegisterRequest;

public interface UserService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}

