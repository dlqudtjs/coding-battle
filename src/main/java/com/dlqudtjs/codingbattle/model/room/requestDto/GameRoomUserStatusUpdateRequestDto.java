package com.dlqudtjs.codingbattle.model.room.requestDto;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class GameRoomUserStatusUpdateRequestDto {
    private String userId;
    private Boolean isReady;
    private String language;

    public void validate() {
        if (userId == null || isReady == null || language == null) {
            throw new Custom4XXException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }

        if (ProgrammingLanguage.isNotContains(language)) {
            throw new Custom4XXException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }
    }
}
