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

import com.scittech.city.keycloakmicroservice.services.LogoutUserService;
import com.scittech.city.keycloakmicroservice.utils.ObjectKey;
import com.scittech.city.keycloakmicroservice.utils.TokenDecoder;


@Service
public class LogoutUserServiceImpl implements LogoutUserService {
    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private Environment environment;

    @Autowired
    public TokenDecoder tokenDecoder;

    @Autowired
    public ObjectKey objKey;

    public LogoutUserServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<String> logOutRequest(String userToken, String client_id) {
        // Define the request URL
        String url = environment.getProperty("keycloak.server.url") + "/protocol/openid-connect/logout";

        // Create a MultiValueMap to hold the form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", client_id);
        formData.add("client_secret", environment.getProperty("keycloak.client_secret"));
        formData.add("access_token", userToken);

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create a request entity with form data and headers
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // Make the POST request and get the response
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        return responseEntity;
    }

    @Override
    public ResponseEntity<String> endAllSessions(String userToken, String client_id) {

        String tokenData = tokenDecoder.decodeJwt(userToken);
        String sid = objKey.getKey(tokenData, "sub");
        
        String url = environment.getProperty("keycloak.server") +"/admin/realms/" + environment.getProperty("keycloak.realm")
                + "/users/" + sid + "/logout";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        HttpHeaders headers = new HttpHeaders();
        String bearerToken = "Bearer " + userToken; // Assuming 'userToken' contains the bearer token value
        headers.set("Authorization", bearerToken);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        return responseEntity;
    }

}
