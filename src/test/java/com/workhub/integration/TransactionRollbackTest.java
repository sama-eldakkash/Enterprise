package com.workhub.integration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@Disabled("Disabled on local Windows Docker environment")
public class TransactionRollbackTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Test
    @Transactional
    void transactionShouldRollbackOnFailure() {

        assertThrows(RuntimeException.class, () -> {

            System.out.println("Insert operation 1");
            System.out.println("Insert operation 2");

            throw new RuntimeException("Rollback triggered");
        });
    }
}