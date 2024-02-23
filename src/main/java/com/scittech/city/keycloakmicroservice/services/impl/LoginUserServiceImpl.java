package com.scittech.city.keycloakmicroservice.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.scittech.city.keycloakmicroservice.services.LogInUserService;

@Service
public class LoginUserServiceImpl implements LogInUserService {
    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private Environment environment;
    
    public LoginUserServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("null")
    @Override
    public ResponseEntity<String> loginUserRequest(String username,String password) {
        // Define the request URL
        String url = environment.getProperty("keycloak.server.url").toString() + "/protocol/openid-connect/token";

        // Create a MultiValueMap to hold the form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", "admin-cli");
        // formData.add("client_id", environment.getProperty("keycloak.client_id").toString());
        formData.add("client_secret", environment.getProperty("keycloak.client_secret").toString());
        formData.add("grant_type", "password");
        formData.add("username", username.toString());
        formData.add("password", password.toString());

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create a request entity with form data and headers
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // Make the POST request and get the response
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        return responseEntity;
    }

    @SuppressWarnings("null")
    @Override
    public ResponseEntity<String> generateToken(String username,String password) {
        // Define the request URL
        String url = environment.getProperty("keycloak.server.url").toString() + "/protocol/openid-connect/token";

        // Create a MultiValueMap to hold the form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", "admin-cli");
        formData.add("client_secret", environment.getProperty("keycloak.client_secret").toString());
        formData.add("grant_type", "password");
        formData.add("username", username.toString());
        formData.add("password", password.toString());

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create a request entity with form data and headers
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // Make the POST request and get the response
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        return responseEntity;
    }

}
