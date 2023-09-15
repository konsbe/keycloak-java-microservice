package com.scittech.city.keycloakmicroservice.services.impl;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scittech.city.keycloakmicroservice.entities.UserEntity;
import com.scittech.city.keycloakmicroservice.services.LogInUserService;
import com.scittech.city.keycloakmicroservice.services.LogoutUserService;
import com.scittech.city.keycloakmicroservice.services.SignupUserService;
import com.scittech.city.keycloakmicroservice.utils.ErrorResponse;

@Service
public class SignupUserServiceImpl implements SignupUserService {

    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private Environment environment;
    @Autowired
    private LogInUserService loginUserService;
    @Autowired
    private LogoutUserService logoutUserService;

    public SignupUserServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<?> signupUser(UserEntity userData) {

        String url = environment.getProperty("keycloak.server") + "/admin/realms/sci-tech/users";
        String username = environment.getProperty("keycloak.username").toString();
        String password = environment.getProperty("keycloak.password").toString();

        try {
            ResponseEntity<String> authenticationResponse = loginUserService.generateToken(username, password);
            String token = authenticationResponse.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode;
            String access_token;

            if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                try {
                    jsonNode = objectMapper.readTree(token);
                    access_token = jsonNode.get("access_token").asText();
                } catch (JsonProcessingException e1) {
                    access_token = "Unexpected error occurred";
                }

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + access_token.toString());
                headers.setContentType(MediaType.APPLICATION_JSON);

                ObjectMapper postData = new ObjectMapper();
                String jsonBody;
                try {
                    jsonBody = postData.writeValueAsString(userData);
                } catch (Exception e) {
                    jsonBody = e.toString();
                }

                HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

                try {
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity,
                            String.class);
                    return responseEntity;
                } catch (HttpClientErrorException e) {
                    logoutUserService.logOutRequest(access_token);
                    ErrorResponse error = new ErrorResponse(
                            OffsetDateTime.now(),
                            e.getStatusCode().toString(),
                            e.toString(),
                            "/api/keycloak-service/signup");
                    return new ResponseEntity<>(error, e.getStatusCode());
                }
            } else {
                return new ResponseEntity<>(authenticationResponse.getBody(), authenticationResponse.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode;
            String errorDescription;
            try {
                jsonNode = objectMapper.readTree(responseBody);
                errorDescription = jsonNode.get("error").asText();
            } catch (JsonProcessingException e1) {
                errorDescription = "Unexpected error occurred";
            }
            ErrorResponse error = new ErrorResponse(
                    OffsetDateTime.now(),
                    e.getStatusCode().toString(),
                    errorDescription.toString(),
                    "/api/keycloak-service/signup");
            return new ResponseEntity<>(error, e.getStatusCode());
        }
    }

}
