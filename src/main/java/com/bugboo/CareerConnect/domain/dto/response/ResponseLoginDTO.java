package com.bugboo.CareerConnect.domain.dto.response;

import com.bugboo.CareerConnect.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private User user;
}
