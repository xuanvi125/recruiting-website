package com.bugboo.CareerConnect.domain.dto.request.resume;

import com.bugboo.CareerConnect.type.constant.ResumeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestUpdateResume {
    private int id;
    @NotNull(message = "Status is required")
    private ResumeStatus status;
}
