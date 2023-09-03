package com.scittech.city.keycloakmicroservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scittech.city.keycloakmicroservice.entities.UserLoginCredentialsEntity;
import com.scittech.city.keycloakmicroservice.services.LogInUserService;


@RestController
@RequestMapping(value = "/api/keycloak-service")
@CrossOrigin(origins = "*")
public class KeycloakController {
    

    @Autowired
    private LogInUserService logInUserService;

    @GetMapping("/signup")
    public String createUser(){
        return "user";
    } 

    @PostMapping("/signin")
    public ResponseEntity<String> getToken(@RequestBody UserLoginCredentialsEntity userLoginCredentials){
        String username = userLoginCredentials.getUsername();
        String password = userLoginCredentials.getPassword();
        logInUserService.loginUserRequest(username, password);
        return new ResponseEntity <>("token", HttpStatus.OK);
    } 
    
    @PostMapping("/spect-token")
    public ResponseEntity<String> spectToken(){
        return new ResponseEntity <>("spect-token", HttpStatus.OK);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> destroyToken(){
        return new ResponseEntity <>("logout", HttpStatus.OK);
    } 

}
