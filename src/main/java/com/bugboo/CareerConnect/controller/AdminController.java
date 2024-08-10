package com.bugboo.CareerConnect.controller;

import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.domain.dto.request.admin.RequestAdminUpdateUserDTO;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.service.UserService;
import com.bugboo.CareerConnect.type.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admins")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @ApiMessage("Get users successfully")
    public ResponseEntity<ResponsePagingResultDTO> getUsers(@Filter Specification<User> specification, Pageable pageable) {
        return ResponseEntity.ok(userService.getUsers(specification,pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update user successfully")
    public ResponseEntity<User> updateUser(@Valid @RequestBody RequestAdminUpdateUserDTO requestAdminUpdateUserDTO) {
        return ResponseEntity.ok(userService.updateUser(requestAdminUpdateUserDTO));
    }
}
