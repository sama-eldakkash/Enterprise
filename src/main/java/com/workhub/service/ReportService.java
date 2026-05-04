package com.workhub.service;

import com.workhub.entity.Project;
import com.workhub.entity.ReportJob;
import com.workhub.config.RabbitConfig;
import com.workhub.messaging.ReportJobMessage;
import com.workhub.repository.ProjectRepository;
import com.workhub.repository.ReportJobRepository;
import com.workhub.tenant.TenantContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final ReportJobRepository repo;
    private final ProjectRepository projectRepository;
    private final RabbitTemplate rabbitTemplate;

    public ReportService(
            ReportJobRepository repo,
            ProjectRepository projectRepository,
            RabbitTemplate rabbitTemplate
    ) {
        this.repo = repo;
        this.projectRepository = projectRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public ReportJob createJob(Long projectId) {
        Long tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is required");
        }

        Project project = projectRepository.findByIdAndTenantId(projectId, tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ReportJob job = new ReportJob();
        job.setTenantId(tenantId);
        job.setProjectId(project.getId());
        job.setStatus("PENDING");

        job = repo.save(job);

        ReportJobMessage message =
                new ReportJobMessage(job.getId(), tenantId, project.getId());

        rabbitTemplate.convertAndSend(
                RabbitConfig.REPORT_QUEUE,
                message
        );

        return job;
    }

    public List<ReportJob> getJobs() {
        Long tenantId = TenantContext.getTenantId();
        return repo.findByTenantId(tenantId);
    }
}
