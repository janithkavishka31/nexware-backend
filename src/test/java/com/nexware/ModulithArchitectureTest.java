package com.nexware;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Verifies Spring Modulith detects all domain modules. Strict boundary verification
 * ({@code modules.verify()}) can be enabled once cross-module APIs are extracted to named interfaces.
 */
class ModulithArchitectureTest {

    @Test
    void applicationModulesAreDiscovered() {
        ApplicationModules modules = ApplicationModules.of(NexwareBackendApplication.class);
        assertThat(modules.stream().map(module -> module.getName()))
                .contains(
                        "auth",
                        "user",
                        "product",
                        "inventory",
                        "warehouse",
                        "supplier",
                        "purchaseorder",
                        "reservation",
                        "notification",
                        "audit",
                        "shared",
                        "config"
                );
    }
}
