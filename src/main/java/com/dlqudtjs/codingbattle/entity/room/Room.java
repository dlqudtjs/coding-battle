package com.dlqudtjs.codingbattle.entity.room;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.constant.RoomConfig;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.RoomStatusUpdateMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomStatusResponseDto;
import com.dlqudtjs.codingbattle.entity.game.GameRunningConfig;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import com.dlqudtjs.codingbattle.service.session.SessionService;
import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

    public Room(SessionService sessionService) {
        this.roomId = RoomConfig.DEFAULT_ROOM_ID.getValue();
        this.host = User.deafultUser();
        this.title = "default";
        this.password = "";
        this.maxUserCount = GameSetting.DEFAULT_ROOM_MAX_USER_COUNT.getValue();
        this.isStarted = false;
        this.gameRunningConfig = GameRunningConfig.defaultGameRunningConfig();
        this.sessionService = sessionService;
        this.roomUserStatusMap = new ConcurrentHashMap<>();
    }

    public Boolean startGame() {
        if (!canStartGame()) {
            return false;
        }

        setAllUsersToGameStart();

        isStarted = true;
        return true;
    }


    public Room enter(UserInfo userInfo, String password) {
        if (isLocked() && !isMatchPassword(password)) {
            return null;
        }

        User user = userInfo.getUser();
        String sessionId = WebsocketSessionHolder.getSessionIdFromUser(user);

        roomUserStatusMap.put(user, new RoomUserStatus(userInfo, sessionId));
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

    private Boolean isMatchPassword(String password) {
        return this.password.equals(password);
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
        return !password.isBlank();
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
                .filter(roomUserStatus -> !roomUserStatus.getUserInfo().getUser().equals(host))
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

    private Boolean canStartGame() {
        return isAllUserReady() &&
                isUserAndRoomLanguageMatch();
        // 시작 인원 제한
        //                getUserCount() >= GameSetting.GAME_START_MIN_USER_COUNT.getValue();
    }

    private void setAllUsersToGameStart() {
        getUserList().forEach(sessionService::startGame);
    }

    private RoomUserStatus getUserStatus(User user) {
        return roomUserStatusMap.get(user);
    }

    public List<RoomUserStatus> getRoomUserStatusList() {
        return roomUserStatusMap.values().stream()
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

    public Long getRoomId() {
        return roomId;
    }

    public User getHost() {
        return host;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getIsStarted() {
        return isStarted;
    }

    public Integer getMaxUserCount() {
        return maxUserCount;
    }
}
