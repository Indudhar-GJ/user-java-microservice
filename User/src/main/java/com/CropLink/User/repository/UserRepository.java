package com.CropLink.User.repository;

import com.CropLink.User.model.User;
import com.CropLink.User.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:phoneNumber IS NULL OR u.phoneNumber LIKE %:phoneNumber%) AND " +
           "(:email IS NULL OR u.email LIKE %:email%)")
    List<User> findByFilters(@Param("role") UserRole role, 
                             @Param("phoneNumber") String phoneNumber, 
                             @Param("email") String email);
}
