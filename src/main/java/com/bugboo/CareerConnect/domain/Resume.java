package com.bugboo.CareerConnect.domain;

import com.bugboo.CareerConnect.type.constant.ResumeStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeStatus status = ResumeStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"avatar","active","googleId", "role", "company"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    @JsonIgnoreProperties({"description", "salary", "quantity", "startDate","skills"})
    private Job job;

    Instant createdAt = Instant.now();
}
