package com.bugboo.CareerConnect.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestLoginDTO {
    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Password is required")
    private String password;
}
