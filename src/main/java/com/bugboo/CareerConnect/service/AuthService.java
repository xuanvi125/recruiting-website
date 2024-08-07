package com.bugboo.CareerConnect.service;

import com.bugboo.CareerConnect.domain.Role;
import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.domain.VerificationToken;
import com.bugboo.CareerConnect.domain.dto.request.RequestLoginDTO;
import com.bugboo.CareerConnect.domain.dto.request.RequestRegisterUserDTO;
import com.bugboo.CareerConnect.domain.dto.response.ResponseLoginDTO;
import com.bugboo.CareerConnect.repository.RoleRepository;
import com.bugboo.CareerConnect.repository.UserRepository;
import com.bugboo.CareerConnect.repository.VerificationTokenRepository;
import com.bugboo.CareerConnect.type.exception.AppException;
import com.bugboo.CareerConnect.utils.TokenUtils;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, VerificationTokenRepository verificationTokenRepository, PasswordEncoder passwordEncoder, SendEmailService sendEmailService, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.sendEmailService = sendEmailService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
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

    public Authentication login(RequestLoginDTO requestLoginDTO) {
        User user = userRepository.findByEmail(requestLoginDTO.getEmail()).orElseThrow(() -> new AppException("Incorrect email or password", 400));
        if(!user.isActive()){
            throw new AppException("Account not verified", 400);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestLoginDTO.getEmail(), requestLoginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}
