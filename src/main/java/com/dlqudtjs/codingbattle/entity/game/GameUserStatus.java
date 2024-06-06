package com.dlqudtjs.codingbattle.entity.game;

import com.dlqudtjs.codingbattle.entity.user.User;
import lombok.Builder;
import lombok.Getter;

@Builder
public class GameUserStatus {
    @Getter
    private User user;
    private Boolean isSurrender;

    public Boolean isSurrender() {
        return isSurrender;
    }

    public User surrender() {
        isSurrender = true;
        return user;
    }
}
