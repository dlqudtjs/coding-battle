package com.dlqudtjs.codingbattle.entity.game;

import com.dlqudtjs.codingbattle.entity.user.User;
import lombok.experimental.Delegate;

public class Winner extends User {

    @Delegate
    private final User user;
    private final String code;

    public Winner(User user, String code) {
        this.user = user;
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
