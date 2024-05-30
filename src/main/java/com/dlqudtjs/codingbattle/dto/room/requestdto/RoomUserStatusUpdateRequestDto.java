package com.dlqudtjs.codingbattle.dto.room.requestdto;

import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import lombok.Getter;

@Getter
public class RoomUserStatusUpdateRequestDto {
    private String userId;
    private Boolean isReady;
    private String language;

    public ProgrammingLanguage getLanguage() {
        return ProgrammingLanguage.getLanguage(language);
    }

    public void validate() {
        if (userId == null || isReady == null || language == null) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        if (ProgrammingLanguage.isNotContains(language)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }
    }
}
