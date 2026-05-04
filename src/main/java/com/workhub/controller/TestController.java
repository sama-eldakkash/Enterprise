package com.workhub.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','TENANT_USER')")
    public String test() {
        return "App is working ";
    }
}