package com.scittech.city.keycloakmicroservice.services;

import org.springframework.http.ResponseEntity;

public interface GetUserInfoService {
    public ResponseEntity<?> getUserInfo(String token);
}
