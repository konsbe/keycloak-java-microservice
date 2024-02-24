package com.scittech.city.keycloakmicroservice.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateUserJSON {

    private Long user_id;
    
    private String username;
    
    private String email;
    
    private String picture;
    
    private Timestamp created_at;
    
    private String password;
}
