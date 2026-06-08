package com.nexware.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * When running behind Nginx without X-Forwarded-Proto, SpringDoc generates http:// server URLs.
 * This filter makes the request appear as HTTPS so Swagger UI calls https:// endpoints.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(name = "nexware.openapi.force-https", havingValue = "true")
class ForceHttpsRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.isSecure() || "https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto"))) {
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(new HttpsRequestWrapper(request), response);
    }

    private static final class HttpsRequestWrapper extends HttpServletRequestWrapper {

        HttpsRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getScheme() {
            return "https";
        }

        @Override
        public boolean isSecure() {
            return true;
        }

        @Override
        public int getServerPort() {
            return 443;
        }
    }
}
