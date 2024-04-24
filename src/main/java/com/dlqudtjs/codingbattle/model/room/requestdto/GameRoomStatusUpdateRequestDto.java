package com.dlqudtjs.codingbattle.model.room.requestdto;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class GameRoomStatusUpdateRequestDto {
    private String hostId;
    private String title;
    private String password;
    private String language;
    private Integer problemLevel;
    private Integer maxUserCount;
    private Integer maxSubmitCount;
    private Integer limitTime;

    public void validate() {
        if (hostId == null || title == null || password == null || language == null ||
                problemLevel == null || maxUserCount == null || maxSubmitCount == null || limitTime == null) {
            throw new Custom4XXException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }

        // problemLevel 이 GameSetting에 설정된 범위를 벗어나면 예외 발생
        if (checkRange(problemLevel, GameSetting.MIN_PROBLEM_LEVEL.getValue(),
                GameSetting.MAX_PROBLEM_LEVEL.getValue())) {
            throw new Custom4XXException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }

        // maxUserCount가 GameSetting에 설정된 범위를 벗어나면 예외 발생
        if (checkRange(maxUserCount, GameSetting.MIN_USER_COUNT.getValue(), GameSetting.MAX_USER_COUNT.getValue())) {
            throw new Custom4XXException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }

        // maxSubmitCount가 GameSetting에 설정된 범위를 벗어나면 예외 발생
        if (checkRange(maxSubmitCount, GameSetting.MIN_SUBMISSION_COUNT.getValue(),
                GameSetting.MAX_SUBMISSION_COUNT.getValue())) {
            throw new Custom4XXException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }

        // limitTime이 GameSetting에 설정된 범위를 벗어나면 예외 발생
        if (checkRange(limitTime, GameSetting.MIN_LIMIT_TIME.getValue(), GameSetting.MAX_LIMIT_TIME.getValue())) {
            throw new Custom4XXException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }

        if (ProgrammingLanguage.isNotContains(language)) {
            throw new Custom4XXException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }
    }

    private boolean checkRange(int value, int min, int max) {
        return min > value || value > max;
    }
}
