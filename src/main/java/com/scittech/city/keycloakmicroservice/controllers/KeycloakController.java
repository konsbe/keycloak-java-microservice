package com.scittech.city.keycloakmicroservice.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.scittech.city.keycloakmicroservice.entities.CreateUserJSON;
import com.scittech.city.keycloakmicroservice.entities.UserEntity;
import com.scittech.city.keycloakmicroservice.entities.UserLoginCredentialsEntity;
import com.scittech.city.keycloakmicroservice.entities.UserTokenCredentialsEntity;
import com.scittech.city.keycloakmicroservice.services.EditUserInfoService;
import com.scittech.city.keycloakmicroservice.services.GetUserInfoService;
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
    private EditUserInfoService editUserInfoService;
    @Autowired
    private GetUserInfoService userInfoService;
    @Autowired
    private Environment environment;
    @Autowired
    private ObjectKey objectKey;

    @SuppressWarnings("unused")
    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody CreateUserJSON userData) {
        UserEntity userEntity = new UserEntity(userData);
        try {
            ResponseEntity<?> authenticationResponse = signupUserService.signupUser(userEntity);
            if (authenticationResponse.getStatusCode() == HttpStatus.OK
                    || authenticationResponse.getStatusCode() == HttpStatus.CREATED) {
                HttpHeaders headers = authenticationResponse.getHeaders();
                String locationHeader = headers.getFirst("Location");
                if (locationHeader != null) {
                    try {
                        URI locationUri = new URI(locationHeader);
                        return new ResponseEntity<>(ResponseEntity.created(locationUri).build(), HttpStatus.CREATED);
                    } catch (URISyntaxException e) {
                        return new ResponseEntity<>(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(),
                                HttpStatus.I_AM_A_TEAPOT);
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {
                @SuppressWarnings("null")
                JsonNode res = authenticationResponse.getBody() != null
                        ? objectKey.createJSONObject(authenticationResponse.getBody().toString())
                        : null;
                if (res != null) {
                    if (res != null)
                        return new ResponseEntity<>(res, authenticationResponse.getStatusCode());
                } else {
                    ErrorResponse error = new ErrorResponse(
                            OffsetDateTime.now(),
                            "418",
                            "drink a coffee",
                            "/api/keycloak-service/logout");
                    return new ResponseEntity<>(error, HttpStatus.I_AM_A_TEAPOT);
                }
            }
        } catch (HttpClientErrorException e) {
            ErrorResponse error = new ErrorResponse(
                    OffsetDateTime.now(),
                    e.getStatusCode().toString(),
                    e.getResponseBodyAsString(),
                    "/api/keycloak-service/logout");
            return new ResponseEntity<>(error, e.getStatusCode());
        }
        return new ResponseEntity<>(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(),
                HttpStatus.I_AM_A_TEAPOT);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> getToken(@RequestBody UserLoginCredentialsEntity userLoginCredentials) {
        String username = userLoginCredentials.getUsername();
        String password = userLoginCredentials.getPassword();
        JsonNode jsonNode;
        try {
            ResponseEntity<String> authenticationResponse = logInUserService.loginUserRequest(username, password);

            if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                String token = authenticationResponse.getBody();
                jsonNode = objectKey.createJSONObject(token);
                return new ResponseEntity<>(jsonNode, HttpStatus.OK);
            } else {
                String authError = authenticationResponse.getBody();
                jsonNode = objectKey.createJSONObject(authError);
                return new ResponseEntity<>(jsonNode, authenticationResponse.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            ErrorResponse error = new ErrorResponse(
                    OffsetDateTime.now(),
                    e.getStatusCode().toString(),
                    e.getResponseBodyAsString(),
                    "/api/keycloak-service/logout");

            return new ResponseEntity<>(error, e.getStatusCode());
        }
    }

    @PostMapping("/inspect-token")
    public ResponseEntity<?> spectToken(@RequestBody UserTokenCredentialsEntity access_token) {
        try {
            ResponseEntity<String> authenticationResponse = inspectTokenService
                    .inspectToken(access_token.getAccess_token());
            if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                String res = authenticationResponse.getBody();
                JsonNode jsonNode = objectKey.createJSONObject(res);
                ;
                return new ResponseEntity<>(jsonNode, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(authenticationResponse.getBody(), authenticationResponse.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            ErrorResponse error = new ErrorResponse(
                    OffsetDateTime.now(),
                    e.getStatusCode().toString(),
                    e.getResponseBodyAsString(),
                    "/api/keycloak-service/logout");

            return new ResponseEntity<>(error, e.getStatusCode());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> destroyToken(@RequestBody UserTokenCredentialsEntity access_token) {
        @SuppressWarnings("null")
        String client_id = environment.getProperty("keycloak.client_id").toString();
        String jwtToken = access_token.getAccess_token().toString();
        try {
            ResponseEntity<String> authenticationResponse = logOutUserService.logOutRequest(jwtToken, client_id);
            if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                String res = authenticationResponse.getBody();
                JsonNode jsonNode = objectKey.createJSONObject(res);
                ;
                return new ResponseEntity<>(jsonNode, HttpStatus.OK);
            } else {
                // Handle other responses if needed
                return new ResponseEntity<>(authenticationResponse.getBody(), authenticationResponse.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            ErrorResponse error = new ErrorResponse(
                    OffsetDateTime.now(),
                    e.getStatusCode().toString(),
                    e.getResponseBodyAsString(),
                    "/api/keycloak-service/logout");

            return new ResponseEntity<>(error, e.getStatusCode());
        }
    }

    @PutMapping("/edit-user")
    public ResponseEntity<?> editUserInfo(@RequestBody Object userData,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");

        try {
            ResponseEntity<?> authenticationResponse = editUserInfoService.editUserInfo(userData, token);
            if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                // String res = authenticationResponse.getBody();
                return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
            } else {
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

    @GetMapping("/get-user-info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");

        try {
            ResponseEntity<?> authenticationResponse = userInfoService.getUserInfo(token);
            if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                String res = (String) authenticationResponse.getBody();
                JsonNode jsonNode = objectKey.createJSONObject(res);

                return new ResponseEntity<>(jsonNode, HttpStatus.OK);
            } else {
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
