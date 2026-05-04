package com.workhub.repository;

import com.workhub.entity.ReportJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportJobRepository extends JpaRepository<ReportJob, Long> {

    List<ReportJob> findByTenantId(Long tenantId);

    Optional<ReportJob> findByIdAndTenantId(Long id, Long tenantId);
}
