package com.scittech.city.keycloakmicroservice.services.impl;

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
import org.springframework.web.client.RestTemplate;

import com.scittech.city.keycloakmicroservice.entities.SciUserEntity;
import com.scittech.city.keycloakmicroservice.repository.UserRepository;
import com.scittech.city.keycloakmicroservice.services.LogInUserService;
import com.scittech.city.keycloakmicroservice.utils.ObjectKey;

@Service
public class LoginUserServiceImpl implements LogInUserService {
    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private Environment environment;
    @Autowired
    private ObjectKey objKey;
    @Autowired
    private final UserRepository userRepository;

    public LoginUserServiceImpl(RestTemplate restTemplate, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    @SuppressWarnings("null")
    @Override
    public ResponseEntity<String> loginUserRequest(String username, String password) {
        // Define the request URL
        String url = environment.getProperty("keycloak.server.url").toString() + "/protocol/openid-connect/token";

        // Create a MultiValueMap to hold the form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", "admin-cli");
        // formData.add("client_id",
        // environment.getProperty("keycloak.client_id").toString());
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
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            SciUserEntity sciUserEntity = userRepository.findByEmail(username);
            String jsonSciUserEntity = objKey.createObject(sciUserEntity);
            // Combine sciUserEntity response with the original responseEntity
            String combinedResponse = String.format("[%s,%s]", responseEntity.getBody(), jsonSciUserEntity);

            // Create a new ResponseEntity with the combined response and the same status as
            // the original responseEntity
            ResponseEntity<String> combinedResponseEntity = new ResponseEntity<>(combinedResponse,
                    responseEntity.getStatusCode());
            return combinedResponseEntity;
        }
        return responseEntity;
    }

    @SuppressWarnings("null")
    @Override
    public ResponseEntity<String> generateToken(String username, String password) {
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
