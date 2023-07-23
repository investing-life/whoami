package com.example.whoami.java;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomAddressGenerator {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String generateRandomAddress(Integer n) {
        SecureRandom random = new SecureRandom();
        StringBuilder address = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            address.append(randomChar);
        }

        return address.toString();
    }
}
