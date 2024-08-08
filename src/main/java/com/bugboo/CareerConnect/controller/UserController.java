package com.bugboo.CareerConnect.controller;

import com.bugboo.CareerConnect.domain.Resume;
import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.domain.dto.request.user.RequestUpdatePassword;
import com.bugboo.CareerConnect.domain.dto.request.user.RequestUpdateProfile;
import com.bugboo.CareerConnect.domain.dto.response.ResponseLoginDTO;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.service.UserService;
import com.bugboo.CareerConnect.type.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @ApiMessage("Get current user successfully")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/resumes")
    @ApiMessage("Get resumes successfully")
    public ResponseEntity<ResponsePagingResultDTO> getResumesByUser(@Filter Specification<Resume> specification, Pageable pageable) {
        return ResponseEntity.ok(userService.getResumesByUser(specification,pageable));
    }

    @PostMapping("/update-password")
    @ApiMessage("Update password successfully")
    public ResponseEntity<ResponseLoginDTO> updatePassword(@Valid @RequestBody RequestUpdatePassword requestUpdatePassword) {
        return ResponseEntity.ok(userService.updatePassword(requestUpdatePassword));
    }

    @PostMapping("/update-profile")
    @ApiMessage("Update profile successfully")
    public ResponseEntity<User> updateProfile(@Valid RequestUpdateProfile requestUpdateProfile, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        requestUpdateProfile.setAvatar(file);
        return ResponseEntity.ok(userService.updateProfile(requestUpdateProfile));
    }

}
