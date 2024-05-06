package com.scittech.city.keycloakmicroservice.services.impl;

import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scittech.city.keycloakmicroservice.entities.KeycloakEntity;
import com.scittech.city.keycloakmicroservice.entities.SciUserEntity;
import com.scittech.city.keycloakmicroservice.entities.UserEntity;
import com.scittech.city.keycloakmicroservice.services.LogInUserService;
import com.scittech.city.keycloakmicroservice.services.LogoutUserService;
import com.scittech.city.keycloakmicroservice.services.SignupUserService;
import com.scittech.city.keycloakmicroservice.utils.ErrorResponse;
import com.scittech.city.keycloakmicroservice.utils.ObjectKey;


@Service
public class SignupUserServiceImpl implements SignupUserService {


    @Value("${rabbitmq.topic.exchange.name}")
    private String topicExchangeName;
    @Value("${rabbitmq.queue.route.key}")
    private String routingKey;

    @Autowired
    private final RestTemplate restTemplate;
    // @Autowired
    // private final UserRepository userRepository;
    @Autowired
    private Environment environment;
    @Autowired
    private LogInUserService loginUserService;
    @Autowired
    private LogoutUserService logoutUserService;
    @Autowired
    private ObjectKey objectKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(SignupUserServiceImpl.class);

    private RabbitTemplate rabbitTemplate;

    public SignupUserServiceImpl(RestTemplate restTemplate, RabbitTemplate rabbitTemplate) {
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @SuppressWarnings("null")
    @Override
    public ResponseEntity<?> signupUser(UserEntity userData) {

        String url = environment.getProperty("keycloak.server") + "/admin/realms/"
                + environment.getProperty("keycloak.realm") + "/users";
        String username = environment.getProperty("keycloak.username").toString();
        String password = environment.getProperty("keycloak.password").toString();
        String client_id = "admin-cli";

        try {
            ResponseEntity<String> authenticationResponse = loginUserService.generateToken(username, password);
            String token = authenticationResponse.getBody();
            String access_token = objectKey.getKey(token, "access_token");

            if (authenticationResponse.getStatusCode() == HttpStatus.OK) {

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + access_token.toString());
                headers.setContentType(MediaType.APPLICATION_JSON);

                ObjectMapper postData = new ObjectMapper();
                String jsonBody;
                KeycloakEntity keycloak_user_ent = new KeycloakEntity(userData);
                try {
                    jsonBody = postData.writeValueAsString(keycloak_user_ent);
                } catch (Exception e) {
                    jsonBody = e.toString();
                }

                HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

                try {
                    // create user in keycloak
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity,
                            String.class);
                    // save user information in our database
                    SciUserEntity sci_user_ent = new SciUserEntity(userData);
                    // userRepository.save(sci_user_ent);
                    String sci_user_parString = objectKey.createObject(sci_user_ent);
                    LOGGER.info("User send --> {}", sci_user_parString);
                    rabbitTemplate.convertAndSend(topicExchangeName, routingKey, userData);

                    // restTemplate.postForLocation("http://localhost:8081/user-info", sci_user_ent);
                    logoutUserService.endSession(access_token, client_id);
                    return responseEntity;
                } catch (HttpClientErrorException e) {
                    logoutUserService.endSession(access_token, client_id);
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
            String errorDescription = objectKey.getKey(responseBody, "error");
            ErrorResponse error = new ErrorResponse(
                    OffsetDateTime.now(),
                    e.getStatusCode().toString(),
                    errorDescription.toString(),
                    "/api/keycloak-service/signup");
            return new ResponseEntity<>(error, e.getStatusCode());
        }
    }

}
