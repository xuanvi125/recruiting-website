package com.bugboo.CareerConnect.repository;

import com.bugboo.CareerConnect.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    User findByEmailAndRefreshToken(String email, String refreshToken);

    User findByEmailOrGoogleId(String email, String googleId);
}
