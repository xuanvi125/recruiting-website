package com.bugboo.CareerConnect.domain;

import com.bugboo.CareerConnect.type.constant.ConstantUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String email;

    @JsonIgnore
    private String password;
    private String avatar = ConstantUtils.DEFAULT_USER_AVATAR;

    @Column(name = "refresh_token", columnDefinition = "MEDIUMTEXT")
    @JsonIgnore
    private String refreshToken;


    private boolean active = false;

    @Column(name = "google_id")
    private String googleId;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Resume> resumes;

}
