package com.dlqudtjs.codingbattle.model.room;

import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Builder
@AllArgsConstructor
public class WaitRoom {

    private String hostId;
    private String title;
    private String password;
    private String language;
    private int problemLevel;
    private int maxUserCount;
    private int maxSummitCount;
    private int limitTime;
    private ConcurrentHashMap<String, WebSocketSession> userMap;

    public void addUser(String userId) {
        WebSocketSession session = WebsocketSessionHolder.getSessionFromUserId(userId);
        userMap.put(userId, session);
    }
}
