package com.workhub.integration;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class RabbitMQIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void rabbitMQShouldSendMessage() {

        assertDoesNotThrow(() -> {
            rabbitTemplate.convertAndSend(
                    "testQueue",
                    "Hello RabbitMQ"
            );
        });
    }
}