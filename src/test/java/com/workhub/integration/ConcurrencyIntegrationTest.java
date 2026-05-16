package com.workhub.integration;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrencyIntegrationTest {

    @Test
    void concurrentExecutionShouldSucceed() throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                System.out.println(Thread.currentThread().getName() + " executed");
            });
        }

        executorService.shutdown();

        boolean finished =
                executorService.awaitTermination(5, TimeUnit.SECONDS);

        assertTrue(finished);
    }
}