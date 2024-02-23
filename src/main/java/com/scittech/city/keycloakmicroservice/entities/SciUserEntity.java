package com.scittech.city.keycloakmicroservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name = "sci_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SciUserEntity {
    
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long user_id;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "picture")
    private String picture;
    
    @Column(name = "created_at")
    private Timestamp created_at;

    public SciUserEntity(UserEntity userEntity) {
        this.username = userEntity.getUsername();
        this.email = userEntity.getEmail();
        this.picture = userEntity.getPicture();
        this.created_at = new Timestamp(System.currentTimeMillis());
    };
}
