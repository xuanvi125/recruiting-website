package com.bugboo.CareerConnect.controller;

import com.bugboo.CareerConnect.domain.Subscriber;
import com.bugboo.CareerConnect.domain.dto.request.subscriber.RequestSubscribeSkillDTO;
import com.bugboo.CareerConnect.service.SubscriberService;
import com.bugboo.CareerConnect.type.annotation.ApiMessage;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscribers")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping
    @ApiMessage("Subscriber created successfully")
    public ResponseEntity<List<Subscriber>> createSubscriber(@Valid @RequestBody RequestSubscribeSkillDTO requestSubscribeSkillDTO) {
        return ResponseEntity.ok(subscriberService.createSubscriber(requestSubscribeSkillDTO));
    }
}
