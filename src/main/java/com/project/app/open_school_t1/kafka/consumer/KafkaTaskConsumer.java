package com.project.app.open_school_t1.kafka.consumer;

import com.project.app.open_school_t1.dto.TaskDTO;
import com.project.app.open_school_t1.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTaskConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${spring.kafka.template.default-topic}",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(List<TaskDTO> messageList, Acknowledgment ack) {
        log.info("Received {} Task Updates", messageList.size());
        try {
            messageList.forEach(taskDto -> {
                log.info("Processing Task Update: ID = {}, Status = {}", taskDto.getId(), taskDto.getStatus());
                notificationService.sendMessage(taskDto);
            });
            ack.acknowledge();
            log.info("Successfully processed and acknowledged {} messages.", messageList.size());

        } catch (Exception e) {
            log.error("Error processing batch of messages, skipping acknowledgment. Cause: {}", e.getMessage(), e);
        }
    }
}