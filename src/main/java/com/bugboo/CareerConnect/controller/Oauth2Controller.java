package com.bugboo.CareerConnect.controller;

import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.repository.RoleRepository;
import com.bugboo.CareerConnect.repository.UserRepository;
import com.bugboo.CareerConnect.type.constant.ConstantUtils;
import com.bugboo.CareerConnect.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Oauth2Controller {
    @Value("${jwt.refresh_token.expiration}")
    private Long refreshTokenExpiration;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;

    public Oauth2Controller(UserRepository userRepository, RoleRepository roleRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/public/login/success")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) {
        String name = principal.getAttribute("name");
        String email = principal.getAttribute("email");
        String avatar = principal.getAttribute("picture");
        String googleId = principal.getAttribute("sub");
        User user = userRepository.findByEmailOrGoogleId(email, googleId);
        if(user!= null && !user.isActive()){
            return "redirect:" + ConstantUtils.CLIENT_URL + "/login?error=Account is banned, contact admin for more information";
        }

        if(user == null){
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setAvatar(avatar);
            user.setRole(roleRepository.findByName("ROLE_USER").orElse(null));
            user.setGoogleId(googleId);
            user.setActive(true);
            user = userRepository.save(user);
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null);

        String accessToken = jwtUtils.generateToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);



        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpiration.intValue());
        response.addCookie(cookie);
        String redirectUrl = ConstantUtils.CLIENT_URL + "/login?token=" + accessToken;
        return "redirect:" + redirectUrl;
    }
}
