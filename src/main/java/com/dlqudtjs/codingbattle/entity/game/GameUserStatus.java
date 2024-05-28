package com.dlqudtjs.codingbattle.entity.game;

import com.dlqudtjs.codingbattle.entity.user.User;
import lombok.Builder;

@Builder
public class GameUserStatus {
    private User user;
    private Boolean isSubmitDone;

    public Boolean getIsSubmitDone() {
        return isSubmitDone;
    }

    public Boolean toggleSubmitDone() {
        isSubmitDone = !isSubmitDone;
        return isSubmitDone;
    }
}
