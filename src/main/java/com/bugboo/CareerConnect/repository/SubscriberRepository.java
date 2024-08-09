package com.bugboo.CareerConnect.repository;

import com.bugboo.CareerConnect.domain.Subscriber;
import com.bugboo.CareerConnect.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Integer> {
    List<Subscriber> findByUserAndSkillIdIn(User user, int[] skillIds);
}
