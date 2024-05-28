package com.dlqudtjs.codingbattle.entity.room;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
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
    private Long limitTime;
    private ConcurrentHashMap<String, RoomUserStatus> roomUserStatusMap;

    public void startGame() {
        isStarted = true;
    }

    public void addUser(UserInfo userInfo) {
        User user = userInfo.getUser();
        WebSocketSession session = WebsocketSessionHolder.getSessionFromUserId(user.getUserId());
        roomUserStatusMap.put(user.getUserId(), new RoomUserStatus(userInfo, session));
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public void removeUser(String userId) {
        roomUserStatusMap.remove(userId);
    }

    public Boolean isFull() {
        return roomUserStatusMap.size() >= maxUserCount;
    }

    public Boolean isHost(String userId) {
        return hostId.equals(userId);
    }

    public Boolean isExistUser(String userId) {
        return roomUserStatusMap.containsKey(userId);
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

    public RoomUserStatus updateGameRoomUserStatus(
            GameRoomUserStatusUpdateRequestDto requestDto) {

        RoomUserStatus userStatus = getUserStatus(requestDto.getUserId());
        userStatus.updateStatus(requestDto.getIsReady(),
                ProgrammingLanguage.valueOf(requestDto.getLanguage().toUpperCase()));

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
        return roomUserStatusMap.values().stream()
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

}
