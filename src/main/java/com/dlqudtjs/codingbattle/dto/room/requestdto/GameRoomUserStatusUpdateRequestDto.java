package com.dlqudtjs.codingbattle.dto.room.requestdto;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.common.exception.CommonErrorCode;
import lombok.Getter;

@Getter
public class GameRoomUserStatusUpdateRequestDto {
    private String userId;
    private Boolean isReady;
    private String language;

    public void validate() {
        if (userId == null || isReady == null || language == null) {
            throw new Custom4XXException(CommonErrorCode.INVALID_INPUT_VALUE.getMessage());
        }

        if (ProgrammingLanguage.isNotContains(language)) {
            throw new Custom4XXException(CommonErrorCode.INVALID_INPUT_VALUE.getMessage());
        }
    }
}
