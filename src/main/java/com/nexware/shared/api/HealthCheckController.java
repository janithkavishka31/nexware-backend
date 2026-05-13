package com.nexware.shared.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${nexware.api.base-path}/health")
@RequiredArgsConstructor
@Tag(name = "health-check", description = "Verify backend is running — try this first in Swagger")
public class HealthCheckController {

    private final HealthEndpoint healthEndpoint;

    @GetMapping
    @Operation(
            summary = "Health check",
            description = "Public endpoint. Same data as GET /actuator/health — expect status UP when backend and database are healthy."
    )
    public ResponseEntity<HealthComponent> health() {
        return ResponseEntity.ok(healthEndpoint.health());
    }
}
