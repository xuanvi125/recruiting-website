package com.bugboo.CareerConnect.repository;

import com.bugboo.CareerConnect.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
    Optional<VerificationToken> findByTokenAndExpirationAfter(String token, Instant expiration);
}
