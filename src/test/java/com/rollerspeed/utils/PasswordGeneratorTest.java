package com.rollerspeed.utils;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGeneratorTest {

    @Test
    public void generatePassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("----------------------------------------");
        System.out.println("Password Generator Results:");
        System.out.println("----------------------------------------");
        System.out.println("Raw Password: " + rawPassword);
        System.out.println("Encoded Password: " + encodedPassword);
        System.out.println("----------------------------------------");

        // Verify the password
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("Password Verification Test: " + (matches ? "PASSED" : "FAILED"));
        System.out.println("----------------------------------------");
    }
}