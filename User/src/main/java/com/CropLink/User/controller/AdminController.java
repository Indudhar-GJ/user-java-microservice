package com.CropLink.User.controller;

import com.CropLink.User.dto.ApiResponse;
import com.CropLink.User.dto.RegisterRequest;
import com.CropLink.User.model.User;
import com.CropLink.User.model.UserRole;
import com.CropLink.User.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email) {
        List<User> users = userService.getAllUsers(role, phoneNumber, email);
        return ResponseEntity.ok(new ApiResponse<>("Users retrieved successfully", users));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody RegisterRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.of("User updated successfully"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.of("User deleted successfully"));
    }
}
