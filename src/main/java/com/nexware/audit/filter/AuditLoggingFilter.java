package com.nexware.audit.filter;

import com.nexware.audit.service.AuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AuditLoggingFilter extends OncePerRequestFilter {

    private final AuditService auditService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (request.getRequestURI().startsWith("/api/v1") && !request.getRequestURI().startsWith("/api/v1/audit")) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String actor = authentication == null || !authentication.isAuthenticated() ? null : authentication.getName();
                auditService.recordRequest(actor, request.getMethod(), request.getRequestURI(), request.getHeader("X-Request-Id"), request.getRemoteAddr(), request.getHeader("User-Agent"));
            }
        }
    }
}
