package com.dlqudtjs.codingbattle.dto.game.requestDto;

import com.dlqudtjs.codingbattle.common.constant.JudgeResultCode;
import com.dlqudtjs.codingbattle.dto.game.responseDto.ParsedJudgeResultResponseDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JudgeResultRequestDto {

    private String roomId;
    private String userId;
    private String problemId;
    private String submitId;
    private String testcaseNumber;
    @NotNull
    private JudgeResultCode result;
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
                .currentTest(result == JudgeResultCode.PASS ? Long.parseLong(currentTest) : 0L)
                .totalTests(result == JudgeResultCode.PASS ? Long.parseLong(totalTests) : 0L)
                .executionTime(result == JudgeResultCode.PASS ? Long.parseLong(executionTime) : 0L)
                .errorMessage(errorMessage)
                .containerId(containerId)
                .build();
    }

}
