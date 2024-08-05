package com.bugboo.CareerConnect.aop;

import com.bugboo.CareerConnect.type.annotation.ApiMessage;
import com.bugboo.CareerConnect.type.apiResponse.SuccessfulApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse httpServletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = httpServletResponse.getStatus();
        if(Integer.toString(status).startsWith("4") || Integer.toString(status).startsWith("5")){
            return body;
        }
        SuccessfulApiResponse apiResponse = new SuccessfulApiResponse();
        apiResponse.setStatus("success");
        String message = Objects.requireNonNull(returnType.getMethod()).getAnnotation(ApiMessage.class).value();
        apiResponse.setMessage(message);
        apiResponse.setData(body);
        return apiResponse;
    }
}