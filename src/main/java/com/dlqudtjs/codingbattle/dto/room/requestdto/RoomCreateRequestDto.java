package com.dlqudtjs.codingbattle.dto.room.requestdto;

import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomCreateRequestDto {

    @NotNull
    private String hostId;

    @NotNull
    private String title;

    @NotNull
    private String password;

    @NotNull
    private String language;

    private Integer problemLevel;

    private Integer maxUserCount;

    private Integer maxSubmitCount;

    private Long limitTime;

    public ProgrammingLanguage getLanguage() {
        return ProgrammingLanguage.getLanguage(language);
    }

    public ProblemLevelType getProblemLevel() {
        return ProblemLevelType.getProblemLevel(problemLevel);
    }

    public void validate() {
        if (ProgrammingLanguage.isNotContains(language)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        if (ProblemLevelType.isNotContains(problemLevel)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }
    }
}
