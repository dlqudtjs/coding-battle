package com.dlqudtjs.codingbattle.entity.problem;

import com.dlqudtjs.codingbattle.dto.game.responseDto.ProblemIOExampleResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProblemInfo {

    private Problem problem;
    private List<ProblemIOExample> problemIOExamples;

    public List<ProblemIOExampleResponseDto> getProblemIOExamples() {
        return problemIOExamples.stream()
                .map(problemIOExample -> ProblemIOExampleResponseDto.builder()
                        .input(problemIOExample.getInput())
                        .output(problemIOExample.getOutput())
                        .build())
                .toList();
    }
}
