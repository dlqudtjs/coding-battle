package com.dlqudtjs.codingbattle.dto.game.requestDto;

import com.dlqudtjs.codingbattle.common.constant.JudgeResultCode;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.dto.game.responseDto.ParsedJudgeResultResponseDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JudgeResultResponseDto {

    @NotNull
    private String roomId;

    @NotNull
    private String userId;

    @NotNull
    private String problemId;

    @NotNull
    private String submitId;

    @NotNull
    private ProgrammingLanguage language;

    @NotNull
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
                .currentTest(Long.parseLong(currentTest))
                .totalTests(Long.parseLong(totalTests))
                .executionTime(Long.parseLong(executionTime))
                .errorMessage(errorMessage)
                .containerId(containerId)
                .build();
    }
}
