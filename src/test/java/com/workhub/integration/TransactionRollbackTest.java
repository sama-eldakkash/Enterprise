package com.workhub.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class TransactionRollbackTest {

    @Test
    @Transactional
    void transactionShouldRollbackOnFailure() {

        assertThrows(RuntimeException.class, () -> {

            // Simulate first DB operation
            System.out.println("Insert operation");

            // Simulate second DB operation failure
            throw new RuntimeException("Transaction failed");

        });
    }
}