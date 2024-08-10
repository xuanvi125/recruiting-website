package com.bugboo.CareerConnect.domain.dto.request.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestAdminUpdateUserDTO {
    @NotNull(message = "User id is required")
    private int id;
    private int companyId;
    private int roleId;
}
