package com.project.app.open_school_t1.service;

import com.project.app.open_school_t1.dto.TaskDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private JavaMailSender mailSender;

    @Mock private MailProperties mailProperties;

    @InjectMocks private NotificationService notificationService;

    private TaskDTO taskStatusMessage;

    @BeforeEach
    void setUp() {
        when(mailProperties.getUsername()).thenReturn("test@example.com");

        ReflectionTestUtils.setField(notificationService, "recipient", "recipient@example.com");

        taskStatusMessage = TaskDTO.builder().id(1L).build();
    }

    @Test
    @DisplayName("Отправка текстового уведомления об изменении статуса задачи")
    void sendSimpleNotification_ShouldSendEmailWithCorrectContent() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        notificationService.sendMessage(taskStatusMessage);

        ArgumentCaptor<SimpleMailMessage> emailCaptor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(emailCaptor.capture());

        SimpleMailMessage sentMessage = emailCaptor.getValue();
        assertEquals("test@example.com", sentMessage.getFrom());
        assertEquals("recipient@example.com", Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals("Изменение статуса задачи #1", sentMessage.getSubject());
        assertTrue(
                Objects.requireNonNull(sentMessage.getText())
                        .contains("Статус задачи с id 1 изменился на: PROCESSING"));
    }

    @Test
    @DisplayName("Обработка исключений при отправке уведомления")
    void sendSimpleNotification_ShouldHandleExceptions() {
        doThrow(new RuntimeException("Mail server connection failed"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertDoesNotThrow(
                () -> {
                    notificationService.sendMessage(taskStatusMessage);
                },
                "Метод должен корректно обрабатывать исключения");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}