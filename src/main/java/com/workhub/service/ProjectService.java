package com.workhub.service;

import com.workhub.tenant.TenantContext;
import com.workhub.entity.Project;
import com.workhub.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(Project project) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is required (JWT or X-Tenant-ID)");
        }
        project.setTenantId(tenantId);
        return projectRepository.save(project);
    }

    public List<Project> getProjects() {
        Long tenantId = TenantContext.getTenantId();
        return projectRepository.findAllByTenantId(tenantId);
    }

    public Project getProject(Long id) {
        Long tenantId = TenantContext.getTenantId();
        return projectRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
}