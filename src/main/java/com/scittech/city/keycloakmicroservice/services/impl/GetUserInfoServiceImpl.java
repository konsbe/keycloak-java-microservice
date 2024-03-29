package com.scittech.city.keycloakmicroservice.services.impl;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.scittech.city.keycloakmicroservice.services.GetUserInfoService;
import com.scittech.city.keycloakmicroservice.utils.ErrorResponse;
import com.scittech.city.keycloakmicroservice.utils.ObjectKey;
import com.scittech.city.keycloakmicroservice.utils.TokenDecoder;

@Service
public class GetUserInfoServiceImpl implements GetUserInfoService {

    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private Environment environment;
    @Autowired
    private ObjectKey objKey;
    @Autowired
    public TokenDecoder tokenDecoder;

    public GetUserInfoServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<?> getUserInfo(String token) {

        String tokenData = tokenDecoder.decodeJwt(token);
        String sub = objKey.getKey(tokenData, "sub");
        String url = environment.getProperty("keycloak.server") + "/admin/realms/"
                + environment.getProperty("keycloak.realm")
                + "/users/" + sub;
        String jsonBody = "";

        HttpHeaders headers = new HttpHeaders();

        String bearerToken = "Bearer " + token;
        headers.set("Authorization", bearerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        try {
            @SuppressWarnings("null")
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
                    String.class);
            return responseEntity;
        } catch (HttpClientErrorException e) {
            ErrorResponse error = new ErrorResponse(
                    OffsetDateTime.now(),
                    e.getStatusCode().toString(),
                    e.toString(),
                    "/api/keycloak-service/edit-user");
            return new ResponseEntity<>(error, e.getStatusCode());
        }

    }
}
