package com.scittech.city.keycloakmicroservice.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private boolean emailVerified;
    private List<Credentials> credentials;
    private Access access;
    private List<String> realmRoles;
    private List<String> groups;
    private boolean enabled;

    public UserEntity(KeycloakEntity keycloakEntity) {
        this.email = keycloakEntity.getEmail();
        this.firstName = keycloakEntity.getFirstName();
        this.lastName = keycloakEntity.getLastName();
        this.username = keycloakEntity.getUsername();
        this.emailVerified = keycloakEntity.isEmailVerified();
        this.credentials = keycloakEntity.getCredentials();
        this.access = keycloakEntity.getAccess();
        this.realmRoles = keycloakEntity.getRealmRoles();
        this.groups = keycloakEntity.getGroups();
        this.enabled = keycloakEntity.isEnabled();
    }
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class Credentials {
    private String type;
    private String value;
    private boolean temporary;
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