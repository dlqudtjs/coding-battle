package com.dlqudtjs.codingbattle.dto.room.requestdto;

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
public class RoomUserStatusUpdateRequestDto {
    @NotNull
    private String userId;
    @NotNull
    private Boolean isReady;
    @ValidProgrammingLanguage
    private String language;

    public ProgrammingLanguage getProgrammingLanguage() {
        return ProgrammingLanguageManager.getLanguageFromName(language);
    }
}
