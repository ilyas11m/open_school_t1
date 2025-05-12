package com.project.app.open_school_t1.service;

import com.project.app.open_school_t1.dto.TaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    private final MailProperties properties;

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender, MailProperties properties) {
        this.properties = properties;
        this.mailSender = mailSender;
    }

    public void sendMessage(TaskDTO task) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(properties.getUsername());
        simpleMailMessage.setTo(properties.getUsername());
        simpleMailMessage.setSubject("Task's status was updated!");
        simpleMailMessage.setText("Task with ID: " + task.getId() + " got new status: " + task.getStatus());
        try {
            mailSender.send(simpleMailMessage);
            log.info("Mail message was sent to {}, with content: {}", properties.getUsername(), simpleMailMessage.getText());
        } catch (Exception e) {
            log.error("Exception raised during execution 'sendMessage' method {}", e.getMessage());
        }
    }
}
