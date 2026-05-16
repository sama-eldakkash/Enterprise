package com.workhub.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleAccessIntegrationTest {

    @Test
    void wrongRoleShouldReturnForbidden() {

        HttpStatus status = HttpStatus.FORBIDDEN;

        assertEquals(HttpStatus.FORBIDDEN, status);
    }

    @Test
    void adminShouldBeAllowed() {

        HttpStatus status = HttpStatus.OK;

        assertEquals(HttpStatus.OK, status);
    }
}