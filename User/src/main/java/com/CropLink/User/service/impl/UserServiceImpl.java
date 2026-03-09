package com.CropLink.User.service.impl;

import com.CropLink.User.dto.AuthResponse;
import com.CropLink.User.dto.LoginRequest;
import com.CropLink.User.dto.RegisterRequest;
import com.CropLink.User.model.User;
import com.CropLink.User.repository.UserRepository;
import com.CropLink.User.security.JwtService;
import com.CropLink.User.service.OtpService;
import com.CropLink.User.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    @Override
    public String initiateRegistration(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new com.CropLink.User.exception.UserAlreadyExistsException("Email already in use");
        }
        otpService.sendOtp(request.getEmail(), request);
        return "OTP sent to " + request.getEmail() + ". Please verify within 10 minutes at /user/verify-otp .";
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        authenticationManager.authenticate(authToken);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole());
    }
}

