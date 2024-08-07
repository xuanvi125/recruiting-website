package com.bugboo.CareerConnect.service;

import com.bugboo.CareerConnect.domain.Role;
import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.domain.VerificationToken;
import com.bugboo.CareerConnect.domain.dto.request.RequestRegisterUserDTO;
import com.bugboo.CareerConnect.repository.RoleRepository;
import com.bugboo.CareerConnect.repository.UserRepository;
import com.bugboo.CareerConnect.repository.VerificationTokenRepository;
import com.bugboo.CareerConnect.type.exception.AppException;
import com.bugboo.CareerConnect.utils.TokenUtils;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendEmailService sendEmailService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, VerificationTokenRepository verificationTokenRepository, PasswordEncoder passwordEncoder, SendEmailService sendEmailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.sendEmailService = sendEmailService;
    }

    @Transactional
    public User register(RequestRegisterUserDTO requestRegisterUserDTO) throws MessagingException {
        User userDB = userRepository.findByEmail(requestRegisterUserDTO.getEmail()).orElse(null);
        if (userDB != null) {
            throw new AppException("Email already exists", 400);
        }
        User user = new User();

        // save user
        user.setEmail(requestRegisterUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(requestRegisterUserDTO.getPassword()));
        user.setName(requestRegisterUserDTO.getName());

        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        user = userRepository.save(user);

        // create verification token
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(TokenUtils.generateToken());
        verificationToken = verificationTokenRepository.save(verificationToken);

        // send verification email
        try {
            sendEmailService.sendEmailVerifyAccount(user, verificationToken.getToken());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
//        sendEmailService.sendEmailVerifyAccount(user, verificationToken.getToken());
        return user;
    }

    public User verifyAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndExpirationAfter(token, Instant.now()).orElseThrow(() -> new AppException("Invalid token or token expired", 400));
        User user = verificationToken.getUser();
        user.setActive(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
        return user;
    }
}
