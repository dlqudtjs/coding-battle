package com.dlqudtjs.codingbattle.repository.problem;

import com.dlqudtjs.codingbattle.entity.problem.ProblemLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemLevelRepository extends JpaRepository<ProblemLevel, Long> {
    ProblemLevel findByName(String name);
}
