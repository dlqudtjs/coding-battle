package com.dlqudtjs.codingbattle.repository.game;

import com.dlqudtjs.codingbattle.entity.match.MatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchHistoryRepository extends JpaRepository<MatchHistory, Long> {
}
