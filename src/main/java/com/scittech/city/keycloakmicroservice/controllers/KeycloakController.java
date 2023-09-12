package com.scittech.city.keycloakmicroservice.controllers;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.scittech.city.keycloakmicroservice.entities.UserLoginCredentialsEntity;
import com.scittech.city.keycloakmicroservice.entities.UserTokenCredentialsEntity;
import com.scittech.city.keycloakmicroservice.services.LogInUserService;
import com.scittech.city.keycloakmicroservice.services.LogoutUserService;
import com.scittech.city.keycloakmicroservice.utils.ErrorResponse;

@RestController
@RequestMapping(value = "/api/keycloak-service")
@CrossOrigin(origins = "*")
public class KeycloakController {

    @Autowired
    private LogInUserService logInUserService;
    @Autowired
    private LogoutUserService logOutUserService;

    @GetMapping("/signup")
    public String createUser() {
        return "user";
    }

    @PostMapping("/signin")
    public ResponseEntity<?> getToken(@RequestBody UserLoginCredentialsEntity userLoginCredentials) {
        String username = userLoginCredentials.getUsername();
        String password = userLoginCredentials.getPassword();
        try {
            ResponseEntity<String> authenticationResponse = logInUserService.loginUserRequest(username, password);

            if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                String token = authenticationResponse.getBody();
                return new ResponseEntity<>(token, HttpStatus.OK);
            } else {
                // Handle other responses if needed
                return new ResponseEntity<>(authenticationResponse.getBody(), authenticationResponse.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // Handle 401 Unauthorized response here
                ErrorResponse error = new ErrorResponse(
                    OffsetDateTime.now(),
                    HttpStatus.UNAUTHORIZED.toString(),
                    "Unauthorized",
                    "/api/keycloak-service/signin"
                );
                return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
            } else {
                // Handle other exceptions or return an appropriate response
                return new ResponseEntity<>("Unexpected error occurred", e.getStatusCode());
                // return new ResponseEntity<>("Unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("/spect-token")
    public ResponseEntity<?> spectToken() {
        return new ResponseEntity<>("spect-token", HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> destroyToken(@RequestBody UserTokenCredentialsEntity access_token) {
        try {
            ResponseEntity<String> authenticationResponse = logOutUserService.logOutRequest(access_token.getAccess_token());
            if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                String token = authenticationResponse.getBody();
                return new ResponseEntity<>(token, HttpStatus.OK);
            } else {
                // Handle other responses if needed
                return new ResponseEntity<>(authenticationResponse.getBody(), authenticationResponse.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
                ErrorResponse error = new ErrorResponse(
                    OffsetDateTime.now(),
                    e.getStatusCode().toString(),
                    "Unexpected error occurred",
                    "/api/keycloak-service/logout"
                );
                return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }

}
