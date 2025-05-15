package com.myshop.ecommerce;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "1341992necko"; // CAMBIA QUESTO!
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Password per admin: " + encodedPassword);
    }
}