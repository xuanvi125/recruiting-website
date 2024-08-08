package com.bugboo.CareerConnect.domain.dto.request.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RequestResetPasswordDTO {
    @NotNull(message = "Password is required")
    @Length(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Confirm password is required")
    @Length(min = 6, message = "Password must be at least 6 characters")
    private String confirmPassword;
    public boolean isPasswordMatch(){
        return password.equals(confirmPassword);
    }
}
