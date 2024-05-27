package com.dlqudtjs.codingbattle.entity.game;

import com.dlqudtjs.codingbattle.entity.user.User;
import lombok.experimental.Delegate;

public class Winner extends User {

    @Delegate
    private User user;
    private String code;

    public Winner(User user, String code) {
        this.user = user;
        this.code = code;
    }
}
