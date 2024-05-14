package com.dlqudtjs.codingbattle.repository.problem;

import com.dlqudtjs.codingbattle.entity.problem.Problem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    @Procedure(name = "getRandomProblems")
    List<Problem> getRandomProblems(Long algorithmId, Long problemLevelId, Integer count);
}
