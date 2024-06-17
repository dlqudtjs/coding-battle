package com.dlqudtjs.codingbattle.service.problem;

import com.dlqudtjs.codingbattle.entity.problem.Algorithm;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.problem.ProblemLevel;
import java.util.List;

public interface ProblemService {

    List<ProblemInfo> getProblemInfoList(Algorithm algorithm, ProblemLevel problemLevel, Integer count);

    List<ProblemInfo> getProblemInfoList(ProblemLevel problemLevel, Integer count);
}
