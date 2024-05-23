package com.dlqudtjs.codingbattle.repository.problem;

import com.dlqudtjs.codingbattle.entity.problem.ProblemIOExample;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemIOExampleRepository extends JpaRepository<ProblemIOExample, Long> {

    List<ProblemIOExample> findByProblemId(Long problemId);
}
