package com.scittech.city.keycloakmicroservice.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakEntity {
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private boolean emailVerified = true;
    private List<Credentials> credentials;
    private Access access;
    private List<String> realmRoles = new ArrayList<>(Arrays.asList("app_client", "manage-users"));
    private List<String> groups = new ArrayList<>(Arrays.asList("sci-tech.client"));
    private boolean enabled = true;

    public KeycloakEntity(UserEntity userEntity) {
        this.email = userEntity.getEmail();
        this.firstName = userEntity.getUsername();
        this.lastName = userEntity.getUsername();
        this.username = userEntity.getUsername();
        this.credentials = Arrays.asList(new Credentials(userEntity.getPassword()));
        // this.access = this.getAccess();
        // this.emailVerified = this.isEmailVerified();
        // this.realmRoles = this.getRealmRoles();
        // this.groups = this.getGroups();
        // this.enabled = this.isEnabled();
    }
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class Credentials {
    private String type = "password";
    private String value;
    private boolean temporary = false;
    public Credentials(String pass) {
        this.value = pass;
    }
}
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class Access {
    private boolean manageGroupMembership;
    private boolean view;
    private boolean mapRoles;
    private boolean impersonate;
    private boolean manage;
}