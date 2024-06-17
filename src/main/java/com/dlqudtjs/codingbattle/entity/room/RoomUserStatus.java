package com.dlqudtjs.codingbattle.entity.room;

import com.dlqudtjs.codingbattle.entity.user.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import lombok.Getter;

@Getter
public class RoomUserStatus {
    UserInfo userInfo;
    String sessionId;
    Boolean isReady;
    ProgrammingLanguage useLanguage;

    public RoomUserStatus(UserInfo userInfo, String sessionId) {
        this.userInfo = userInfo;
        this.sessionId = sessionId;
        this.isReady = false;
        this.useLanguage = userInfo.getUserSetting().getProgrammingLanguage();
    }

    public void updateStatus(Boolean isReady, ProgrammingLanguage language) {
        this.isReady = isReady;
        this.useLanguage = language;
    }

    public String getUserId() {
        return userInfo.getUser().getUserId();
    }
}
