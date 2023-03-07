package com.dedsec.TheDedSec.repository;

import com.dedsec.TheDedSec.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User , Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
}
