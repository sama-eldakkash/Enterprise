package com.workhub.messaging;

public record ReportJobMessage(
        Long jobId,
        Long tenantId,
        Long projectId
) {
}
