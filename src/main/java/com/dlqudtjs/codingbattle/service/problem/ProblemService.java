package com.dlqudtjs.codingbattle.service.problem;

import com.dlqudtjs.codingbattle.common.constant.AlgorithmType;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.problem.ProblemLevel;
import java.util.List;

public interface ProblemService {

    List<ProblemInfo> getProblemInfoList(AlgorithmType algorithmType, ProblemLevel problemLevel, Integer count);

    List<ProblemInfo> getProblemInfoList(ProblemLevel problemLevel, Integer count);
}
