package com.nexware.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexware.shared.api.ApiError;
import com.nexware.shared.api.ApiResponse;
import com.nexware.shared.api.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ApiError error = ApiError.of(
                ErrorCode.AUTHENTICATION_FAILED,
                "Authentication is required to access this resource",
                request.getRequestURI(),
                request.getHeader("X-Request-Id")
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), ApiResponse.failure(error));
    }
}
