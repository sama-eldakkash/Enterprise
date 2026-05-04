package com.workhub.tenant;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String tenant = req.getHeader("X-Tenant-ID");

        try {
            // ✅ لو فيه tenant header
            if (tenant != null && !tenant.isEmpty()) {
                TenantContext.setTenantId(Long.parseLong(tenant));
            }

            // ✅ كمل request
            chain.doFilter(request, response);

        } finally {
            // ✅ تنظيف بعد كل request
            TenantContext.clear();
        }
    }
}