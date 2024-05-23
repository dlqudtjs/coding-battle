package com.dlqudtjs.codingbattle.dto.game.responseDto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProblemInfoResponseDto {

    private Long id;
    private String algorithmClassification;
    private String problemLevel;
    private String title;
    private String problemDescription;
    private String inputDescription;
    private String outputDescription;
    private String hint;
    private List<ProblemIOExampleResponseDto> problemIOExamples;
}
