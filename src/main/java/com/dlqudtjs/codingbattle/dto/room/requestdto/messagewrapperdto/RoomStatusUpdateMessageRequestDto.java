package com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import com.dlqudtjs.codingbattle.common.constant.ProblemLevelManager;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguageManager;
import com.dlqudtjs.codingbattle.common.validator.problemLevel.ValidProblemLevel;
import com.dlqudtjs.codingbattle.common.validator.programmingLanguage.ValidProgrammingLanguage;
import com.dlqudtjs.codingbattle.entity.problem.ProblemLevel;
import com.dlqudtjs.codingbattle.entity.user.ProgrammingLanguage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RoomStatusUpdateMessageRequestDto {

    @Getter
    @NotNull
    private String hostId;

    @Getter
    @NotNull
    private String title;

    @Getter
    @NotNull
    private String password;

    @ValidProgrammingLanguage
    private String language;

    @ValidProblemLevel
    private String problemLevel;

    @Getter
    @NotNull
    @Min(GameSetting.MIN_USER_COUNT_VALUE)
    @Max(GameSetting.MAX_USER_COUNT_VALUE)
    private Integer maxUserCount;

    @Getter
    @NotNull
    @Min(GameSetting.MIN_SUBMISSION_COUNT_VALUE)
    @Max(GameSetting.MAX_SUBMISSION_COUNT_VALUE)
    private Integer maxSubmitCount;

    @Getter
    @NotNull
    @Min(GameSetting.MIN_LIMIT_TIME_VALUE)
    @Max(GameSetting.MAX_LIMIT_TIME_VALUE)
    private Long limitTime;

    public ProgrammingLanguage getProgrammingLanguage() {
        return ProgrammingLanguageManager.getLanguageFromName(language);
    }

    public ProblemLevel getProblemLevel() {
        return ProblemLevelManager.getProblemLevelFromName(problemLevel);
    }
}
