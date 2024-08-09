package com.bugboo.CareerConnect.domain.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class SubscriberId implements Serializable {
    @Column(name = "user_id")
    private int userId;
    @Column(name = "skill_id")
    private int skillId ;

    public SubscriberId() {

    }
    public SubscriberId(int userId, int skillId) {
        this.userId = userId;
        this.skillId = skillId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }
}
