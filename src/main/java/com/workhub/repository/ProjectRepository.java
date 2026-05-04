package com.workhub.repository;

import com.workhub.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByTenantId(Long tenantId);

    Optional<Project> findByIdAndTenantId(Long id, Long tenantId);
}