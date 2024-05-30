package com.dlqudtjs.codingbattle.service.room;

import static com.dlqudtjs.codingbattle.common.exception.CommonErrorCode.INVALID_INPUT_VALUE;
import static com.dlqudtjs.codingbattle.common.exception.game.GameErrorCode.GAME_START_ERROR;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import com.dlqudtjs.codingbattle.common.constant.MessageType;
import com.dlqudtjs.codingbattle.common.constant.RoomConfig;
import com.dlqudtjs.codingbattle.common.constant.code.RoomSuccessCode;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.common.exception.room.CustomRoomException;
import com.dlqudtjs.codingbattle.common.exception.room.RoomErrorCode;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomCreateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomEnterRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.RoomStatusUpdateMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomInfoResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomLeaveUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomUserStatusUpdateMessageResponseDto;
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
    public ResponseDto create(RoomCreateRequestDto requestDto, User host) {
        validateCreateRoomRequest(requestDto, host);

        Long newRoomId = getNewRoomId();

        // 방 생성시 기존 방 나가기 (default 방 포함)
        Long alreadyEnterRoomId = sessionService.getRoomIdFromUser(host);
        Boolean isHost = leaveRoom(alreadyEnterRoomId, host);

        // 방 생성
        Room createdRoom = new Room(createGameRunningConfig(requestDto, newRoomId),
                sessionService,
                newRoomId,
                host,
                requestDto.getTitle(),
                requestDto.getPassword(),
                requestDto.getMaxUserCount());

        // 방 입장
        createdRoom.enter(userService.getUserInfo(host.getUserId()));

        roomMap.put(newRoomId, createdRoom);

        RoomInfoResponseDto roomInfoResponseDto = CreateRoomResponseDto(
                createdRoom,
                RoomLeaveUserStatusResponseDto.builder()
                        .roomId(alreadyEnterRoomId)
                        .userId(host.getUserId())
                        .isHost(isHost)
                        .build()
        );

        return ResponseDto.builder()
                .status(RoomSuccessCode.CREATE_GAME_ROOM_SUCCESS.getStatus())
                .message(RoomSuccessCode.CREATE_GAME_ROOM_SUCCESS.getMessage())
                .data(roomInfoResponseDto)
                .build();
    }

    @Override
    public ResponseDto enter(RoomEnterRequestDto requestDto) {
        User user = validateEnterRoomRequest(requestDto);

        // 방 생성시 기존 방 나가기 (default 방 포함)
        Long alreadyEnterRoomId = sessionService.getRoomIdFromUser(user);
        Boolean isHost = leaveRoom(alreadyEnterRoomId, user);

        // 방 입장
        Room joinedRoom = roomMap.get(requestDto.getRoomId());
        joinedRoom.enter(userService.getUserInfo(user.getUserId()));

        RoomInfoResponseDto roomInfoResponseDto = CreateRoomResponseDto(
                joinedRoom,
                RoomLeaveUserStatusResponseDto.builder()
                        .roomId(alreadyEnterRoomId)
                        .userId(user.getUserId())
                        .isHost(isHost)
                        .build()
        );

        return ResponseDto.builder()
                .status(RoomSuccessCode.JOIN_GAME_ROOM_SUCCESS.getStatus())
                .message(RoomSuccessCode.JOIN_GAME_ROOM_SUCCESS.getMessage())
                .data(roomInfoResponseDto)
                .build();
    }

    @Override
    public LeaveRoomUserStatus leave(Long roomId, User user) {
        validateLeaveRoomRequest(roomId, user);

        Boolean isHost = leaveRoom(roomId, user);
        enterDefaultRoom(user);

        return LeaveRoomUserStatus.builder()
                .roomId(roomId)
                .user(user)
                .isHost(isHost)
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

        if (canStartable(room)) {
            throw new Custom4XXException(GAME_START_ERROR.getMessage(), GAME_START_ERROR.getStatus());
        }

        // GameRoom 상태 변경
        room.startGame();

        // GameRoom 내 유저 상태 변경
        room.getUserList().forEach(sessionService::startGame);

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
    public SendToRoomMessageResponseDto parseMessage(Long roomId, String sessionId,
                                                     SendToRoomMessageRequestDto requestDto) {
        User user = WebsocketSessionHolder.getUserFromSessionId(sessionId);

        validateRoomExistence(roomId);
        validateUserSession(user);
        validateUserInRoom(roomId, user);

        return SendToRoomMessageResponseDto.builder()
                .messageType(MessageType.USER.getMessageType())
                .senderId(user.getUserId())
                .message(requestDto.getMessage())
                .sendTime(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    @Override
    public RoomStatusUpdateMessageResponseDto updateRoomStatus(
            Long roomId, String sessionId, RoomStatusUpdateMessageRequestDto requestDto) {
        validateUpdateRoomStatusRequest(roomId, sessionId, requestDto);

        Room updatedRoom = roomMap.get(roomId).updateRoomStatus(requestDto);

        return RoomStatusUpdateMessageResponseDto.builder()
                .roomStatus(updatedRoom.toRoomStatusResponseDto())
                .build();
    }

    @Override
    public RoomUserStatusUpdateMessageResponseDto updateRoomUserStatus(
            Long roomId, String sessionId,
            RoomUserStatusUpdateRequestDto requestDto) {
        validateUpdateRoomUserStatusRequest(roomId, sessionId, requestDto);

        Room room = roomMap.get(roomId);
        User user = userService.getUser(requestDto.getUserId());
        RoomUserStatus updatedUserStatus = room.updateRoomUserStatus(requestDto, user);

        return RoomUserStatusUpdateMessageResponseDto.builder()
                .updateUserStatus(RoomUserStatusResponseDto.builder()
                        .userId(updatedUserStatus.getUserId())
                        .isReady(updatedUserStatus.getIsReady())
                        .language(updatedUserStatus.getUseLanguage().getLanguageName())
                        .build())
                .build();
    }

    private Room joinRoom(User user, Long roomId) {
        UserInfo userInfo = userService.getUserInfo(user.getUserId());

        sessionService.enterRoom(user, roomId);
        return roomMap.get(roomId).enter(userInfo);
    }

    /*
     * 게임 시작 가능한지 확인하는 메서드
     * 모든 유저가 준비 상태 확인,
     * 방에 있는 모든 유저의 언어가 일치한지 확인,
     * 게임 시작 최소 인원 확인,
     * 이미 게임 중인 유저가 있는지 확인
     */
    public Boolean canStartable(Room room) {
        return room != null &&
                room.isAllUserReady() &&
                room.isUserAndRoomLanguageMatch() &&
                room.getUserCount() >= GameSetting.GAME_START_MIN_USER_COUNT.getValue() &&
                room.getUserList().stream().noneMatch(this::isUserInGame);
    }


    private Boolean isUserInGame(User user) {
        return sessionService.isUserInGame(user);
    }

    /*
     * 방을 나가는 메서드
     * 방장이 아닐 경우 방을 나가고, 유저의 상태를 default 으로 변경
     * 방장일 경우 방을 삭제하고, 방에 있던 유저들의 상태를 default 방으로 변경
     */
    private Boolean leaveRoom(Long roomId, User user) {
        // 아무 방에 들어가 있지 않은 경우
        if (roomId.equals(RoomConfig.NO_ROOM_ID.getValue())) {
            return false;
        }

        Room room = roomMap.get(roomId);

        // 방을 삭제해야 된다면 삭제하기 전 방에 있는 모든 유저 leaveRoom
        if (room.isHost(user)) {
            leaveAllUserInRoom(roomId);
            roomMap.remove(roomId);

            return true;
        }

        room.leave(user);
        return false;
    }

    // 방에 있는 모든 유저 leave 하는 메서드
    private void leaveAllUserInRoom(Long roomId) {
        Room room = roomMap.get(roomId);

        room.getUserList().forEach(user -> {
            room.leave(user);
            enterDefaultRoom(user);
        });
    }

    private RoomInfoResponseDto CreateRoomResponseDto(
            Room room,
            RoomLeaveUserStatusResponseDto leaveUserStatusResponseDto) {
        return RoomInfoResponseDto.builder()
                .roomStatus(room.toRoomStatusResponseDto())
                .leaveUserStatus(leaveUserStatusResponseDto)
                .userStatus(room.toRoomUserStatusResponseDto())
                .build();
    }

    private void validateUpdateRoomUserStatusRequest(
            Long roomId, String sessionId, RoomUserStatusUpdateRequestDto requestDto) {
        Room room = roomMap.get(roomId);
        User user = WebsocketSessionHolder.getUserFromSessionId(sessionId);

        validateRoomExistence(roomId);

        // 세션 아이디와 요청한 유저 아이디가 일치하지 않으면
        if (!requestDto.getUserId().equals(user.getUserId())) {
            throw new CustomRoomException(RoomErrorCode.INVALID_REQUEST.getMessage());
        }

        // 밤에 설정된 language외 다른 language로 변경하려고 하면
        if (room.checkAvailableLanguage(requestDto.getLanguage())) {
            throw new CustomRoomException(RoomErrorCode.INVALID_REQUEST.getMessage());
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
        Room room = roomMap.get(roomId);
        User user = userService.getUser(requestDto.getHostId());
        User socketUser = WebsocketSessionHolder.getUserFromSessionId(sessionId);

        validateRoomExistence(roomId);

        // 방장과 세션 아이디가 일치하지 않으면 (웹 소켓 세션에 존재하지 않으면)
        if (!room.isHost(user)) {
            throw new CustomRoomException(RoomErrorCode.INVALID_REQUEST.getMessage());
        }

        // requestDto의 hostId와 userId가 일치하지 않으면
        if (!user.equals(socketUser)) {
            throw new CustomRoomException(RoomErrorCode.INVALID_REQUEST.getMessage());
        }
    }

    private void validateLeaveRoomRequest(Long roomId, User user) {
        validateRoomExistence(roomId);
        validateUserSession(user);
        validateUserInRoom(roomId, user);

        // 게임이 시작 된 방에서는 roomLeave를 할 수 없음 (gameLeave 가능)
        if (isStartedGame(roomId)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }
    }

    private User validateEnterRoomRequest(RoomEnterRequestDto requestDto) {
        User user = userService.getUser(requestDto.getUserId());

        validateUserSession(user);
        validateRoomExistence(requestDto.getRoomId());

        // 방이 꽉 찼으면
        if (isFullRoom(requestDto.getRoomId())) {
            throw new CustomRoomException(RoomErrorCode.FULL_ROOM.getMessage());
        }

        // 게임 중인 유저가 방에 들어가려고 하면
        if (isUserInGame(user)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        return user;
    }

    private void validateCreateRoomRequest(RoomCreateRequestDto requestDto, User user) {
        // userId와 requestDto의 hostId가 일치하지 않으면
        if (!user.getUserId().equals(requestDto.getHostId())) {
            throw new CustomRoomException(RoomErrorCode.INVALID_REQUEST.getMessage());
        }

        // 요청한 유저가 웹 소켓 세션에 존재하지 않으면
        validateUserSession(user);
    }

    // 유저 세션이 존재하지 않으면
    private void validateUserSession(User user) {
        if (WebsocketSessionHolder.isNotConnected(user)) {
            throw new CustomRoomException(RoomErrorCode.NOT_CONNECT_USER.getMessage());
        }
    }

    // 방이 존재하지 않으면
    private void validateRoomExistence(Long roomId) {
        if (!roomMap.containsKey(roomId)) {
            throw new CustomRoomException(RoomErrorCode.NOT_EXIST_ROOM.getMessage());
        }
    }

    // 방에 유저가 존재하지 않으면
    private void validateUserInRoom(Long roomId, User user) {
        if (!isExistUserInRoom(user, roomId)) {
            throw new CustomRoomException(RoomErrorCode.NOT_EXIST_USER_IN_ROOM.getMessage());
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
        roomMap.get(RoomConfig.DEFAULT_ROOM_ID.getValue()).enter(userInfo);
    }
}
