package com.scittech.city.keycloakmicroservice.services;

import org.springframework.http.ResponseEntity;


public interface LogoutUserService {
        public ResponseEntity<String> logOutRequest(String access_token);
}
