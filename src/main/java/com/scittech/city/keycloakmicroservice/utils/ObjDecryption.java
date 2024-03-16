package com.scittech.city.keycloakmicroservice.utils;

import org.mindrot.jbcrypt.BCrypt;

public class ObjDecryption {

    public static String decryptHash(String hashedString) {
        try {
            // Here, we're using the verify method of BCrypt to check if the hash matches
            // the original string
            if (BCrypt.checkpw("your_original_string", hashedString)) {
                return "Decryption successful!";
            } else {
                return "Decryption failed. Hash does not match the original string.";
            }
        } catch (IllegalArgumentException e) {
            return "Invalid hash provided for decryption.";
        } catch (NullPointerException e) {
            return "Hash is null. Please provide a valid hash for decryption.";
        }
    }
}
