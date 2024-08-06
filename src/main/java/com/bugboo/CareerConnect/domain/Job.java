package com.bugboo.CareerConnect.domain;

import com.bugboo.CareerConnect.type.constant.EnumLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnoreProperties({"jobs"})
    private Company company;

    @ManyToMany
    @JoinTable(
            name = "job_skill",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills;

}
