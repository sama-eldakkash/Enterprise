package com.workhub.dto;

public record AuthMeResponse(UserInfoDto user, TenantInfoDto tenant) {
}
