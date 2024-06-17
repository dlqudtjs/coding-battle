package com.dlqudtjs.codingbattle.service.problem;

import com.dlqudtjs.codingbattle.entity.problem.Algorithm;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.problem.ProblemLevel;
import com.dlqudtjs.codingbattle.repository.problem.AlgorithmRepository;
import com.dlqudtjs.codingbattle.repository.problem.ProblemIOExampleRepository;
import com.dlqudtjs.codingbattle.repository.problem.ProblemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemIOExampleRepository problemIOExampleRepository;

    @Override
    @Transactional
    public List<ProblemInfo> getProblemInfoList(Algorithm algorithm,
                                                ProblemLevel problemLevel,
                                                Integer count) {
        return problemRepository.getRandomProblems(algorithm.getId(), problemLevel.getName(), count).stream()
                .map(problem -> ProblemInfo.builder()
                        .problem(problem)
                        .problemIOExamples(problemIOExampleRepository.findByProblemId(problem.getId()))
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public List<ProblemInfo> getProblemInfoList(ProblemLevel problemLevel,
                                                Integer count) {
        return problemRepository.getRandomProblems(0L, problemLevel.getName(), count).stream()
                .map(problem -> ProblemInfo.builder()
                        .problem(problem)
                        .problemIOExamples(problemIOExampleRepository.findByProblemId(problem.getId()))
                        .build())
                .toList();
    }
}
