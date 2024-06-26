package com.dlqudtjs.codingbattle.repository.user;

import com.dlqudtjs.codingbattle.entity.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
