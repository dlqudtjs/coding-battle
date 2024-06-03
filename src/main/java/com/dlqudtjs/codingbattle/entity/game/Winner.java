package com.dlqudtjs.codingbattle.entity.game;

import com.dlqudtjs.codingbattle.common.constant.MatchingResultType;
import com.dlqudtjs.codingbattle.entity.user.User;

public class Winner extends User {

    private final User user;
    private final MatchingResultType matchingResultType;
    private final String code;

    public Winner(User user, MatchingResultType matchingResultType, String code) {
        this.user = user;
        this.matchingResultType = matchingResultType;
        this.code = code;
    }

    public String getWinnerId() {
        if (user == null) {
            return "";
        }

        return user.getUserId();
    }

    public String getCode() {
        return code;
    }

    public MatchingResultType getMatchingResultType() {
        return matchingResultType;
    }
}
