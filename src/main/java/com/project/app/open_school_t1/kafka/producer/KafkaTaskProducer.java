package com.project.app.open_school_t1.kafka.producer;

import com.project.app.open_school_t1.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTaskProducer {

    private final KafkaTemplate kafkaTemplate;

    @Value("${spring.kafka.template.default-topic}")
    private String taskTopic;

    public void send(TaskDTO taskDTO) {
        try {
            log.info("Message 'Task-Status' sent to Topic {}", taskDTO);
            kafkaTemplate.send(taskTopic, taskDTO.getId().toString(), taskDTO);
        } catch (Exception e) {
            log.error("Exception raised in {}", e.getMessage());
        }
    }

}
