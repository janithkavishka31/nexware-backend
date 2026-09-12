package com.nexware.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** One-off helper to print BCrypt hashes for seed migrations. */
public final class BcryptHashGenerator {

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder(12).encode("Password123!"));
    }

    private BcryptHashGenerator() {
    }
}
