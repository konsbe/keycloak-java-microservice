package com.scittech.city.keycloakmicroservice.utils;

import java.util.Base64;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Base64Decoder {

    byte[] decodedBytes;
    String imageTypeFormat;

    public Base64Decoder(String base64String) {
        // Split the string based on the comma (',') delimiter
        String[] parts = base64String.split(",");

        // Check if there are at least two parts after splitting
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid base64 string format");
        }

        // Extract the base64-encoded data from the second part
        String base64Data = parts[1];
        // Decode the base64 data
        this.imageTypeFormat = parts[0];
        this.decodedBytes = Base64.getDecoder().decode(base64Data);
    }
    
}
