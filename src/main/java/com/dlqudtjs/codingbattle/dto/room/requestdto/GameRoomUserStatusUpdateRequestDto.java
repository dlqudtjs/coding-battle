package com.dlqudtjs.codingbattle.dto.room.requestdto;

import static com.dlqudtjs.codingbattle.common.exception.CommonErrorCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import lombok.Getter;

@Getter
public class GameRoomUserStatusUpdateRequestDto {
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

        if (!ProgrammingLanguage.isContains(language)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }
    }
}
