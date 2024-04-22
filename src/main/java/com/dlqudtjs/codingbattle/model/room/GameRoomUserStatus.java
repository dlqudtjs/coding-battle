package com.dlqudtjs.codingbattle.model.room;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class GameRoomUserStatus {

    String userId;
    WebSocketSession session;
    Boolean isReady;
    ProgrammingLanguage useLanguage;

    public GameRoomUserStatus(String userId, WebSocketSession session) {
        this.userId = userId;
        this.session = session;
        this.isReady = false;
        this.useLanguage = ProgrammingLanguage.JAVA;
    }
}