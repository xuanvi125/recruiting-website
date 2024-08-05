package com.bugboo.CareerConnect.type.apiResponse;

import lombok.Data;

@Data
public class SuccessfulApiResponse {
    private String status;
    private String message;
    Object data;
}
