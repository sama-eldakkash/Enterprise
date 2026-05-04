package com.workhub.controller;

import com.workhub.config.JwtUtil;
import com.workhub.dto.*;
import com.workhub.entity.User;
import com.workhub.repository.TenantRepository;
import com.workhub.repository.UserRepository;
import com.workhub.service.UserService;
import com.workhub.tenant.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody @Valid RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        userService.register(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("User registered"));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(
            @RequestBody @Valid RegisterRequest request,
            @RequestHeader("X-Tenant-ID") Long tenantId) {

        if (!tenantRepository.existsById(tenantId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiError.of(400, "Bad Request", "Unknown tenant id"));
        }

        if (userService.findByEmailAndTenantId(request.getEmail(), tenantId).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiError.of(409, "Conflict", "User already exists for this tenant"));
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("TENANT_ADMIN");
        user.setTenantId(tenantId);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Admin registered successfully"));
    }

    /**
     * Admin-only demo: transactional rollback (requires TENANT_ADMIN JWT).
     */
    @PostMapping("/register-fail")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<MessageResponse> registerFail(@RequestBody @Valid RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        userService.registerWithFailure(user);

        return ResponseEntity.ok(new MessageResponse("Unexpected success"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("X-Tenant-ID header is required for login");
        }

        User existingUser = userService.findByEmailAndTenantId(request.getEmail(), tenantId)
                .orElse(null);

        if (existingUser == null || !passwordMatches(request.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiError.of(401, "Unauthorized", "Invalid email or password"));
        }

        String role = existingUser.getRole() != null && !existingUser.getRole().isBlank()
                ? existingUser.getRole()
                : "TENANT_USER";
        String token = jwtUtil.generateToken(existingUser.getEmail(), existingUser.getTenantId(), role);
        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','TENANT_USER')")
    public ResponseEntity<?> me(Authentication authentication) {
        String email = authentication.getName();
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiError.of(400, "Bad Request", "Tenant context is required"));
        }
        User user = userService.findByEmailAndTenantId(email, tenantId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiError.of(404, "Not Found", "User not found"));
        }

        return tenantRepository.findById(user.getTenantId())
                .map(tenant -> {
                    UserInfoDto userDto = new UserInfoDto(
                            user.getId(),
                            user.getEmail(),
                            user.getName(),
                            user.getRole(),
                            user.getTenantId()
                    );
                    TenantInfoDto tenantDto = new TenantInfoDto(tenant.getId(), tenant.getName(), tenant.getPlan());
                    return ResponseEntity.<Object>ok(new AuthMeResponse(userDto, tenantDto));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiError.of(404, "Not Found", "Tenant not found for user")));
    }

    private boolean passwordMatches(String raw, String stored) {
        if (stored == null || raw == null) {
            return false;
        }
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            return passwordEncoder.matches(raw, stored);
        }
        return raw.equals(stored);
    }
}
