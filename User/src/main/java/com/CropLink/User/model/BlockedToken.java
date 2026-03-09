package com.CropLink.User.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "blocked_tokens")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedToken {

    @Id
    @Column(nullable = false, length = 1024)
    private String token;

    /** When this token naturally expires — used for cleanup */
    @Column(nullable = false)
    private Instant expiresAt;
}
