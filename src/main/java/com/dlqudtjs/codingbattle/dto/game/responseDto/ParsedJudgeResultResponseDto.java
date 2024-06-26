package com.dlqudtjs.codingbattle.dto.game.responseDto;

import com.dlqudtjs.codingbattle.entity.submit.SubmitResult;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParsedJudgeResultResponseDto {

    private Long roomId;
    private Long problemId;
    private Long matchId;
    private Long submitId;
    private String userId;
    private SubmitResult result;  // PASS, FAIL, ERROR
    private Long currentTest;
    private Long totalTests;
    private Long executionTime;
    private String errorMessage;
    private String containerId;
}
