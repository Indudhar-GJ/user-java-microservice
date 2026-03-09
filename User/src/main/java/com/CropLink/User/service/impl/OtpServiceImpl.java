package com.CropLink.User.service.impl;

import com.CropLink.User.dto.BankDetailDto;
import com.CropLink.User.dto.RegisterRequest;
import com.CropLink.User.dto.VerifyOtpRequest;
import com.CropLink.User.exception.InvalidOtpException;
import com.CropLink.User.model.BankDetail;
import com.CropLink.User.model.OtpRecord;
import com.CropLink.User.model.User;
import com.CropLink.User.repository.OtpRepository;
import com.CropLink.User.repository.UserRepository;
import com.CropLink.User.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final long OTP_EXPIRY_MINUTES = 10;

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    /** In-memory store of pending registrations keyed by email */
    private final Map<String, RegisterRequest> pendingRegistrations = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public void sendOtp(String email, RegisterRequest request) {
        // Delete any existing OTP for this email
        otpRepository.deleteById(email);

        // Generate 6-digit OTP
        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        // Store in DB
        OtpRecord record = OtpRecord.builder()
                .email(email)
                .otp(otp)
                .createdAt(Instant.now())
                .build();
        otpRepository.save(record);

        // Cache the registration request
        pendingRegistrations.put(email, request);

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("CropLink - Your OTP Code");
        message.setText(
                "Hello " + request.getName() + ",\n\n" +
                "Your OTP for CropLink registration is: " + otp + "\n\n" +
                "This OTP is valid for " + OTP_EXPIRY_MINUTES + " minutes.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Regards,\nCropLink Team"
        );
        mailSender.send(message);
    }

    @Override
    @Transactional
    public String verifyOtpAndRegister(VerifyOtpRequest request) {
        String email = request.getEmail();

        // Clean up all expired OTPs first
        Instant expiry = Instant.now().minus(Duration.ofMinutes(OTP_EXPIRY_MINUTES));
        otpRepository.deleteExpiredOtps(expiry);

        // Find OTP record
        OtpRecord record = otpRepository.findById(email)
                .orElseThrow(() -> new InvalidOtpException("OTP not found or expired. Please request a new OTP."));

        // Check OTP match
        if (!record.getOtp().equals(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP. Please check and try again.");
        }

        // Retrieve pending registration
        RegisterRequest registerRequest = pendingRegistrations.remove(email);
        if (registerRequest == null) {
            throw new InvalidOtpException("Registration session expired. Please start over.");
        }

        // Delete OTP record
        otpRepository.deleteById(email);

        // Create user
        List<BankDetail> bankDetails = null;
        if (registerRequest.getBankDetails() != null) {
            bankDetails = registerRequest.getBankDetails()
                    .stream()
                    .map(this::mapToBankDetail)
                    .collect(Collectors.toList());
        }

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .bankDetails(bankDetails)
                .role(registerRequest.getRole())
                .active(true)
                .build();

        userRepository.save(user);

        return "Registration successful. You can now login at /user/login";
    }

    private BankDetail mapToBankDetail(BankDetailDto dto) {
        return new BankDetail(dto.getAccountNumber(), dto.getIfscCode());
    }
}
