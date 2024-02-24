package com.scittech.city.keycloakmicroservice.services;

import org.springframework.http.ResponseEntity;

import com.scittech.city.keycloakmicroservice.entities.UserEntity;

public interface SignupUserService {
    public ResponseEntity<?> signupUser(UserEntity userData);
}
