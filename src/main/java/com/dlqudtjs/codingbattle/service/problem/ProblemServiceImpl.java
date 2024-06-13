package com.dlqudtjs.codingbattle.service.problem;

import com.dlqudtjs.codingbattle.common.constant.AlgorithmType;
import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.repository.problem.AlgorithmClassificationRepository;
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
    private final AlgorithmClassificationRepository algorithmClassificationRepository;

    @Override
    @Transactional
    public List<ProblemInfo> getProblemInfoList(AlgorithmType algorithmType,
                                                ProblemLevelType problemLevelType,
                                                Integer count) {
        Long algorithmId = algorithmClassificationRepository.findByName(algorithmType.name()).getId();

        return problemRepository.getRandomProblems(algorithmId, problemLevelType.name(), count).stream()
                .map(problem -> ProblemInfo.builder()
                        .problem(problem)
                        .problemIOExamples(problemIOExampleRepository.findByProblemId(problem.getId()))
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public List<ProblemInfo> getProblemInfoList(ProblemLevelType problemLevelType,
                                                Integer count) {
        return problemRepository.getRandomProblems(0L, problemLevelType.name(), count).stream()
                .map(problem -> ProblemInfo.builder()
                        .problem(problem)
                        .problemIOExamples(problemIOExampleRepository.findByProblemId(problem.getId()))
                        .build())
                .toList();
    }
}
