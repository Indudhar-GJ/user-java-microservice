package com.CropLink.User.service.impl;

import com.CropLink.User.dto.AuthResponse;
import com.CropLink.User.dto.BankDetailDto;
import com.CropLink.User.dto.LoginRequest;
import com.CropLink.User.dto.RegisterRequest;
import com.CropLink.User.model.BankDetail;
import com.CropLink.User.model.User;
import com.CropLink.User.repository.UserRepository;
import com.CropLink.User.security.JwtService;
import com.CropLink.User.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new com.CropLink.User.exception.UserAlreadyExistsException("Email already in use");
        }

        List<BankDetail> bankDetails = null;
        if (request.getBankDetails() != null) {
            bankDetails = request.getBankDetails()
                    .stream()
                    .map(this::mapToBankDetail)
                    .collect(Collectors.toList());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .bankDetails(bankDetails)
                .role(request.getRole())
                .active(true)
                .build();

        User saved = userRepository.save(user);

        String token = jwtService.generateToken(saved.getEmail(), saved.getRole());
        return new AuthResponse(token, saved.getId(), saved.getEmail(), saved.getRole(), "Registration successful");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getEmail(),
                request.getPassword());
        authenticationManager.authenticate(authToken);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole(), "Login successful");
    }

    private BankDetail mapToBankDetail(BankDetailDto dto) {
        return new BankDetail(dto.getAccountNumber(), dto.getIfscCode());
    }
}
