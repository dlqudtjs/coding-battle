package com.dlqudtjs.codingbattle.entity.game;

import com.dlqudtjs.codingbattle.common.constant.MatchingResultType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.entity.submit.Submit;
import com.dlqudtjs.codingbattle.entity.user.User;
import lombok.Getter;

public class Winner {

    private final User user;
    @Getter
    private final MatchingResultType matchingResultType;
    private final Submit submit;

    public Winner(User user, MatchingResultType matchingResultType, Submit submit) {
        this.user = user;
        this.matchingResultType = matchingResultType;
        this.submit = submit;
    }

    public String getUserId() {
        return user.getUserId();
    }

    public String getCode() {
        return submit.getCode();
    }

    public ProgrammingLanguage getLanguage() {
        return submit.getLanguage();
    }
}
