package com.nexware.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "nexware")
public record ApplicationProperties(
        Api api,
        Security security,
        Cors cors,
        Reservation reservation,
        Redis redis,
        Kafka kafka,
        Scheduler scheduler
) {

    public record Api(@NotBlank String basePath) {
    }

    public record Security(Jwt jwt) {
    }

    public record Jwt(
            @NotBlank String secret,
            @Positive long accessTokenExpirationMs,
            @Positive long refreshTokenExpirationMs
    ) {
        public Duration accessTokenTtl() {
            return Duration.ofMillis(accessTokenExpirationMs);
        }

        public Duration refreshTokenTtl() {
            return Duration.ofMillis(refreshTokenExpirationMs);
        }
    }

    public record Cors(@NotEmpty List<String> allowedOrigins) {
    }

    public record Reservation(@Positive int defaultExpirationMinutes) {
        public Duration defaultExpiration() {
            return Duration.ofMinutes(defaultExpirationMinutes);
        }
    }

    public record Redis(boolean enabled) {
    }

    public record Kafka(boolean enabled) {
    }

    public record Scheduler(
            boolean reservationExpiryEnabled,
            @NotBlank String reservationExpiryCron
    ) {
    }
}
