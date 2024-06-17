package com.dlqudtjs.codingbattle.dto.game.requestDto;

import com.dlqudtjs.codingbattle.common.constant.SubmitResultManager;
import com.dlqudtjs.codingbattle.common.validator.submitResult.ValidSubmitResult;
import com.dlqudtjs.codingbattle.dto.game.responseDto.ParsedJudgeResultResponseDto;
import com.dlqudtjs.codingbattle.entity.submit.SubmitResult;
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
    @ValidSubmitResult
    private String result;
    private String errorMessage;
    private String executionTime;
    private String currentTest;
    private String totalTests;
    private String containerId;
    private String secretKey;

    public ParsedJudgeResultResponseDto toParsedJudgeResultResponseDto() {
        SubmitResult result = getSubmitResult();
        return ParsedJudgeResultResponseDto.builder()
                .roomId(Long.parseLong(roomId))
                .problemId(Long.parseLong(problemId))
                .userId(userId)
                .submitId(Long.parseLong(submitId))
                .result(result)
                .currentTest(result == SubmitResultManager.PASS ? Long.parseLong(currentTest) : 0L)
                .totalTests(result == SubmitResultManager.PASS ? Long.parseLong(totalTests) : 0L)
                .executionTime(result == SubmitResultManager.PASS ? Long.parseLong(executionTime) : 0L)
                .errorMessage(errorMessage)
                .containerId(containerId)
                .build();
    }

    public SubmitResult getSubmitResult() {
        return SubmitResultManager.getSubmitResultFromName(result);
    }
}
