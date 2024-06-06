package com.dlqudtjs.codingbattle.dto.judge;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JudgeProblemRequestDto {

    private Long problemId;
    private Long roomId;
    private String userId;
    @NotNull
    private ProgrammingLanguage language;
    private String code;
}
