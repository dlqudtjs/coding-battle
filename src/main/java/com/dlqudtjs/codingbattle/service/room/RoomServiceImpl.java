package com.dlqudtjs.codingbattle.service.room;

import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;
import static com.dlqudtjs.codingbattle.common.constant.code.RoomConfigCode.INVALID_REQUEST;
import static com.dlqudtjs.codingbattle.common.constant.code.SocketConfigCode.NOT_CONNECT_USER;

import com.dlqudtjs.codingbattle.common.constant.MessageType;
import com.dlqudtjs.codingbattle.common.constant.RoomConfig;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.common.exception.CustomSocketException;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomCreateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomEnterRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.RoomStatusUpdateMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.game.GameRunningConfig;
import com.dlqudtjs.codingbattle.entity.room.LeaveRoomUserStatus;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.room.RoomUserStatus;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import com.dlqudtjs.codingbattle.service.session.SessionService;
import com.dlqudtjs.codingbattle.service.user.UserService;
import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
import jakarta.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final SessionService sessionService;
    private final UserService userService;
    private final ConcurrentHashMap<Long, Room> roomMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void initDefaultRoom() {
        roomMap.put(RoomConfig.DEFAULT_ROOM_ID.getValue(), new Room(sessionService));
    }

    @Override
    public Room create(RoomCreateRequestDto requestDto, User host) {
        validateCreateRoomRequest(requestDto, host);

        Long newRoomId = getNewRoomId();

        // 방 생성
        Room createdRoom = new Room(createGameRunningConfig(requestDto, newRoomId),
                sessionService,
                newRoomId,
                host,
                requestDto.getTitle(),
                requestDto.getPassword(),
                requestDto.getMaxUserCount());

        // 방 입장
        createdRoom.enter(userService.getUserInfo(host.getUserId()), requestDto.getPassword());
        roomMap.put(newRoomId, createdRoom);
        return createdRoom;
    }

    @Override
    public Room enter(RoomEnterRequestDto requestDto) {
        User user = validateEnterRoomRequest(requestDto);

        // 방 입장
        Room joinedRoom = roomMap.get(requestDto.getRoomId());
        return joinedRoom.enter(userService.getUserInfo(user.getUserId()), requestDto.getPassword());
    }

    @Override
    public LeaveRoomUserStatus leave(Long roomId, User user) {
        // 아무 방에 들어가 있지 않은 경우
        if (roomId.equals(RoomConfig.NO_ROOM_ID.getValue())) {
            return LeaveRoomUserStatus.builder()
                    .roomId(roomId)
                    .user(user)
                    .isHost(false)
                    .build();
        }

        validateLeaveRoomRequest(roomId, user);

        Room room = roomMap.get(roomId);

        // 방을 삭제해야 된다면 삭제하기 전 방에 있는 모든 유저 leaveRoom
        if (room.isHost(user)) {
            leaveAllUserInRoom(roomId);
            roomMap.remove(roomId);
        } else {
            // 방장이 아닐 경우 방을 나가고, 유저의 상태를 default 으로 변경
            room.leave(user);
            enterDefaultRoom(user);
        }

        return LeaveRoomUserStatus.builder()
                .roomId(roomId)
                .user(user)
                .isHost(room.isHost(user))
                .build();
    }

    @Override
    public Room getRoom(Long roomId) {
        if (!roomMap.containsKey(roomId)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        return roomMap.get(roomId);
    }

    @Override
    public Room start(Long roomId, User user) {
        Room room = roomMap.get(roomId);

        if (!room.isHost(user)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        if (!room.startGame()) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        return room;
    }

    @Override
    public Boolean isExistUserInRoom(User user, Long roomId) {
        return roomMap.get(roomId).isExistUser(user);
    }

    @Override
    public Boolean isExistRoom(Long roomId) {
        return roomMap.containsKey(roomId);
    }

    @Override
    public Boolean isStartedGame(Long roomId) {
        return roomMap.get(roomId).isStarted();
    }

    @Override
    public void logout(User user) {
        Long roomId = sessionService.getRoomIdFromUser(user);
        getRoom(roomId).leave(user);
    }

    @Override
    public List<Room> getRoomList() {
        return List.copyOf(roomMap.values());
    }

    /*
     방에 보내는 메시지의 유효성을 검사하고 SendToRoomMessageResponseDto로 변환하는 메서드
     */
    @Override
    public SendToRoomMessageResponseDto parseMessage(
            Long roomId,
            String sessionId,
            SendToRoomMessageRequestDto requestDto) {
        User user = userService.getUser(requestDto.getSenderId());
        validateRoomExistence(roomId);
        validateUserSession(user);
        validateUserInRoom(roomId, user);

        String senderSessionId = WebsocketSessionHolder.getSessionIdFromUser(user);

        // Sender의 세션 아이디와 요청한 세션 아이디가 일치하지 않으면
        if (!senderSessionId.equals(sessionId)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        return SendToRoomMessageResponseDto.builder()
                .messageType(MessageType.USER.getMessageType())
                .senderId(user.getUserId())
                .message(requestDto.getMessage())
                .sendTime(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    @Override
    public Room updateRoomStatus(
            Long roomId, String sessionId, RoomStatusUpdateMessageRequestDto requestDto) {
        validateUpdateRoomStatusRequest(roomId, sessionId, requestDto);

        return roomMap.get(roomId).updateRoomStatus(requestDto);
    }

    @Override
    public RoomUserStatus updateRoomUserStatus(
            Long roomId,
            String sessionId,
            RoomUserStatusUpdateRequestDto requestDto) {
        validateUpdateRoomUserStatusRequest(roomId, sessionId, requestDto);

        Room room = roomMap.get(roomId);
        User user = userService.getUser(requestDto.getUserId());

        return room.updateRoomUserStatus(requestDto, user);
    }

    private Boolean isUserInGame(User user) {
        return sessionService.isUserInGame(user);
    }

    // 방에 있는 모든 유저 leave 하는 메서드
    private void leaveAllUserInRoom(Long roomId) {
        Room room = roomMap.get(roomId);

        room.getUserList().forEach(user -> {
            room.leave(user);
            enterDefaultRoom(user);
        });
    }

    private void validateUpdateRoomUserStatusRequest(
            Long roomId,
            String sessionId,
            RoomUserStatusUpdateRequestDto requestDto) {
        Room room = roomMap.get(roomId);
        User user = WebsocketSessionHolder.getUserFromSessionId(sessionId);

        validateRoomExistence(roomId);

        // 세션 아이디와 요청한 유저 아이디가 일치하지 않으면
        if (!requestDto.getUserId().equals(user.getUserId())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // 밤에 설정된 language외 다른 language로 변경하려고 하면
        if (room.checkAvailableLanguage(requestDto.getLanguage())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }
    }

    private GameRunningConfig createGameRunningConfig(RoomCreateRequestDto requestDto, Long newRoomId) {
        return new GameRunningConfig(newRoomId,
                requestDto.getProblemLevel(),
                requestDto.getLanguage(),
                requestDto.getMaxSubmitCount(),
                requestDto.getLimitTime());
    }

    private Boolean isFullRoom(Long roomId) {
        return roomMap.get(roomId).isFull();
    }

    private void validateUpdateRoomStatusRequest(
            Long roomId, String sessionId, RoomStatusUpdateMessageRequestDto requestDto) {
        User socketUser = WebsocketSessionHolder.getUserFromSessionId(sessionId);

        validateRoomExistence(roomId);
        validateUserIsHost(roomId, socketUser);
    }

    private void validateLeaveRoomRequest(Long roomId, User user) {
        validateRoomExistence(roomId);
        validateUserSession(user);
        validateUserInRoom(roomId, user);
    }

    private User validateEnterRoomRequest(RoomEnterRequestDto requestDto) {
        User user = userService.getUser(requestDto.getUserId());

        validateUserSession(user);
        validateRoomExistence(requestDto.getRoomId());

        // 방이 꽉 찼으면
        if (isFullRoom(requestDto.getRoomId())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // 게임 중인 유저가 방에 들어가려고 하면
        if (isUserInGame(user)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        return user;
    }

    private void validateUserIsHost(Long roomId, User user) {
        if (!roomMap.get(roomId).isHost(user)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }
    }

    private void validateCreateRoomRequest(RoomCreateRequestDto requestDto, User user) {
        // userId와 requestDto의 hostId가 일치하지 않으면
        if (!user.getUserId().equals(requestDto.getHostId())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // 요청한 유저가 웹 소켓 세션에 존재하지 않으면
        validateUserSession(user);
    }

    // 유저 세션이 존재하지 않으면
    private void validateUserSession(User user) {
        if (WebsocketSessionHolder.isNotConnected(user)) {
            throw new CustomSocketException(NOT_CONNECT_USER.getMessage());
        }
    }

    // 방이 존재하지 않으면
    private void validateRoomExistence(Long roomId) {
        if (!roomMap.containsKey(roomId)) {
            throw new Custom4XXException(INVALID_REQUEST.getMessage(), INVALID_REQUEST.getStatus());
        }
    }

    // 방에 유저가 존재하지 않으면
    private void validateUserInRoom(Long roomId, User user) {
        if (!isExistUserInRoom(user, roomId)) {
            throw new Custom4XXException(INVALID_REQUEST.getMessage(), INVALID_REQUEST.getStatus());
        }
    }

    private Long getNewRoomId() {
        Long roomId = 1L;

        while (roomMap.containsKey(roomId)) {
            roomId++;
        }

        return roomId;
    }

    private void enterDefaultRoom(User user) {
        UserInfo userInfo = userService.getUserInfo(user.getUserId());
        roomMap.get(RoomConfig.DEFAULT_ROOM_ID.getValue()).enter(userInfo, null);
    }
}
