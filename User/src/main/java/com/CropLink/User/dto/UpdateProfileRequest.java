package com.CropLink.User.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateProfileRequest {
    private String name;
    private String phoneNumber;
    private String password; // Optional
    private List<BankDetailDto> bankDetails;
}
