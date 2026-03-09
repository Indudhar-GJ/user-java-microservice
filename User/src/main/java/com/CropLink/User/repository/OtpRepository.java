package com.CropLink.User.repository;

import com.CropLink.User.model.OtpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface OtpRepository extends JpaRepository<OtpRecord, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM OtpRecord o WHERE o.createdAt < :expiry")
    void deleteExpiredOtps(Instant expiry);
}
