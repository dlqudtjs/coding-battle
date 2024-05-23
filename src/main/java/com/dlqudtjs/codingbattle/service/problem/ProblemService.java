package com.dlqudtjs.codingbattle.service.problem;

import com.dlqudtjs.codingbattle.common.constant.AlgorithmType;
import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import java.util.List;

public interface ProblemService {

    List<ProblemInfo> getProblemInfoList(AlgorithmType algorithmType, ProblemLevelType problemLevel, Integer count);
}
