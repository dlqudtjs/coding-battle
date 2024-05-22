package com.dlqudtjs.codingbattle.dto.game.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JudgeResultResponseDto {

    private Long roomId;
    private Long problemId;
    private String userId;
    private Long currentTest;
    private String result;  // PASS, FAIL, ERROR
    private String errorMessage;
    private String executionTime;
    private String totalTests;
}
