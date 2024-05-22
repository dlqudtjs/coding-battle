package com.dlqudtjs.codingbattle.dto.judge;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class JudgeProblemRequestDto {

    @NotBlank
    private Long problemId;
    @NotBlank
    private Long roomId;
    @NotBlank
    private String userId;
    @NotBlank
    private String language;
    @NotBlank
    private String code;
}
