package com.workhub.dto;

public record UserInfoDto(Long id, String email, String name, String role, Long tenantId) {
}
