package com.CropLink.User.dto;

import com.CropLink.User.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

import org.hibernate.validator.constraints.Length;

@Data
public class RegisterRequest {

    @NotBlank
    @Length(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Name must contain only letters and spaces")
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @Length(min = 10, max = 10, message = "Phone number must be 10 digits")
    @Pattern(regexp = "^[0-9]*$", message = "Phone number must contain only digits")
    private String phoneNumber;

    @NotNull
    private UserRole role;

    private List<BankDetailDto> bankDetails;
}
