package com.scittech.city.keycloakmicroservice.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
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

    public LoginUserServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<String> loginUserRequest(String username,String password) {
        // Define the request URL
        String url = "http://67.202.48.180:8080/realms/sci-tech/protocol/openid-connect/token";

        // Create a MultiValueMap to hold the form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", "sci-tech.city");
        formData.add("client_secret", "Sx41lvm2LWQG0edBR81rOlWm5UiqtDjJ");
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
