package com.dlqudtjs.codingbattle.model.room;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.responseDto.GameRoomStatusResponseDto;
import com.dlqudtjs.codingbattle.model.room.responseDto.GameRoomUserStatusResponseDto;
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
public class GameRoom {

    private Integer roomId;
    private String hostId;
    private String title;
    private String password;
    private ProgrammingLanguage language;
    private Boolean isStarted;
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

    public Integer getUserCount() {
        return userMap.size();
    }

    public Boolean isLocked() {
        return password != null;
    }

    public GameRoom updateGameRoomStatus(GameRoomStatusUpdateRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.password = requestDto.getPassword();
        this.language = ProgrammingLanguage.valueOf(requestDto.getLanguage().toUpperCase());
        this.problemLevel = requestDto.getProblemLevel();
        this.maxUserCount = requestDto.getMaxUserCount();
        this.maxSubmitCount = requestDto.getMaxSubmitCount();
        this.limitTime = requestDto.getLimitTime();

        return this;
    }

    public GameRoomUserStatus updateGameRoomUserStatus(
            GameRoomUserStatusUpdateRequestDto requestDto) {

        GameRoomUserStatus userStatus = getUserStatus(requestDto.getUserId());
        userStatus.updateStatus(requestDto.getIsReady(), requestDto.getLanguage());

        return userStatus;
    }

    public GameRoomStatusResponseDto toGameRoomStatusResponseDto() {
        return GameRoomStatusResponseDto.builder()
                .roomId(roomId)
                .hostId(hostId)
                .title(title)
                .language(language.getLanguageName())
                .isLocked(isLocked())
                .isStarted(isStarted)
                .problemLevel(problemLevel)
                .maxUserCount(maxUserCount)
                .maxSubmitCount(maxSubmitCount)
                .limitTime(limitTime)
                .build();
    }

    public List<GameRoomUserStatusResponseDto> toGameRoomUserStatusResponseDto() {
        return userMap.values().stream()
                .map(status -> GameRoomUserStatusResponseDto.builder()
                        .userId(status.getUserId())
                        .isReady(status.getIsReady())
                        .language(status.getUseLanguage().getLanguageName())
                        .build())
                .toList();
    }

    private GameRoomUserStatus getUserStatus(String userId) {
        return userMap.get(userId);
    }
}
