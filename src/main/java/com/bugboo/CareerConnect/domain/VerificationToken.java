package com.bugboo.CareerConnect.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@Table(name = "verification_tokens")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String token;
    private Instant expiration = Instant.now().plus(10, ChronoUnit.MINUTES);

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
