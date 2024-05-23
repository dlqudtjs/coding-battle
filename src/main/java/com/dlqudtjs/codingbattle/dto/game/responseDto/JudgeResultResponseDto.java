package com.dlqudtjs.codingbattle.dto.game.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JudgeResultResponseDto {

    private Long roomId;
    private Long problemId;
    private String userId;
    private String result;  // PASS, FAIL, ERROR
    private Long currentTest;
    private Long totalTests;
    private String executionTime;
    private String errorMessage;
}
