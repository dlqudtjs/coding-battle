package com.dlqudtjs.codingbattle.repository.user;

import com.dlqudtjs.codingbattle.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUserId(String userId);
}
