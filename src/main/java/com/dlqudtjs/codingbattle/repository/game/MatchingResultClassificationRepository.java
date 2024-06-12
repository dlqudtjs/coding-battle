package com.dlqudtjs.codingbattle.repository.game;

import com.dlqudtjs.codingbattle.entity.match.MatchingResultClassification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingResultClassificationRepository extends
        JpaRepository<MatchingResultClassification, Long> {

}
