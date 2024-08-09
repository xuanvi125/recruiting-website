package com.bugboo.CareerConnect.domain;

import com.bugboo.CareerConnect.domain.keys.SubscriberId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "subscribers")
public class Subscriber {

    @EmbeddedId
    private SubscriberId id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "skill_id", insertable = false, updatable = false)
    private Skill skill;

    Instant createdAt = Instant.now();
}
