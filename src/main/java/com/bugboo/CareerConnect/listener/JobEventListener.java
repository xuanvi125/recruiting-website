package com.bugboo.CareerConnect.listener;

import com.bugboo.CareerConnect.event.JobCreatedEvent;
import com.bugboo.CareerConnect.service.NotificationService;
import com.bugboo.CareerConnect.service.SendEmailService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class JobEventListener {
    private final NotificationService notificationService;


    public JobEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async
    @EventListener
    public void handleJobCreatedEvent(JobCreatedEvent jobCreatedEvent) {
        notificationService.sendJobNotification(jobCreatedEvent.getJob());
    }
}
