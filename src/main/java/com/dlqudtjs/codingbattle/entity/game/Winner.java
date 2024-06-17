package com.dlqudtjs.codingbattle.entity.game;

import com.dlqudtjs.codingbattle.entity.match.MatchResult;
import com.dlqudtjs.codingbattle.entity.submit.Submit;
import com.dlqudtjs.codingbattle.entity.user.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.entity.user.User;
import lombok.Getter;

public class Winner {

    private final User user;
    @Getter
    private final MatchResult matchResult;
    private final Submit submit;

    public Winner(User user, MatchResult matchResult, Submit submit) {
        this.user = user;
        this.matchResult = matchResult;
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

    public Boolean equalsUser(User user) {
        return this.user.equals(user);
    }
}
