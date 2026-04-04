package com.workhub.tenant;

public class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    // ✅ set tenant
    public static void setTenantId(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    // ✅ get tenant
    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }

    // ✅ clear بعد كل request
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}