package com.workhub.controller;

import com.workhub.entity.ReportJob;
import com.workhub.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @PostMapping("/{projectId}")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','TENANT_USER')")
    public ReportJob create(@PathVariable Long projectId) {
        return service.createJob(projectId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','TENANT_USER')")
    public List<ReportJob> list() {
        log.info("Fetching reports");
        return service.getJobs();
    }
}
