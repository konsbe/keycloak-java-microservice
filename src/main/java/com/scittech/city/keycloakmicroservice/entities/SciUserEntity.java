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
    protected Long user_id;
    
    @Column(name = "username")
	protected String username;
    
    @Column(name = "email")
    protected String email;
    
    @Column(name = "picture")
    protected byte[] picture;
    
    @Column(name = "created_at")
    protected Timestamp created_at;

    public SciUserEntity(UserEntity userEntity) {
        this.username = userEntity.getUsername();
        this.email = userEntity.getEmail();
        this.picture =  userEntity.getPicture();
        this.created_at = new Timestamp(System.currentTimeMillis());
    };
}
