package com.dlqudtjs.codingbattle.entity.room;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

@Getter
@RequiredArgsConstructor
public class RoomUserStatus {
    UserInfo userInfo;
    WebSocketSession session;
    Boolean isReady;
    ProgrammingLanguage useLanguage;

    public RoomUserStatus(UserInfo userInfo, WebSocketSession session) {
        this.userInfo = userInfo;
        this.session = session;
        this.isReady = false;
        this.useLanguage = userInfo.getUserSetting().getProgrammingLanguage();
    }

    public void updateStatus(Boolean isReady, String language) {
        this.isReady = isReady;
        this.useLanguage = ProgrammingLanguage.valueOf(language.toUpperCase());
    }

    public String getUserId() {
        return userInfo.getUser().getUserId();
    }
}
