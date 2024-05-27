package com.dlqudtjs.codingbattle.dto.game.requestDto;

import com.dlqudtjs.codingbattle.dto.game.responseDto.ParsedJudgeResultResponseDto;
import lombok.Getter;

@Getter
public class JudgeResultResponseDto {

    private String roomId;
    private String userId;
    private String problemId;
    private String submitId;
    private String testcaseNumber;
    private String result;
    private String errorMessage;
    private String executionTime;
    private String currentTest;
    private String totalTests;
    private String containerId;
    private String secretKey;

    public ParsedJudgeResultResponseDto toParsedJudgeResultResponseDto() {
        return ParsedJudgeResultResponseDto.builder()
                .roomId(Long.parseLong(roomId))
                .problemId(Long.parseLong(problemId))
                .userId(userId)
                .submitId(Long.parseLong(submitId))
                .result(result)
                .currentTest(Long.parseLong(currentTest))
                .totalTests(Long.parseLong(totalTests))
                .executionTime(Long.parseLong(executionTime))
                .errorMessage(errorMessage)
                .containerId(containerId)
                .build();
    }
}
