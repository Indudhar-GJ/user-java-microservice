package com.CropLink.User.repository;

import com.CropLink.User.model.BlockedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface BlockedTokenRepository extends JpaRepository<BlockedToken, String> {

    boolean existsByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM BlockedToken b WHERE b.expiresAt < :now")
    void deleteExpiredTokens(Instant now);
}
