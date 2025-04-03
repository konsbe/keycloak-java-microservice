package com.scittech.city.keycloakmicroservice.services.impl;

import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scittech.city.keycloakmicroservice.services.DeleteUserService;
import com.scittech.city.keycloakmicroservice.utils.ErrorResponse;
import com.scittech.city.keycloakmicroservice.utils.ObjectKey;
import com.scittech.city.keycloakmicroservice.utils.TokenDecoder;
import java.util.Collections;

@Service
public class DeleteUserServiceImpl implements DeleteUserService {
    
    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private Environment environment;
    @Autowired
    private ObjectKey objKey;
    @Autowired
    public TokenDecoder tokenDecoder;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteUserServiceImpl.class);

    public DeleteUserServiceImpl(RestTemplate restTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }
    @Override
    public ResponseEntity<?> deleteUser(String token) {
        // Decode the token to extract the user ID (subject)
        String tokenData = tokenDecoder.decodeJwt(token);

        String sub = objKey.getKey(tokenData, "sub");
        String email = objKey.getKey(tokenData, "email");
    
        // Construct the Keycloak delete user URL
        String url = environment.getProperty("keycloak.server") + "/admin/realms/"
                + environment.getProperty("keycloak.realm")
                + "/users/" + sub;
    
        // Set the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        String bearerToken = "Bearer " + token;
        headers.set("Authorization", bearerToken);

        // Prepare the request entity
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    
        try {
            // Make the DELETE request
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
            LOGGER.info("Send user to delete --> {}", email);
            
            // Wrap the email into a JSON object
            ObjectMapper objectMapper = new ObjectMapper();
            String emailJson = objectMapper.writeValueAsString(Collections.singletonMap("email", email));
            
            // Send the serialized JSON object to Kafka
            kafkaTemplate.send("sci-tech.city", emailJson);
            return responseEntity; // Return the response directly
        } catch (HttpClientErrorException | JsonProcessingException e) {
            // Handle errors
            ErrorResponse error = new ErrorResponse(
                    OffsetDateTime.now(),
                    ((RestClientResponseException) e).getStatusCode().toString(),
                    e.toString(),
                    "/api/keycloak-service/delete-user");
            return new ResponseEntity<>(error, ((RestClientResponseException) e).getStatusCode());
        }
    }
    
}
