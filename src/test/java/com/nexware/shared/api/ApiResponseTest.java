package com.nexware.shared.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiResponseTest {

    @Test
    void successWrapsPayloadWithStandardEnvelope() {
        ApiResponse<String> response = ApiResponse.success("created", "payload");

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("created");
        assertThat(response.data()).isEqualTo("payload");
        assertThat(response.error()).isNull();
        assertThat(response.timestamp()).isNotNull();
    }

    @Test
    void failureWrapsApiErrorWithStandardEnvelope() {
        ApiError error = ApiError.of(ErrorCode.CONFLICT, "Conflict", "/api/v1/test", "trace-1");

        ApiResponse<Void> response = ApiResponse.failure(error);

        assertThat(response.success()).isFalse();
        assertThat(response.data()).isNull();
        assertThat(response.error()).isEqualTo(error);
    }
}
