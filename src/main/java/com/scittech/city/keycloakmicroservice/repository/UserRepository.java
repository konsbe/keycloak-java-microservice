package com.scittech.city.keycloakmicroservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scittech.city.keycloakmicroservice.entities.SciUserEntity;

@Repository
public interface UserRepository extends JpaRepository<SciUserEntity,Long>{
    SciUserEntity findByEmail(String email);
};
