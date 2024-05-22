package com.dlqudtjs.codingbattle.dto.game.responseDto;

import com.dlqudtjs.codingbattle.entity.problem.Problem;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StartGameResponseDto {
    private List<Problem> problemList;
}
