package com.example.server.service.impl;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncryption {

    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(16));
    }

    public static boolean checkPassword(String password, String encryptedPassword) {
        return BCrypt.checkpw(password, encryptedPassword);
    }
}
