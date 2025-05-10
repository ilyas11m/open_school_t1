package com.project.app.open_school_t1.kafka.serializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MessageDeserializer<T> extends JsonDeserializer<T> {

    @Override
    public T deserialize(String topic, Headers headers, ByteBuffer data) {
        try {
            return super.deserialize(topic, headers, data);
        } catch (Exception e) {
            log.warn("Exception raised during deserialization message {}", new String(data.array(), StandardCharsets.UTF_8), e);
            return null;
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return super.deserialize(topic, data);
        } catch (Exception e) {
            log.warn("Exception raised during deserialization message {}", new String(data, StandardCharsets.UTF_8), e);
            return null;
        }
    }
}