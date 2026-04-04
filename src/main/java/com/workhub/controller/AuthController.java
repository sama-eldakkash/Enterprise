package com.workhub.controller;

import com.workhub.config.JwtUtil;
import com.workhub.dto.*;
import com.workhub.entity.User;
import com.workhub.repository.TenantRepository;
import com.workhub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TenantRepository tenantRepository;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody @Valid RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        userService.register(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("User registered"));
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
        User existingUser = userService.findByEmail(request.getEmail());

        if (existingUser == null || !existingUser.getPassword().equals(request.getPassword())) {
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
    public ResponseEntity<?> me(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
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
}
