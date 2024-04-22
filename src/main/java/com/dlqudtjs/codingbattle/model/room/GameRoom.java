package com.dlqudtjs.codingbattle.model.room;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Builder
@AllArgsConstructor
public class WaitRoom {

    private Integer roomId;
    private String hostId;
    private String title;
    private String password;
    private ProgrammingLanguage language;
    private Integer problemLevel;
    private Integer maxUserCount;
    private Integer maxSubmitCount;
    private Integer limitTime;
    private ConcurrentHashMap<String, GameRoomUserStatus> userMap;

    public void addUser(String userId) {
        WebSocketSession session = WebsocketSessionHolder.getSessionFromUserId(userId);
        userMap.put(userId, new GameRoomUserStatus(userId, session));
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
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

    public Boolean isLocked() {
        return password != null;
    }

    public List<GameRoomUserStatus> getUserStatusList() {
        return List.copyOf(userMap.values());
    }
}
