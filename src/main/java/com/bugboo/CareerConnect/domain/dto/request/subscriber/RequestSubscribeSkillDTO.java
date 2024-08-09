package com.bugboo.CareerConnect.domain.dto.request.subscriber;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestSubscribeSkillDTO {
    @NotNull(message = "Skill id is required")
    private int[] skillIds;
}
