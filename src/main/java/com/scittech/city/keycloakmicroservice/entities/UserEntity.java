package com.scittech.city.keycloakmicroservice.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UserEntity {
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private boolean emailVerified;
    private List<?> credentials;
    private Access access;
    private List<String> realmRoles;
    private List<String> groups;
    private boolean enabled;
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