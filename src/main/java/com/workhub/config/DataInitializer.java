package com.workhub.config;

import com.workhub.entity.Tenant;
import com.workhub.entity.User;
import com.workhub.repository.TenantRepository;
import com.workhub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(TenantRepository tenantRepository, UserRepository userRepository) {
        return args -> {
            if (tenantRepository.count() > 0) {
                return;
            }
            Tenant tenant = new Tenant();
            tenant.setName("Acme Corp");
            tenant.setPlan("STANDARD");
            tenantRepository.save(tenant);

            User user = new User();
            user.setName("Admin");
            user.setEmail("admin@test.com");
            user.setPassword("123456");
            user.setRole("TENANT_ADMIN");
            user.setTenantId(tenant.getId());
            userRepository.save(user);
        };
    }
}
