package com.dlqudtjs.codingbattle.dto.game.requestDto;

import com.dlqudtjs.codingbattle.dto.game.responseDto.JudgeResultResponseDto;
import lombok.Getter;

@Getter
public class JudgeResultRequestDto {

    private String roomId;
    private String userId;
    private String problemId;
    private String problemNumber;
    private String result;
    private String errorMessage;
    private String executionTime;
    private String currentTest;
    private String totalTests;
    private String secretKey;

    public JudgeResultResponseDto toJudgeResultResponseDto() {
        return JudgeResultResponseDto.builder()
                .roomId(Long.parseLong(roomId))
                .problemId(Long.parseLong(problemId))
                .userId(userId)
                .currentTest(Long.parseLong(currentTest))
                .result(result)
                .errorMessage(errorMessage)
                .executionTime(executionTime)
                .totalTests(totalTests)
                .build();
    }
}
