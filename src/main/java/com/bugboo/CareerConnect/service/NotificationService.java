package com.bugboo.CareerConnect.service;

import com.bugboo.CareerConnect.domain.Job;
import com.bugboo.CareerConnect.domain.Subscriber;
import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.repository.SubscriberRepository;
import com.bugboo.CareerConnect.type.exception.AppException;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final SendEmailService sendEmailService;
    private final SubscriberRepository subscriberRepository;

    public NotificationService(SendEmailService sendEmailService, SubscriberRepository subscriberRepository) {
        this.sendEmailService = sendEmailService;
        this.subscriberRepository = subscriberRepository;
    }

    @Async
    public void sendJobNotification(Job job){
        List<User> subscribers = subscriberRepository.findDistinctSubscriptionsBySkills((job.getSkills()));
        subscribers.forEach(subscriber -> {
            try {
                sendEmailService.sendEmailJobNotification(subscriber, job);
            } catch (MessagingException e) {
                throw new AppException("Failed to send email", 500);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
