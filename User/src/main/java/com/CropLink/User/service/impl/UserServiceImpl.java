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

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.CropLink.User.model.UserRole;
import com.CropLink.User.model.BankDetail;
import com.CropLink.User.dto.BankDetailDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    public List<User> getAllUsers(UserRole role, String phoneNumber, String email) {
        return userRepository.findByFilters(role, phoneNumber, email);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void updateUser(String id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getBankDetails() != null) {
            List<BankDetail> bankDetails = request.getBankDetails()
                    .stream()
                    .map(this::mapToBankDetail)
                    .collect(Collectors.toList());
            user.setBankDetails(bankDetails);
        }

        userRepository.save(user);
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void updateProfile(String email, com.CropLink.User.dto.UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getBankDetails() != null) {
            List<BankDetail> bankDetails = request.getBankDetails()
                    .stream()
                    .map(this::mapToBankDetail)
                    .collect(Collectors.toList());
            user.setBankDetails(bankDetails);
        }

        userRepository.save(user);
    }

    private BankDetail mapToBankDetail(BankDetailDto dto) {
        return new BankDetail(dto.getAccountNumber(), dto.getIfscCode());
    }
}

