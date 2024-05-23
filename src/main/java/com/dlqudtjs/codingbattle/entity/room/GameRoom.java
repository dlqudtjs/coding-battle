package com.dlqudtjs.codingbattle.entity.room;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserSetting;
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
    private Long roomId;
    private String hostId;
    private String title;
    private String password;
    private ProgrammingLanguage language;
    private Boolean isStarted;
    private ProblemLevelType problemLevel;
    private Integer maxUserCount;
    private Integer maxSubmitCount;
    private Integer limitTime;
    private ConcurrentHashMap<String, GameRoomUserStatus> userMap;

    public void startGame() {
        isStarted = true;
    }

    public void addUser(UserSetting userSetting) {
        User user = userSetting.getUser();
        WebSocketSession session = WebsocketSessionHolder.getSessionFromUserId(user.getUserId());
        userMap.put(user.getUserId(), new GameRoomUserStatus(userSetting, session));
    }

    public void setRoomId(Long roomId) {
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

    public Boolean isStarted() {
        return isStarted;
    }

    public Integer getUserCount() {
        return userMap.size();
    }

    public Boolean isLocked() {
        return password != null;
    }

    public List<String> getUserList() {
        return userMap.keySet().stream().toList();
    }

    public Boolean isAllUserReady() {
        return userMap.values().stream()
                .allMatch(GameRoomUserStatus::getIsReady);
    }

    public GameRoom updateGameRoomStatus(GameRoomStatusUpdateRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.password = requestDto.getPassword();
        this.language = ProgrammingLanguage.valueOf(requestDto.getLanguage().toUpperCase());
        this.problemLevel = ProblemLevelType.getProblemLevel(requestDto.getProblemLevel());
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

    public Boolean isUserAndRoomLanguageMatch() {
        if (language.equals(ProgrammingLanguage.DEFAULT)) {
            return true;
        }

        return userMap.values().stream()
                .allMatch(user -> user.getUseLanguage().equals(language));
    }

    private GameRoomUserStatus getUserStatus(String userId) {
        return userMap.get(userId);
    }
}
