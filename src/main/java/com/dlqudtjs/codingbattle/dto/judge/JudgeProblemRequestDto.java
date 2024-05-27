package com.dlqudtjs.codingbattle.dto.judge;

import lombok.Getter;

@Getter
public class JudgeProblemRequestDto {

    private Long problemId;
    private Long roomId;
    private Long matchId;
    private String userId;
    private String language;
    private String code;
}
