package com.dlqudtjs.codingbattle.dto.judge;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguageManager;
import com.dlqudtjs.codingbattle.common.validator.programmingLanguage.ValidProgrammingLanguage;
import com.dlqudtjs.codingbattle.entity.user.ProgrammingLanguage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JudgeProblemRequestDto {

    @NotNull
    private Long problemId;
    @NotNull
    private Long roomId;
    @NotNull
    private String userId;
    @ValidProgrammingLanguage
    private String language;
    @NotNull
    private String code;

    public ProgrammingLanguage getProgrammingLanguage() {
        return ProgrammingLanguageManager.getLanguageFromName(language);
    }
}
