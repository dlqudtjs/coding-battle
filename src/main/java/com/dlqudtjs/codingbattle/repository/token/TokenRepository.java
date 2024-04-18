package com.dlqudtjs.codingbattle.repository.token;

import com.dlqudtjs.codingbattle.model.oauth.JwtToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<JwtToken, Long> {
    Optional<JwtToken> findByUserId(Long userId);
}
