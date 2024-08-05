package com.bugboo.CareerConnect.type.exception;

import lombok.Data;

@Data
public class AppException  extends RuntimeException{
    private int statusCode;
    private String status;
    public AppException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.status = Integer.toString(statusCode).startsWith("4") ? "fail" : "error";
    }
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
