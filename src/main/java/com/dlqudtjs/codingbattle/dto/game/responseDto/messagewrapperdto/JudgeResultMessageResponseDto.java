package com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.game.responseDto.JudgeResultResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JudgeResultMessageResponseDto {
    private JudgeResultResponseDto judgeResult;
}
