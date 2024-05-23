package com.dlqudtjs.codingbattle.entity.room;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.entity.user.UserSetting;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

@Getter
@RequiredArgsConstructor
public class GameRoomUserStatus {
    String userId;
    WebSocketSession session;
    Boolean isReady;
    ProgrammingLanguage useLanguage;

    public GameRoomUserStatus(UserSetting userSetting, WebSocketSession session) {
        this.userId = userSetting.getUser().getUserId();
        this.session = session;
        this.isReady = false;
        this.useLanguage = ProgrammingLanguage.valueOf(userSetting.getLanguage().getName().toUpperCase());
    }

    public void updateStatus(Boolean isReady, String language) {
        this.isReady = isReady;
        this.useLanguage = ProgrammingLanguage.valueOf(language.toUpperCase());
    }
}
