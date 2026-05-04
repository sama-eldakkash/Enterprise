package com.workhub.controller;

import com.workhub.entity.Project;
import com.workhub.service.ProjectService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','TENANT_USER')")
    public List<Project> getProjects() {
        return projectService.getProjects();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','TENANT_USER')")
    public Project getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }
}