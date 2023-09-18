package com.scittech.city.keycloakmicroservice.utils;

import java.util.Base64;

import org.springframework.stereotype.Service;


@Service
public class TokenDecoder {

    public String decodeJwt(String jwtToken) {
        try {
            String[] chunks = jwtToken.split("\\.");

            Base64.Decoder decoder = Base64.getUrlDecoder();
            
            // String header = new String(decoder.decode(chunks[0]));
            String payload = new String(decoder.decode(chunks[1]));
            
            return payload;
        } catch (Exception e) {
            // Handle exception, e.g., invalid token or signature
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }
}
