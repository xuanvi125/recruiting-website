package com.bugboo.CareerConnect.aop;

import com.bugboo.CareerConnect.type.apiResponse.ErrorApiResponse;
import com.bugboo.CareerConnect.type.exception.AppException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(AppException.class)
    ResponseEntity<ErrorApiResponse> handleAppException(AppException e){
        ErrorApiResponse response = new ErrorApiResponse();
        response.setStatus(e.getStatus());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorApiResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException e){
        ErrorApiResponse response = new ErrorApiResponse();
        response.setStatus("fail");
        response.setMessage("invalid input, please check the input type");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApiResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        ErrorApiResponse response = new ErrorApiResponse();
        response.setStatus("fail");
        response.setMessage(String.join(". ", errors));
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorApiResponse> handleAccessDeniedException(AccessDeniedException e){
        ErrorApiResponse response = new ErrorApiResponse();
        response.setStatus("fail");
        response.setMessage("You do not have permission to access this resource");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ErrorApiResponse> handleBadCredentialsException(BadCredentialsException e){
        ErrorApiResponse response = new ErrorApiResponse();
        response.setStatus("fail");
        response.setMessage("invalid email or password");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    ResponseEntity<ErrorApiResponse> handleNoHandlerFoundException(NoHandlerFoundException e){
        ErrorApiResponse response = new ErrorApiResponse();
        response.setStatus("fail");
        response.setMessage("invalid endpoint");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorApiResponse> handleException(Exception e){
        System.out.println(">>>>>>>>" + e);
        ErrorApiResponse response = new ErrorApiResponse();
        response.setStatus("error");
        response.setMessage("internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
