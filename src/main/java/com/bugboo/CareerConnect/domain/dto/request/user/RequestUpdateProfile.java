package com.bugboo.CareerConnect.domain.dto.request.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RequestUpdateProfile {
    private String name;
    private MultipartFile avatar;
}
