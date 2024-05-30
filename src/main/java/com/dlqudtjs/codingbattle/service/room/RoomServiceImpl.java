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
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.GameRoomStatusUpdateMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomInfoResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomLeaveUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomListResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomUserStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.game.GameRunningConfig;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.room.RoomUserStatus;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import com.dlqudtjs.codingbattle.repository.socket.room.RoomRepository;
import com.dlqudtjs.codingbattle.service.session.SessionService;
import com.dlqudtjs.codingbattle.service.user.UserService;
import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final SessionService sessionService;
    private final UserService userService;

    @Override
    public ResponseDto create(GameRoomCreateRequestDto requestDto, User host) {
        validateCreateGameRoomRequest(requestDto, host);

        // 방 생성시 기존 방 나가기 (default 방 포함)
        Long alreadyEnterRoomId = sessionService.getRoomIdFromUser(host);
        GameRoomLeaveUserStatusResponseDto leaveUserStatusResponseDto =
                leaveRoom(alreadyEnterRoomId, host);

        Long newRoomId = roomRepository.getNewRoomId();
        GameRunningConfig gameRunningConfig = createGameRunningConfig(requestDto, newRoomId);

        // 방 번호 생성과 매핑
        Room createdRoom = roomRepository.save(
                new Room(gameRunningConfig,
                        newRoomId,
                        host,
                        requestDto.getTitle(),
                        requestDto.getPassword(),
                        requestDto.getMaxUserCount()), newRoomId);

        // 유저의 세션 상태 변경
        joinRoom(host, newRoomId);

        GameRoomInfoResponseDto gameRoomInfoResponseDto = CreateGameRoomResponseDto(
                createdRoom,
                leaveUserStatusResponseDto
        );

        return ResponseDto.builder()
                .status(RoomSuccessCode.CREATE_GAME_ROOM_SUCCESS.getStatus())
                .message(RoomSuccessCode.CREATE_GAME_ROOM_SUCCESS.getMessage())
                .data(gameRoomInfoResponseDto)
                .build();
    }

    @Override
    public ResponseDto enter(Long roomId, User user) {
        validateEnterGameRoomRequest(roomId, user);

        Long alreadyEnterRoomId = sessionService.getRoomIdFromUser(user);
        // 이미 입장한 방에 다시 입장할 때 예외발생
        if (alreadyEnterRoomId.equals(roomId)) {
            throw new CustomRoomException(RoomErrorCode.SAME_USER_IN_ROOM.getMessage());
        }

        // 기존 방 나가기 (default 방 포함)
        GameRoomLeaveUserStatusResponseDto leaveUserStatusResponseDto = null;
        if (!alreadyEnterRoomId.equals(RoomConfig.NO_ROOM_ID.getValue())) {
            leaveUserStatusResponseDto = leaveRoom(alreadyEnterRoomId, user);
        }

        Room joinedRoom = joinRoom(user, roomId);
        GameRoomInfoResponseDto gameRoomInfoResponseDto = CreateGameRoomResponseDto(
                joinedRoom,
                leaveUserStatusResponseDto
        );

        return ResponseDto.builder()
                .status(RoomSuccessCode.JOIN_GAME_ROOM_SUCCESS.getStatus())
                .message(RoomSuccessCode.JOIN_GAME_ROOM_SUCCESS.getMessage())
                .data(gameRoomInfoResponseDto)
                .build();
    }

    @Override
    public ResponseDto leave(Long roomId, User user) {
        validateLeaveGameRoomRequest(roomId, user);

        GameRoomLeaveUserStatusResponseDto leaveUserStatusResponseDto = leaveRoom(roomId, user);
        joinRoom(user, RoomConfig.DEFAULT_ROOM_ID.getValue());

        // 상태 변경
        return ResponseDto.builder()
                .status(RoomSuccessCode.LEAVE_GAME_ROOM_SUCCESS.getStatus())
                .message(RoomSuccessCode.LEAVE_GAME_ROOM_SUCCESS.getMessage())
                .data(leaveUserStatusResponseDto)
                .build();
    }

    @Override
    public Room getRoom(Long roomId) {
        if (!roomRepository.isExistRoom(roomId)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        return roomRepository.getGameRoom(roomId);
    }

    @Override
    public Room start(Long roomId, User user) {
        Room room = roomRepository.getGameRoom(roomId);

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
    public Boolean isExistUserInRoom(Long roomId, User user) {
        return roomRepository.isExistUserInRoom(user, roomId);
    }

    @Override
    public Boolean isExistRoom(Long roomId) {
        return roomRepository.isExistRoom(roomId);
    }

    @Override
    public Boolean isStartedGame(Long roomId) {
        return roomRepository.isStartedGame(roomId);
    }

    @Override
    public void logout(User user) {
        Long roomId = sessionService.getRoomIdFromUser(user);
        leaveRoom(roomId, user);

        // default 방도 나가기
        roomRepository.leave(RoomConfig.DEFAULT_ROOM_ID.getValue(), user);
    }

    @Override
    public ResponseDto getRoomList() {
        List<Room> roomList = roomRepository.getGameRoomList();

        List<GameRoomListResponseDto> responseDtoList = roomList.stream()
                .map(room -> GameRoomListResponseDto.builder()
                        .roomId(room.getRoomId())
                        .hostId(room.getHost().getUserId())
                        .title(room.getTitle())
                        .language(room.getGameRunningConfig().getLanguage().getLanguageName())
                        .isLocked(room.isLocked())
                        .isStarted(room.getIsStarted())
                        .problemLevel(room.getGameRunningConfig().getProblemLevel())
                        .maxUserCount(room.getMaxUserCount())
                        .maxSubmitCount(room.getGameRunningConfig().getMaxSubmitCount())
                        .limitTime(room.getGameRunningConfig().getLimitTime())
                        .countUsersInRoom(room.getUserCount())
                        .build())
                .toList();

        return ResponseDto.builder()
                .status(RoomSuccessCode.GET_GAME_ROOM_LIST_SUCCESS.getStatus())
                .message(RoomSuccessCode.GET_GAME_ROOM_LIST_SUCCESS.getMessage())
                .data(responseDtoList)
                .build();
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
    public GameRoomStatusUpdateMessageResponseDto updateGameRoomStatus(
            Long roomId, String sessionId, GameRoomStatusUpdateMessageRequestDto requestDto) {
        Room room = validateUpdateGameRoomStatusRequest(roomId, sessionId, requestDto);

        Room updatedRoom = roomRepository.updateGameRoomStatus(
                roomId,
                room.updateGameRoomStatus(requestDto)
        );

        return GameRoomStatusUpdateMessageResponseDto.builder()
                .roomStatus(updatedRoom.toGameRoomStatusResponseDto())
                .build();
    }

    @Override
    public GameRoomUserStatusUpdateMessageResponseDto updateGameRoomUserStatus(
            Long roomId, String sessionId,
            GameRoomUserStatusUpdateRequestDto requestDto) {
        validateUpdateGameRoomUserStatusRequest(roomId, sessionId, requestDto);

        Room room = roomRepository.getGameRoom(roomId);
        User user = userService.getUser(requestDto.getUserId());
        RoomUserStatus updatedUserStatus = room.updateGameRoomUserStatus(requestDto, user);

        return GameRoomUserStatusUpdateMessageResponseDto.builder()
                .updateUserStatus(GameRoomUserStatusResponseDto.builder()
                        .userId(updatedUserStatus.getUserId())
                        .isReady(updatedUserStatus.getIsReady())
                        .language(updatedUserStatus.getUseLanguage().getLanguageName())
                        .build())
                .build();
    }

    private Room joinRoom(User user, Long roomId) {
        UserInfo userInfo = userService.getUserInfo(user.getUserId());

        sessionService.enterRoom(user, roomId);
        return roomRepository.join(userInfo, roomId);
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
    private GameRoomLeaveUserStatusResponseDto leaveRoom(Long roomId, User user) {
        Room room = roomRepository.getGameRoom(roomId);

        // 방을 삭제해야 된다면 삭제하기 전 방에 있는 모든 유저 leaveRoom
        if (room.isHost(user)) {
            // 방에 모든 유저 세션 상태 변경
            leaveAllUserInRoom(roomId, user);
        }

        // 방 나감 (Repository 에서 유저가 방장이라면 방 삭제함)
        roomRepository.leave(roomId, user);
        // 나간 유저 세션 상태 변경
        sessionService.leaveRoom(user);

        // 방에 있는 모든 유저에게 나간 유저의 상태를 알림
        return GameRoomLeaveUserStatusResponseDto.builder()
                .roomId(roomId)
                .userId(user.getUserId())
                .isHost(room.isHost(user))
                .build();
    }

    // 방에 있는 모든 유저 leaveRoom 하는 메서드
    private void leaveAllUserInRoom(Long roomId, User host) {
        Room room = roomRepository.getGameRoom(roomId);

        room.getUserList().stream()
                .filter(user -> !host.equals(user))
                .forEach(user -> {
                    sessionService.leaveRoom(user);
                    joinRoom(user, RoomConfig.DEFAULT_ROOM_ID.getValue());
                });
    }

    private GameRoomInfoResponseDto CreateGameRoomResponseDto(
            Room room,
            GameRoomLeaveUserStatusResponseDto leaveUserStatusResponseDto) {
        return GameRoomInfoResponseDto.builder()
                .roomStatus(room.toGameRoomStatusResponseDto())
                .leaveUserStatus(leaveUserStatusResponseDto)
                .userStatus(room.toGameRoomUserStatusResponseDto())
                .build();
    }

    private void validateUpdateGameRoomUserStatusRequest(
            Long roomId, String sessionId, GameRoomUserStatusUpdateRequestDto requestDto) {
        Room room = roomRepository.getGameRoom(roomId);
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

    private GameRunningConfig createGameRunningConfig(GameRoomCreateRequestDto requestDto, Long newRoomId) {
        return new GameRunningConfig(newRoomId,
                requestDto.getProblemLevel(),
                requestDto.getLanguage(),
                requestDto.getMaxSubmitCount(),
                requestDto.getLimitTime());
    }

    private Room validateUpdateGameRoomStatusRequest(
            Long roomId, String sessionId, GameRoomStatusUpdateMessageRequestDto requestDto) {

        Room room = roomRepository.getGameRoom(roomId);
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

        return room;
    }

    private void validateLeaveGameRoomRequest(Long roomId, User user) {
        validateRoomExistence(roomId);
        validateUserSession(user);
        validateUserInRoom(roomId, user);

        // 게임이 시작 된 방에서는 roomLeave를 할 수 없음 (gameLeave 가능)
        if (roomRepository.isStartedGame(roomId)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }
    }

    private void validateEnterGameRoomRequest(Long roomId, User user) {
        validateUserSession(user);
        validateRoomExistence(roomId);

        // 방이 꽉 찼으면
        if (roomRepository.isFullRoom(roomId)) {
            throw new CustomRoomException(RoomErrorCode.FULL_ROOM.getMessage());
        }

        // 게임 중인 유저가 방에 들어가려고 하면
        if (isUserInGame(user)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }
    }

    private void validateCreateGameRoomRequest(GameRoomCreateRequestDto requestDto, User user) {
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
        if (!roomRepository.isExistRoom(roomId)) {
            throw new CustomRoomException(RoomErrorCode.NOT_EXIST_ROOM.getMessage());
        }
    }

    // 방에 유저가 존재하지 않으면
    private void validateUserInRoom(Long roomId, User user) {
        if (!roomRepository.isExistUserInRoom(user, roomId)) {
            throw new CustomRoomException(RoomErrorCode.NOT_EXIST_USER_IN_ROOM.getMessage());
        }
    }
}
