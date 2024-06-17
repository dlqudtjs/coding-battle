package com.dlqudtjs.codingbattle.dto.game.responseDto;

import com.dlqudtjs.codingbattle.common.constant.MatchingResultType;
import com.dlqudtjs.codingbattle.entity.user.ProgrammingLanguage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameEndResponseDto {

    private MatchingResultType result;
    private String userId;
    private String code;
    private ProgrammingLanguage language;
}
