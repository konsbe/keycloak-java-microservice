package com.scittech.city.keycloakmicroservice.services;

import org.springframework.http.ResponseEntity;


public interface DeleteUserService {
        public ResponseEntity<?> deleteUser(String token);
}
