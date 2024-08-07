package com.bugboo.CareerConnect.domain;

import com.bugboo.CareerConnect.type.constant.EnumMethod;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String apiPath;

    @Enumerated(EnumType.STRING)
    private EnumMethod method;
    private String module;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

}
