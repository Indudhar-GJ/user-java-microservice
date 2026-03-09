package com.CropLink.User.service;

import com.CropLink.User.dto.RegisterRequest;
import com.CropLink.User.dto.VerifyOtpRequest;

public interface OtpService {

    /**
     * Generates a 6-digit OTP, saves it in the DB, sends it via email,
     * and caches the RegisterRequest for later use.
     */
    void sendOtp(String email, RegisterRequest request);

    /**
     * Validates the OTP and creates the user. Returns a success message.
     */
    String verifyOtpAndRegister(VerifyOtpRequest request);
}

