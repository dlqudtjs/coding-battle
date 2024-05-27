package com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.game.responseDto.ParsedJudgeResultResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JudgeResultMessageResponseDto {
    private ParsedJudgeResultResponseDto judgeResult;
}
