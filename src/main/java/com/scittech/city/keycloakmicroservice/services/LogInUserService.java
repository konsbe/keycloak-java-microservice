package com.scittech.city.keycloakmicroservice.services;

import org.springframework.http.ResponseEntity;

public interface LogInUserService {
    public ResponseEntity<String> loginUserRequest(String username,String password);
    public ResponseEntity<String> generateToken(String username,String password);
}
