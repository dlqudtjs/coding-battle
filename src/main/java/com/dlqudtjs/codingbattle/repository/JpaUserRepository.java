package com.dlqudtjs.codingbattle.repository;

import com.dlqudtjs.codingbattle.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    boolean existsByUserId(String userId);
    boolean existsByNickname(String nickname);
}
