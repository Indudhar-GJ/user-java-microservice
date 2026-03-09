package com.CropLink.User.controller;

import com.CropLink.User.dto.*;
import com.CropLink.User.model.BlockedToken;
import com.CropLink.User.repository.BlockedTokenRepository;
import com.CropLink.User.security.JwtService;
import com.CropLink.User.service.OtpService;
import com.CropLink.User.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final BlockedTokenRepository blockedTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        String message = userService.initiateRegistration(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.of(message));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        String message = otpService.verifyOtpAndRegister(request);
        return ResponseEntity.ok(ApiResponse.of(message));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse auth = userService.login(request);
        return ResponseEntity.ok(new ApiResponse<>("Login successful", auth));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.updateProfile(email, request);
        return ResponseEntity.ok(ApiResponse.of("Profile updated successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(ApiResponse.of("No token provided"));
        }

        String token = authHeader.substring(7);

        if (!blockedTokenRepository.existsByToken(token)) {
            BlockedToken blocked = BlockedToken.builder()
                    .token(token)
                    .expiresAt(jwtService.getExpiration(token))
                    .build();
            blockedTokenRepository.save(blocked);
        }

        return ResponseEntity.ok(ApiResponse.of("Logged out successfully"));
    }
}


