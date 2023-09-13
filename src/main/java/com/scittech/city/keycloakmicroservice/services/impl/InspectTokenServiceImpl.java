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

import com.scittech.city.keycloakmicroservice.services.InspectTokenService;

@Service
public class InspectTokenServiceImpl implements InspectTokenService {

    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private Environment environment;

    public InspectTokenServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<String> inspectToken(String userToken) {

        String url = environment.getProperty("keycloak.server") + "/protocol/openid-connect/token/introspect";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", environment.getProperty("keycloak.client_id"));
        formData.add("client_secret", environment.getProperty("keycloak.client_secret"));
        formData.add("token", userToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        return responseEntity;
    }
    
}
