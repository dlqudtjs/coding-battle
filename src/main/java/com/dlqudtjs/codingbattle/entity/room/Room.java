package com.dlqudtjs.codingbattle.entity.room;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.GameRoomStatusUpdateMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.entity.game.GameRunningConfig;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class Room {
    private Long roomId;
    private final User host;
    private String title;
    private String password;
    private Integer maxUserCount;
    private Boolean isStarted;
    private final GameRunningConfig gameRunningConfig;
    private final ConcurrentHashMap<User, RoomUserStatus> roomUserStatusMap;

    public Room(GameRunningConfig gameRunningConfig,
                Long roomId,
                User host,
                String title,
                String password,
                Integer maxUserCount) {
        this.gameRunningConfig = gameRunningConfig;
        this.roomId = roomId;
        this.host = host;
        this.title = title;
        this.password = password;
        this.maxUserCount = maxUserCount;
        this.isStarted = false;
        this.roomUserStatusMap = new ConcurrentHashMap<>();
    }

    public void startGame() {
        isStarted = true;
    }

    public void enter(UserInfo userInfo) {
        User user = userInfo.getUser();
        WebSocketSession session = WebsocketSessionHolder.getSessionFromUser(user);
        roomUserStatusMap.put(user, new RoomUserStatus(userInfo, session));
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public User leave(User user) {
        roomUserStatusMap.remove(user);
        return user;
    }

    public Boolean isFull() {
        return roomUserStatusMap.size() >= maxUserCount;
    }

    public Boolean isHost(User user) {
        return host.equals(user);
    }

    public Boolean isExistUser(User user) {
        return roomUserStatusMap.containsKey(user);
    }

    public Boolean isStarted() {
        return isStarted;
    }

    public Integer getUserCount() {
        return roomUserStatusMap.size();
    }

    public Boolean isLocked() {
        return password != null;
    }

    public Boolean checkAvailableLanguage(ProgrammingLanguage language) {
        return language.equals(ProgrammingLanguage.DEFAULT) || gameRunningConfig.getLanguage().equals(language);
    }

    public List<User> getUserList() {
        return roomUserStatusMap.values().stream()
                .map(RoomUserStatus::getUserInfo)
                .map(UserInfo::getUser)
                .toList();
    }

    public Boolean isAllUserReady() {
        return roomUserStatusMap.values().stream()
                .allMatch(RoomUserStatus::getIsReady);
    }

    public Room updateGameRoomStatus(GameRoomStatusUpdateMessageRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.password = requestDto.getPassword();
        this.maxUserCount = requestDto.getMaxUserCount();

        gameRunningConfig.updateGameRunningConfig(
                requestDto.getProblemLevel(),
                requestDto.getLanguage(),
                requestDto.getMaxSubmitCount(),
                requestDto.getLimitTime()
        );

        return this;
    }

    public GameRunningConfig getGameRunningConfig() {
        return gameRunningConfig;
    }

    public RoomUserStatus updateGameRoomUserStatus(
            GameRoomUserStatusUpdateRequestDto requestDto) {

        RoomUserStatus userStatus = getUserStatus(requestDto.getUserId());
        userStatus.updateStatus(requestDto.getIsReady(), requestDto.getLanguage());

        return userStatus;
    }


    public Boolean isUserAndRoomLanguageMatch() {
        ProgrammingLanguage language = gameRunningConfig.getLanguage();

        if (language.equals(ProgrammingLanguage.DEFAULT)) {
            return true;
        }

        return roomUserStatusMap.values().stream()
                .allMatch(user -> user.getUseLanguage().equals(language));
    }

    public void initRoomUserStatus() {
        roomUserStatusMap.values().forEach(
                status -> status.updateStatus(false, status.getUseLanguage())
        );
    }

    private RoomUserStatus getUserStatus(String userId) {
        return roomUserStatusMap.get(userId);
    }

    public List<GameRoomUserStatusResponseDto> toGameRoomUserStatusResponseDto() {
        return roomUserStatusMap.values().stream()
                .map(status -> GameRoomUserStatusResponseDto.builder()
                        .userId(status.getUserId())
                        .isReady(status.getIsReady())
                        .language(status.getUseLanguage().getLanguageName())
                        .build())
                .toList();
    }

    public GameRoomStatusResponseDto toGameRoomStatusResponseDto() {
        return GameRoomStatusResponseDto.builder()
                .roomId(roomId)
                .hostId(host.getUserId())
                .title(title)
                .language(gameRunningConfig.getLanguage().getLanguageName())
                .isLocked(isLocked())
                .isStarted(isStarted)
                .problemLevel(gameRunningConfig.getProblemLevel())
                .maxUserCount(maxUserCount)
                .maxSubmitCount(gameRunningConfig.getMaxSubmitCount())
                .limitTime(gameRunningConfig.getLimitTime())
                .build();
    }
}
