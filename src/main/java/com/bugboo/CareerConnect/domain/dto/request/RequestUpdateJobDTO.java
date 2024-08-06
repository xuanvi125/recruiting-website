package com.bugboo.CareerConnect.domain.dto.request;

import com.bugboo.CareerConnect.type.constant.EnumLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestUpdateJobDTO {
    @NotNull(message = "Id is required")
    private int id;
    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Location is required")
    private String location;

    private int salary;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private EnumLevel level;

    private String description;
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate startDate;

    @NotNull(message = "Company is required")
    private int companyId;

    @NotNull(message = "Skills are required")
    private int[] skillIds;
}
