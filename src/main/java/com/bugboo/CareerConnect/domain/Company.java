package com.bugboo.CareerConnect.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
@Table(name = "companies")
@Data
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Company name is required")
    @Length(min = 3, max = 100, message = "Company name must be between 3 and 100 characters")
    private String name;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    private String logo;

    @NotNull(message = "Company address is required")
    private String address;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private List<Job> jobs;



}
