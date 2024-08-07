package com.bugboo.CareerConnect.utils;

import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.repository.UserRepository;
import com.bugboo.CareerConnect.type.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import static com.bugboo.CareerConnect.config.JwtSecurityConfig.JWT_ALGORITHM;

@Service
public class JwtUtils {
    @Value("${jwt.access_token.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh_token.expiration}")
    private Long refreshTokenExpiration;

    @Value("${jwt.secret.key}")
    private String jwtKey;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    @Autowired
    public JwtUtils(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
    }


    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant validity = now.plus(accessTokenExpiration, ChronoUnit.SECONDS);

        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("authorities", authorities.isEmpty() ? "ROLE_USER" : authorities)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,
                claims)).getTokenValue();
    }

    public String generateRefreshToken(Authentication authentication){
        Instant now = Instant.now();
        Instant validity = now.plus(refreshTokenExpiration, ChronoUnit.SECONDS);

        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("authorities", authorities)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,
                claims)).getTokenValue();

    }

    public User getCurrentUserLogin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }
    public Jwt getJwtFromToken(String refreshToken){
        try{
            return this.jwtDecoder.decode(refreshToken);
        }
        catch (Exception e){
            throw new AppException("Invalid Refresh Token", 400);
        }

    }
}
