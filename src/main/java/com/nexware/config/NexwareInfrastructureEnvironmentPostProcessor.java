package com.nexware.config;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Disables Redis/Kafka Spring Boot auto-configuration when NexWare feature flags are off,
 * so the application can start without those infrastructure services.
 */
public class NexwareInfrastructureEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String EXCLUDE_KEY = "spring.autoconfigure.exclude";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Set<String> excludes = new LinkedHashSet<>();
        String existing = environment.getProperty(EXCLUDE_KEY);
        if (existing != null && !existing.isBlank()) {
            for (String part : existing.split(",")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    excludes.add(trimmed);
                }
            }
        }

        if (!isEnabled(environment, "nexware.redis.enabled", true)) {
            excludes.add("org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration");
            excludes.add("org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration");
        }
        if (!isEnabled(environment, "nexware.kafka.enabled", true)) {
            excludes.add("org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration");
        }

        if (!excludes.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(
                    "nexwareInfrastructureExcludes",
                    Map.of(EXCLUDE_KEY, String.join(",", excludes))));
        }
    }

    private boolean isEnabled(ConfigurableEnvironment environment, String key, boolean defaultValue) {
        return environment.getProperty(key, Boolean.class, defaultValue);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
