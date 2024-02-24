package com.scittech.city.keycloakmicroservice.entities;

import java.sql.Timestamp;


import com.scittech.city.keycloakmicroservice.utils.Base64Decoder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends SciUserEntity {

    private String password;
    
    public UserEntity(CreateUserJSON userEntity) {
        Base64Decoder pic = new Base64Decoder(userEntity.getPicture());
        this.username = userEntity.getUsername();
        this.email = userEntity.getEmail();
        this.picture =  pic.getDecodedBytes();
        this.image_type =  pic.getImageTypeFormat();
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.password = userEntity.getPassword();
    };
}

