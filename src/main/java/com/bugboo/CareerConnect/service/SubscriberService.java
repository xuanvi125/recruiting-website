package com.bugboo.CareerConnect.service;

import com.bugboo.CareerConnect.domain.Skill;
import com.bugboo.CareerConnect.domain.Subscriber;
import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.domain.dto.request.subscriber.RequestSubscribeSkillDTO;
import com.bugboo.CareerConnect.domain.keys.SubscriberId;
import com.bugboo.CareerConnect.repository.SkillRepository;
import com.bugboo.CareerConnect.repository.SubscriberRepository;
import com.bugboo.CareerConnect.type.exception.AppException;
import com.bugboo.CareerConnect.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JwtUtils jwtUtils;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository, JwtUtils jwtUtils) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jwtUtils = jwtUtils;
    }

    public List<Subscriber> createSubscriber(RequestSubscribeSkillDTO requestSubscribeSkillDTO) {
        User user = jwtUtils.getCurrentUserLogin();

        // check skill already subscribed
        List<Subscriber> subscribersDB = subscriberRepository.findByUserAndSkillIdIn(user, requestSubscribeSkillDTO.getSkillIds());
        if(!subscribersDB.isEmpty()){
            String skillNames = subscribersDB.stream().map(subscriber -> {
                return subscriber.getSkill().getName();
            }).reduce((s1, s2) -> s1 + ", " + s2).get();
            throw new AppException("You have already subscribed to " + skillNames, 400);
        }

        // get valid skills
        List<Skill> skills = skillRepository.findByIdIn(requestSubscribeSkillDTO.getSkillIds());
        if(skills.isEmpty())
            throw new AppException("Invalid Skill Ids", 400);


        List<Subscriber> subscribers = new ArrayList<>();
        skills.forEach(skill -> {
            SubscriberId subscriberId = new SubscriberId(user.getId(), skill.getId());
            Subscriber subscriber = new Subscriber();
            subscriber.setId(subscriberId);
            subscriber.setUser(user);
            subscriber.setSkill(skill);
            subscriber = subscriberRepository.save(subscriber);
            subscribers.add(subscriber);
        });
        return subscribers;
    }
}
