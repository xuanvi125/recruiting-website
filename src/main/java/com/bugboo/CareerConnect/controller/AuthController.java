package com.bugboo.CareerConnect.controller;

import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.domain.dto.request.RequestRegisterUserDTO;
import com.bugboo.CareerConnect.service.AuthService;
import com.bugboo.CareerConnect.type.annotation.ApiMessage;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ApiMessage("Please check your email to verify your account")
    public ResponseEntity<User> register(@Valid @RequestBody RequestRegisterUserDTO  requestRegisterUserDTO) throws MessagingException {
        return ResponseEntity.ok(authService.register(requestRegisterUserDTO));
    }

    @GetMapping("/verify-account")
    @ApiMessage("Account verified successfully")
    public ResponseEntity<User> verifyAccount(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyAccount(token));
    }
}
