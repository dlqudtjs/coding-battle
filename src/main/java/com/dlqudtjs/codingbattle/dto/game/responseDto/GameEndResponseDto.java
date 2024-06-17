package com.dlqudtjs.codingbattle.dto.game.responseDto;

import com.dlqudtjs.codingbattle.entity.match.MatchResult;
import com.dlqudtjs.codingbattle.entity.user.ProgrammingLanguage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameEndResponseDto {

    private MatchResult result;
    private String userId;
    private String code;
    private ProgrammingLanguage language;
}
