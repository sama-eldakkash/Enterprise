package com.workhub.messaging;

import com.workhub.entity.ReportJob;
import com.workhub.repository.ReportJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.workhub.config.RabbitConfig;
import org.springframework.stereotype.Component;

@Component
public class ReportConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReportConsumer.class);

    private final ReportJobRepository repo;

    public ReportConsumer(ReportJobRepository repo) {
        this.repo = repo;
    }

    @RabbitListener(queues = RabbitConfig.REPORT_QUEUE)
    public void handle(ReportJobMessage message) throws InterruptedException {
        try {
            process(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Report job interrupted jobId={} tenantId={}", message.jobId(), message.tenantId());
            throw e;
        } catch (Exception e) {
            log.error("Report job processing failed jobId={} tenantId={} projectId={}",
                    message.jobId(), message.tenantId(), message.projectId(), e);
            throw e;
        }
    }

    private void process(ReportJobMessage message) throws InterruptedException {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }

        var jobOpt = repo.findByIdAndTenantId(message.jobId(), message.tenantId());
        if (jobOpt.isEmpty()) {
            log.warn("Ignoring report message: no job for jobId={} tenantId={} (wrong tenant or missing row)",
                    message.jobId(), message.tenantId());
            return;
        }

        ReportJob job = jobOpt.get();
        job.setStatus("DONE");
        repo.save(job);
        log.info("Report finished for jobId={} tenantId={}", message.jobId(), message.tenantId());
    }
}
