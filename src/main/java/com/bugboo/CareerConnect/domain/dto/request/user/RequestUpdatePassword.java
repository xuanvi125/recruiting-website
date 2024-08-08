package com.bugboo.CareerConnect.domain.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RequestUpdatePassword {

    @NotNull(message = "Current password is required")
    private String currentPassword;

    @NotNull(message = "Password is required")
    @Length(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Confirm password is required")
    private String confirmPassword;

    public boolean isMatchPassword() {
        return password.equals(confirmPassword);
    }
}
