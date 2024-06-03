package com.dlqudtjs.codingbattle.dto.game.responseDto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProblemsResponseDto {
    private List<ProblemInfoResponseDto> problems;
}
