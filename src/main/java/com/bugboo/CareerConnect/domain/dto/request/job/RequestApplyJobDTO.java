package com.bugboo.CareerConnect.domain.dto.request.job;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestApplyJobDTO {
    @NotNull(message = "jobId is required")
    private int jobId;
    private String resumeUrl;
}
