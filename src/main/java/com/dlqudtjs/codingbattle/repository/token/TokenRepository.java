package com.dlqudtjs.codingbattle.repository.token;

import com.dlqudtjs.codingbattle.entity.oauth.Token;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserId(Long userId);
}
