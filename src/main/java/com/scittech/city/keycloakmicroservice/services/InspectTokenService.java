package com.scittech.city.keycloakmicroservice.services;

import org.springframework.http.ResponseEntity;

public interface InspectTokenService {
    public ResponseEntity<String> inspectToken(String userToken);
}
