package com.workhub.service;
import com.workhub.tenant.TenantContext;
import com.workhub.entity.User;
import com.workhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ✅ Register
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

        // rollback test
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

    // ✅ Find by email (login)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    // ✅ Login validation (optional clean way)
    public boolean validateUser(String email, String password) {
        User user = findByEmail(email);

        return user != null && user.getPassword().equals(password);
    }
}