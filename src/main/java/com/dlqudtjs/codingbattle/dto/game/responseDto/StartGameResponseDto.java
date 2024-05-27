package com.dlqudtjs.codingbattle.dto.game.responseDto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StartGameResponseDto {
    
    private Long matchId;
    private List<ProblemInfoResponseDto> gameStartInfo;
}
