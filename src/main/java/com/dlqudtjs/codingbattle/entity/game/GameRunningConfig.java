package com.dlqudtjs.codingbattle.entity.game;

import static com.dlqudtjs.codingbattle.common.constant.GameSetting.MAX_LIMIT_TIME;
import static com.dlqudtjs.codingbattle.common.constant.GameSetting.MAX_SUBMISSION_COUNT;
import static com.dlqudtjs.codingbattle.common.constant.GameSetting.MIN_LIMIT_TIME;
import static com.dlqudtjs.codingbattle.common.constant.GameSetting.MIN_SUBMISSION_COUNT;
import static com.dlqudtjs.codingbattle.common.exception.CommonErrorCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import java.util.List;
import lombok.Getter;

@Getter
public class GameRunningConfig {
    private final Long roomId;
    private ProblemLevelType problemLevel;
    private ProgrammingLanguage language;
    private Integer maxSubmitCount;
    private Long limitTime;
    private List<ProblemInfo> problemInfoList;

    public GameRunningConfig(Long roomId,
                             ProblemLevelType problemLevel,
                             ProgrammingLanguage language,
                             Integer maxSubmitCount,
                             Long limitTime) {
        validateMaxSubmitCount(maxSubmitCount);
        validateLimitTime(limitTime);

        this.roomId = roomId;
        this.problemLevel = problemLevel;
        this.language = language;
        this.maxSubmitCount = maxSubmitCount;
        this.limitTime = limitTime;
    }

    public void setProblemInfoList(List<ProblemInfo> problemInfoList) {
        this.problemInfoList = problemInfoList;
    }

    public void updateGameRunningConfig(ProblemLevelType problemLevel,
                                        ProgrammingLanguage language,
                                        Integer maxSubmitCount,
                                        Long limitTime) {
        validateMaxSubmitCount(maxSubmitCount);
        validateLimitTime(limitTime);

        this.problemLevel = problemLevel;
        this.language = language;
        this.maxSubmitCount = maxSubmitCount;
        this.limitTime = limitTime;
    }

    private void validateMaxSubmitCount(Integer maxSubmitCount) throws Custom4XXException {
        if (MIN_SUBMISSION_COUNT.getValue() > maxSubmitCount || maxSubmitCount > MAX_SUBMISSION_COUNT.getValue()) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }
    }

    private void validateLimitTime(Long limitTime) throws Custom4XXException {
        if (MIN_LIMIT_TIME.getValue() > limitTime || limitTime > MAX_LIMIT_TIME.getValue()) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }
    }
}
