package com.bugboo.CareerConnect.domain.dto.request.job;

import com.bugboo.CareerConnect.type.constant.EnumLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestCreateJobDTO {

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

    @NotNull(message = "Skills are required")
    private int[] skillIds;
}
