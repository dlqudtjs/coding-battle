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
    private Integer problemLevel;
    private Integer maxUserCount;
    private Integer maxSummitCount;
    private Integer limitTime;
    private ConcurrentHashMap<String, WaitRoomUserStatus> userMap;

    public void addUser(String userId) {
        WebSocketSession session = WebsocketSessionHolder.getSessionFromUserId(userId);
        userMap.put(userId, new WaitRoomUserStatus(session));
    }

    public void removeUser(String userId) {
        userMap.remove(userId);
    }

    public Boolean isFull() {
        return userMap.size() >= maxUserCount;
    }

    public Boolean isHost(String userId) {
        return hostId.equals(userId);
    }

    public Boolean isExistUser(String userId) {
        return userMap.containsKey(userId);
    }
}
