package com.dlqudtjs.codingbattle.dto.room.requestdto;

import static com.dlqudtjs.codingbattle.common.exception.CommonErrorCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomCreateRequestDto {

    @NotBlank
    private String hostId;

    @NotBlank
    private String title;

    private String password;

    @NotBlank
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
