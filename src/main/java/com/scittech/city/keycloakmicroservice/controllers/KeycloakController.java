package com.scittech.city.keycloakmicroservice.controllers;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.scittech.city.keycloakmicroservice.entities.UserEntity;
import com.scittech.city.keycloakmicroservice.entities.UserLoginCredentialsEntity;
import com.scittech.city.keycloakmicroservice.entities.UserTokenCredentialsEntity;
import com.scittech.city.keycloakmicroservice.services.InspectTokenService;
import com.scittech.city.keycloakmicroservice.services.LogInUserService;
import com.scittech.city.keycloakmicroservice.services.LogoutUserService;
import com.scittech.city.keycloakmicroservice.services.SignupUserService;
import com.scittech.city.keycloakmicroservice.utils.ErrorResponse;
import com.scittech.city.keycloakmicroservice.utils.ObjectKey;

@RestController
@RequestMapping(value = "/api/keycloak-service")
@CrossOrigin(origins = "*")
public class KeycloakController {

    @Autowired
    private LogInUserService logInUserService;
    @Autowired
    private LogoutUserService logOutUserService;
    @Autowired
    private InspectTokenService inspectTokenService;
    @Autowired
    private SignupUserService signupUserService;
    @Autowired
    private Environment environment;
    @Autowired
    private ObjectKey objectKey;

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody UserEntity userData) {
        try {
            ResponseEntity<?> authenticationResponse = signupUserService.signupUser(userData);
            if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                String token = authenticationResponse.getBody().toString();
                return new ResponseEntity<>(token, HttpStatus.OK);
            } else {
                // Handle other responses if needed
                return new ResponseEntity<>(authenticationResponse.getBody(), authenticationResponse.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            String errorDescription = objectKey.getKey(responseBody, "error_description");

            ErrorResponse error = new ErrorResponse(
                    OffsetDateTime.now(),
                    e.getStatusCode().toString(),
                    errorDescription.toString(),
                    "/api/keycloak-service/logout");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
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
                        "/api/keycloak-service/signin");
                return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
            } else {
                // Handle other exceptions or return an appropriate response
                return new ResponseEntity<>("Unexpected error occurred", e.getStatusCode());
                // return new ResponseEntity<>("Unexpected error occurred",
                // HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PostMapping("/inspect-token")
    public ResponseEntity<?> spectToken(@RequestBody UserTokenCredentialsEntity access_token) {
        try {
            ResponseEntity<String> authenticationResponse = inspectTokenService
                    .inspectToken(access_token.getAccess_token());
            if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                String token = authenticationResponse.getBody();
                return new ResponseEntity<>(token, HttpStatus.OK);
            } else {
                // Handle other responses if needed
                return new ResponseEntity<>(authenticationResponse.getBody(), authenticationResponse.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();

            String errorDescription = objectKey.getKey(responseBody, "error_description");

            ErrorResponse error = new ErrorResponse(
                    OffsetDateTime.now(),
                    e.getStatusCode().toString(),
                    errorDescription.toString(),
                    "/api/keycloak-service/logout");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> destroyToken(@RequestBody UserTokenCredentialsEntity access_token) {
        String client_id = environment.getProperty("keycloak.client_id").toString();
        String jwtToken = access_token.getAccess_token().toString();
        try {
            ResponseEntity<String> authenticationResponse = logOutUserService.endSession(jwtToken, client_id);
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
                    "/api/keycloak-service/logout");
            return new ResponseEntity<>(error, e.getStatusCode());
        }
    }

}
