package com.dlqudtjs.codingbattle.entity.game;

import com.dlqudtjs.codingbattle.entity.user.User;
import lombok.Builder;

@Builder
public class GameUserStatus {
    private User user;
    private Boolean isSurrender;

    public Boolean isSurrender() {
        return isSurrender;
    }

    public User surrender() {
        isSurrender = true;
        return user;
    }

    public User getUser() {
        return user;
    }
}
