package com.dlqudtjs.codingbattle.repository.game;

import com.dlqudtjs.codingbattle.entity.match.UserMatchingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMatchingHistoryRepository extends JpaRepository<UserMatchingHistory, Long> {
}
