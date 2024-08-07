package com.bugboo.CareerConnect.controller;

import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.domain.dto.request.RequestForgotPasswordDTO;
import com.bugboo.CareerConnect.domain.dto.request.RequestLoginDTO;
import com.bugboo.CareerConnect.domain.dto.request.RequestRegisterUserDTO;
import com.bugboo.CareerConnect.domain.dto.request.RequestResetPasswordDTO;
import com.bugboo.CareerConnect.domain.dto.response.ResponseLoginDTO;
import com.bugboo.CareerConnect.repository.UserRepository;
import com.bugboo.CareerConnect.service.AuthService;
import com.bugboo.CareerConnect.type.annotation.ApiMessage;
import com.bugboo.CareerConnect.type.exception.AppException;
import com.bugboo.CareerConnect.utils.JwtUtils;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Value("${jwt.refresh_token.expiration}")
    private Long refreshTokenExpiration;

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, JwtUtils jwtUtils, UserRepository userRepository) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
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

    @PostMapping("/login")
    @ApiMessage("Login successful")
    public ResponseEntity<ResponseLoginDTO> login(@Valid @RequestBody RequestLoginDTO requestLoginDTO){
        Authentication authentication = authService.login(requestLoginDTO);
        // create access token and refresh token
        String accessToken = jwtUtils.generateToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        // save refresh token to database
        User user = jwtUtils.getCurrentUserLogin();
        user.setRefreshToken(refreshToken);
        user = userRepository.save(user);

        // send cookie to client
        ResponseCookie cookie = ResponseCookie.from("refresh_token",refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        // create response dto
        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();
        responseLoginDTO.setAccessToken(accessToken);
        responseLoginDTO.setUser(user);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(responseLoginDTO);
    }

    @ApiMessage("Logout successful")
    @GetMapping("/logout")
    public ResponseEntity<User> logout(){
        User currentUser = jwtUtils.getCurrentUserLogin();
        if (currentUser == null){
            throw new AppException("User already logged out", 400);
        }
        currentUser.setRefreshToken(null);
        userRepository.save(currentUser);
        ResponseCookie cookie = ResponseCookie.from("refresh_token","")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(null);
    }

    @GetMapping("/refresh")
    @ApiMessage("get user by refresh token")
    public ResponseEntity<Object> handleRefreshUser(@CookieValue(value = "refresh_token", defaultValue = "EMPTY") String refreshToken){
        if(refreshToken.equals("EMPTY")){
            throw new AppException("Refresh token is empty",400);
        }
        // get JWT Token From Cookie
        Jwt jwt = jwtUtils.getJwtFromToken(refreshToken);
        String email = jwt.getClaim("sub");
        User currentUser = userRepository.findByEmailAndRefreshToken(email, refreshToken);
        if(currentUser == null){
            throw new AppException("Refresh token is invalid",400);
        }
        // create new refresh token + access token
        String newRefreshToken = jwtUtils.generateRefreshToken(new UsernamePasswordAuthenticationToken(email, currentUser.getPassword()));
        String accessToken = jwtUtils.generateToken(new UsernamePasswordAuthenticationToken(email, currentUser.getPassword()));
        currentUser.setRefreshToken(newRefreshToken);
        currentUser = userRepository.save(currentUser);

        ResponseCookie cookie = ResponseCookie.from("refresh_token",newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
    //   create response dto
        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();
        responseLoginDTO.setAccessToken(accessToken);
        responseLoginDTO.setUser(currentUser);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(responseLoginDTO);
    }

    @PostMapping("/forgot-password")
    @ApiMessage("Send email to reset password successfully. Please check your email")
    public ResponseEntity<User> handleForgotPassword(@RequestBody RequestForgotPasswordDTO requestForgotPasswordDTO) throws MessagingException, NoSuchAlgorithmException {
        authService.forgotPassword(requestForgotPasswordDTO);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/reset-password")
    @ApiMessage("Reset password successfully")
    public ResponseEntity<User> handleResetPassword(@RequestParam String token,@Valid @RequestBody RequestResetPasswordDTO requestResetPasswordDTO) throws NoSuchAlgorithmException {
        authService.resetPassword(token, requestResetPasswordDTO);
        return ResponseEntity.ok().body(null);
    }
}
