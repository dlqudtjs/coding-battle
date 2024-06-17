package com.dlqudtjs.codingbattle.dto.oauth;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguageManager;
import com.dlqudtjs.codingbattle.common.validator.ProgrammingLanguage.ValidProgrammingLanguage;
import com.dlqudtjs.codingbattle.entity.user.ProgrammingLanguage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {

    @NotBlank
    private String userId;
    @NotBlank
    private String password;
    @NotBlank
    private String passwordCheck;
    @ValidProgrammingLanguage
    private String language;

    public ProgrammingLanguage getLanguage() {
        return ProgrammingLanguageManager.getLanguageFromName(language);
    }
}
