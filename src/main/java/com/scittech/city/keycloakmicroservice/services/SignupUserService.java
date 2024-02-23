package com.scittech.city.keycloakmicroservice.services;

import org.springframework.http.ResponseEntity;

import com.scittech.city.keycloakmicroservice.entities.KeycloakEntity;

public interface SignupUserService {
    public ResponseEntity<?> signupUser(KeycloakEntity userData);
}
