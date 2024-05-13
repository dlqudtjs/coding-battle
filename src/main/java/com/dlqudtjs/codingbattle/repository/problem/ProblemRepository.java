package com.dlqudtjs.codingbattle.repository.problem;

import com.dlqudtjs.codingbattle.entity.problem.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
}
