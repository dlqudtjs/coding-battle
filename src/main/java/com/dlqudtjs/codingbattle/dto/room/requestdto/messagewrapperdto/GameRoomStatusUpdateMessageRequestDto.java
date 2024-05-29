package com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto;

import static com.dlqudtjs.codingbattle.common.exception.CommonErrorCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import lombok.Getter;

@Getter
public class GameRoomStatusUpdateMessageRequestDto {
    private String hostId;
    private String title;
    private String password;
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
        if (hostId == null || title == null || password == null || language == null ||
                problemLevel == null || maxUserCount == null || maxSubmitCount == null || limitTime == null) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // problemLevel 이 GameSetting에 설정된 범위를 벗어나면 예외 발생
        if (checkRange(problemLevel, GameSetting.MIN_PROBLEM_LEVEL.getValue(),
                GameSetting.MAX_PROBLEM_LEVEL.getValue())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // maxUserCount가 GameSetting에 설정된 범위를 벗어나면 예외 발생
        if (checkRange(maxUserCount, GameSetting.MIN_USER_COUNT.getValue(), GameSetting.MAX_USER_COUNT.getValue())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // maxSubmitCount가 GameSetting에 설정된 범위를 벗어나면 예외 발생
        if (checkRange(maxSubmitCount, GameSetting.MIN_SUBMISSION_COUNT.getValue(),
                GameSetting.MAX_SUBMISSION_COUNT.getValue())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // limitTime이 GameSetting에 설정된 범위를 벗어나면 예외 발생
        if (checkRange(limitTime, GameSetting.MIN_LIMIT_TIME.getValue(), GameSetting.MAX_LIMIT_TIME.getValue())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        if (!ProgrammingLanguage.isContains(language)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }
    }

    private boolean checkRange(int value, int min, int max) {
        return min > value || value > max;
    }

    private boolean checkRange(Long value, int min, int max) {
        return min > value || value > max;
    }
}
