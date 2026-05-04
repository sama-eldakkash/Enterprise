package com.workhub.service;

import com.workhub.entity.User;
import com.workhub.repository.UserRepository;
import com.workhub.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void register(User user) {

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("X-Tenant-ID header is required for registration");
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("TENANT_USER");
        }

        user.setTenantId(tenantId);

        userRepository.save(user);

        if (user.getEmail().contains("fail")) {
            throw new RuntimeException("Forcing rollback!");
        }
    }

    @Transactional
    public void registerWithFailure(User user) {

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is required (JWT or X-Tenant-ID)");
        }
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("TENANT_USER");
        }
        user.setTenantId(tenantId);

        userRepository.save(user);

        throw new RuntimeException("Forcing rollback");
    }

    public Optional<User> findByEmailAndTenantId(String email, Long tenantId) {
        if (tenantId == null || email == null) {
            return Optional.empty();
        }
        return userRepository.findByEmailAndTenantId(email, tenantId);
    }

    public boolean validateUser(String email, String password) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return false;
        }
        return findByEmailAndTenantId(email, tenantId)
                .map(u -> u.getPassword().equals(password))
                .orElse(false);
    }
}
