package com.bugboo.CareerConnect.service;

import com.bugboo.CareerConnect.domain.Job;
import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.type.constant.ConstantUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.Map;


@Service
public class SendEmailService {
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    @Autowired
    public SendEmailService(JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmailWithTemplate(String to, String subject, String htmlBody) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(new InternetAddress(ConstantUtils.EMAIL_FROM, ConstantUtils.EMAIL_FROM_NAME));
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }

    @Async
    public void sendEmailVerifyAccount(User user, String token) throws MessagingException, UnsupportedEncodingException {
        String url = ConstantUtils.SERVER_URL + "/api/v1/auth/verify-account?token=" + token;
        Context context = new Context();
        context.setVariable("name",user.getName());
        context.setVariable("url", url);
        String htmlBody = templateEngine.process("email-verification-template", context);
        sendEmailWithTemplate(user.getEmail(), "Verify Your Account", htmlBody);
    }

    @Async
    public void sendEmailForgotPassword(User user, String url) throws MessagingException, UnsupportedEncodingException {
        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("url", url);
        String htmlBody = templateEngine.process("forgot-password-email-template", context);
        sendEmailWithTemplate(user.getEmail(), "Reset Password", htmlBody);
    }

    @Async
    public void sendEmailJobNotification(User user, Job job) throws MessagingException, UnsupportedEncodingException {
        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("job", job);
        String htmlBody = templateEngine.process("job-notification-template", context);
        sendEmailWithTemplate(user.getEmail(), "Job Notification", htmlBody);
    }
}
