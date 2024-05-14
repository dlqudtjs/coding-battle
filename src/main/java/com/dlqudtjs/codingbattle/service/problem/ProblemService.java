package com.dlqudtjs.codingbattle.service.problem;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.entity.problem.Problem;
import java.util.List;

public interface ProblemService {

    List<Problem> getProblemList(ProblemLevelType problemLevel, Integer count);
}
