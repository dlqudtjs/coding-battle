package com.dlqudtjs.codingbattle.entity.room;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.constant.RoomConfig;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.RoomStatusUpdateMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.entity.game.GameRunningConfig;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import com.dlqudtjs.codingbattle.service.session.SessionService;
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
    private final SessionService sessionService;
    private final ConcurrentHashMap<User, RoomUserStatus> roomUserStatusMap;

    public Room(GameRunningConfig gameRunningConfig,
                SessionService sessionService,
                Long roomId,
                User host,
                String title,
                String password,
                Integer maxUserCount) {
        this.gameRunningConfig = gameRunningConfig;
        this.sessionService = sessionService;
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

    public Room enter(UserInfo userInfo) {
        User user = userInfo.getUser();
        WebSocketSession session = WebsocketSessionHolder.getSessionFromUser(user);

        roomUserStatusMap.put(user, new RoomUserStatus(userInfo, session));
        sessionService.enterRoom(user, roomId);

        return this;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public User leave(User user) {
        roomUserStatusMap.remove(user);
        sessionService.leaveRoom(user);

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

    public Room updateRoomStatus(RoomStatusUpdateMessageRequestDto requestDto) {
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

    public RoomUserStatus updateRoomUserStatus(
            RoomUserStatusUpdateRequestDto requestDto, User user) {

        RoomUserStatus userStatus = getUserStatus(user);
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

    private RoomUserStatus getUserStatus(User user) {
        return roomUserStatusMap.get(user);
    }

    public List<RoomUserStatusResponseDto> toRoomUserStatusResponseDto() {
        return roomUserStatusMap.values().stream()
                .map(status -> RoomUserStatusResponseDto.builder()
                        .userId(status.getUserId())
                        .isReady(status.getIsReady())
                        .language(status.getUseLanguage().getLanguageName())
                        .build())
                .toList();
    }

    public RoomStatusResponseDto toRoomStatusResponseDto() {
        return RoomStatusResponseDto.builder()
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

    public static Room defaultRoom() {
        return new Room(GameRunningConfig.defaultGameRunningConfig(),
                null,
                RoomConfig.DEFAULT_ROOM_ID.getValue(),
                User.deafultUser(),
                "default",
                null,
                GameSetting.DEFAULT_ROOM_MAX_USER_COUNT.getValue());
    }
}
