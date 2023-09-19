package com.scittech.city.keycloakmicroservice.services;

import org.springframework.http.ResponseEntity;


public interface EditUserInfoService {
    public ResponseEntity<?> editUserInfo(Object entity, String token);
}
