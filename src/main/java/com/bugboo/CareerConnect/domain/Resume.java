package com.bugboo.CareerConnect.domain;

import com.bugboo.CareerConnect.type.constant.ResumeStatus;
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
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    Instant createdAt = Instant.now();
}
