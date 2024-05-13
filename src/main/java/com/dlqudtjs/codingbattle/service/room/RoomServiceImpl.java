package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import com.dlqudtjs.codingbattle.common.constant.MessageType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.constant.RoomSuccessCode;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import com.dlqudtjs.codingbattle.entity.room.GameRoomUserStatus;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomInfoResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomLeaveUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomListResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomUserStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.repository.socket.room.RoomRepository;
import com.dlqudtjs.codingbattle.common.exception.room.CustomRoomException;
import com.dlqudtjs.codingbattle.common.exception.room.RoomErrorCode;
import com.dlqudtjs.codingbattle.service.session.SessionService;
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

    @Override
    public ResponseDto createGameRoom(GameRoomCreateRequestDto requestDto, String userId) {
        validateCreateGameRoomRequest(requestDto, userId);

        // 방 생성시 기존 방 나가기 (default 방 포함)
        Long alreadyEnterRoomId = sessionService.getUserInRoomId(userId);
        GameRoomLeaveUserStatusResponseDto leaveUserStatusResponseDto =
                leaveRoom(alreadyEnterRoomId, userId);

        // 방장 설정
        GameRoom room = requestDto.toEntity();
        room.addUser(requestDto.getHostId());

        // 방 생성
        GameRoom createdRoom = roomRepository.save(room);

        // 유저의 세션 상태 변경
        roomRepository.joinRoom(userId, createdRoom.getRoomId());
        sessionService.enterRoom(userId, createdRoom.getRoomId());

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
    public ResponseDto enterGameRoom(Long roomId, String userId) {
        validateEnterGameRoomRequest(roomId, userId);

        Long alreadyEnterRoomId = sessionService.getUserInRoomId(userId);
        // 이미 입장한 방에 다시 입장할 때 예외발생
        if (alreadyEnterRoomId.equals(roomId)) {
            throw new CustomRoomException(RoomErrorCode.SAME_USER_IN_ROOM.getMessage());
        }

        // 기존 방 나가기 (default 방 포함)
        GameRoomLeaveUserStatusResponseDto leaveUserStatusResponseDto = null;
        if (!alreadyEnterRoomId.equals((long) GameSetting.NO_ROOM_ID.getValue())) {
            leaveUserStatusResponseDto = leaveRoom(alreadyEnterRoomId, userId);
        }

        GameRoom joinedRoom = roomRepository.joinRoom(userId, roomId);
        sessionService.enterRoom(userId, roomId);

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
    public ResponseDto leaveGameRoom(Long roomId, String userId) {
        validateLeaveGameRoomRequest(roomId, userId);

        GameRoomLeaveUserStatusResponseDto leaveUserStatusResponseDto = leaveRoom(roomId, userId);
        enterDefaultRoom(userId);

        // 상태 변경
        return ResponseDto.builder()
                .status(RoomSuccessCode.LEAVE_GAME_ROOM_SUCCESS.getStatus())
                .message(RoomSuccessCode.LEAVE_GAME_ROOM_SUCCESS.getMessage())
                .data(leaveUserStatusResponseDto)
                .build();
    }

    @Override
    public void logout(String userId) {
        Long roomId = sessionService.getUserInRoomId(userId);
        leaveRoom(roomId, userId);

        // default 방도 나가기
        roomRepository.leaveRoom((long) GameSetting.DEFAULT_ROOM_ID.getValue(), userId);
    }

    @Override
    public ResponseDto getGameRoomList() {
        List<GameRoom> gameRoomList = roomRepository.getGameRoomList();

        List<GameRoomListResponseDto> responseDtoList = gameRoomList.stream()
                .map(room -> GameRoomListResponseDto.builder()
                        .roomId(room.getRoomId())
                        .hostId(room.getHostId())
                        .title(room.getTitle())
                        .language(room.getLanguage().getLanguageName())
                        .isLocked(room.isLocked())
                        .isStarted(room.getIsStarted())
                        .problemLevel(room.getProblemLevel())
                        .maxUserCount(room.getMaxUserCount())
                        .maxSubmitCount(room.getMaxSubmitCount())
                        .limitTime(room.getLimitTime())
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
        String userId = WebsocketSessionHolder.getUserIdFromSessionId(sessionId);

        validateRoomExistence(roomId);
        validateUserSession(userId);
        validateUserInRoom(roomId, userId);

        return SendToRoomMessageResponseDto.builder()
                .messageType(MessageType.USER.getMessageType())
                .senderId(userId)
                .message(requestDto.getMessage())
                .sendTime(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    @Override
    public GameRoomStatusUpdateMessageResponseDto updateGameRoomStatus(
            Long roomId, String sessionId, GameRoomStatusUpdateRequestDto requestDto) {
        GameRoom gameRoom = validateUpdateGameRoomStatusRequest(roomId, sessionId, requestDto);

        GameRoom updatedGameRoom = roomRepository.updateGameRoomStatus(
                roomId,
                gameRoom.updateGameRoomStatus(requestDto)
        );

        return GameRoomStatusUpdateMessageResponseDto.builder()
                .roomStatus(updatedGameRoom.toGameRoomStatusResponseDto())
                .build();
    }

    @Override
    public GameRoomUserStatusUpdateMessageResponseDto updateGameRoomUserStatus(
            Long roomId, String sessionId,
            GameRoomUserStatusUpdateRequestDto requestDto) {
        validateUpdateGameRoomUserStatusRequest(roomId, sessionId, requestDto);

        GameRoom gameRoom = roomRepository.getGameRoom(roomId);
        GameRoomUserStatus updatedUserStatus = gameRoom.updateGameRoomUserStatus(requestDto);

        return GameRoomUserStatusUpdateMessageResponseDto.builder()
                .updateUserStatus(GameRoomUserStatusResponseDto.builder()
                        .userId(updatedUserStatus.getUserId())
                        .isReady(updatedUserStatus.getIsReady())
                        .language(updatedUserStatus.getUseLanguage().getLanguageName())
                        .build())
                .build();
    }

    /*
     * 방을 나가는 메서드
     * 방장이 아닐 경우 방을 나가고, 유저의 상태를 default 으로 변경
     * 방장일 경우 방을 삭제하고, 방에 있던 유저들의 상태를 default 방으로 변경
     */
    private GameRoomLeaveUserStatusResponseDto leaveRoom(Long roomId, String userId) {
        GameRoom gameRoom = roomRepository.getGameRoom(roomId);

        // 방을 삭제해야 된다면 삭제하기 전 방에 있는 모든 유저 leaveRoom
        if (gameRoom.isHost(userId)) {
            // 방에 모든 유저 세션 상태 변경
            leaveAllUserInRoom(roomId, userId);
        }

        // 방 나감 (Repository 에서 유저가 방장이라면 방 삭제함)
        roomRepository.leaveRoom(roomId, userId);
        // 나간 유저 세션 상태 변경
        sessionService.leaveRoom(userId);

        // 방에 있는 모든 유저에게 나간 유저의 상태를 알림
        return GameRoomLeaveUserStatusResponseDto.builder()
                .roomId(roomId)
                .userId(userId)
                .isHost(gameRoom.isHost(userId))
                .build();
    }

    // 방에 있는 모든 유저 leaveRoom 하는 메서드
    private void leaveAllUserInRoom(Long roomId, String hostId) {
        GameRoom gameRoom = roomRepository.getGameRoom(roomId);
        for (String userId : gameRoom.getUserList()) {
            if (userId.equals(hostId)) {
                continue;
            }

            sessionService.leaveRoom(userId);
            enterDefaultRoom(userId);
        }
    }

    private void enterDefaultRoom(String userId) {
        roomRepository.joinRoom(userId, (long) GameSetting.DEFAULT_ROOM_ID.getValue());
        sessionService.enterRoom(userId, (long) GameSetting.DEFAULT_ROOM_ID.getValue());
    }

    private GameRoomInfoResponseDto CreateGameRoomResponseDto(
            GameRoom room,
            GameRoomLeaveUserStatusResponseDto leaveUserStatusResponseDto
    ) {
        return GameRoomInfoResponseDto.builder()
                .roomStatus(room.toGameRoomStatusResponseDto())
                .leaveUserStatus(leaveUserStatusResponseDto)
                .userStatus(room.toGameRoomUserStatusResponseDto())
                .build();
    }

    private void validateUpdateGameRoomUserStatusRequest(
            Long roomId, String sessionId, GameRoomUserStatusUpdateRequestDto requestDto) {
        GameRoom gameRoom = roomRepository.getGameRoom(roomId);
        String userId = WebsocketSessionHolder.getUserIdFromSessionId(sessionId);

        validateRoomExistence(roomId);

        // 세션 아이디와 요청한 유저 아이디가 일치하지 않으면
        if (!requestDto.getUserId().equals(userId)) {
            throw new CustomRoomException(RoomErrorCode.INVALID_REQUEST.getMessage());
        }

        // 밤에 설정된 language외 다른 language로 변경하려고 하면
        ProgrammingLanguage language = gameRoom.getLanguage();
        if (!language.equals(ProgrammingLanguage.DEFAULT) &&
                !language.equals(ProgrammingLanguage.valueOf(requestDto.getLanguage().toUpperCase()))) {
            throw new CustomRoomException(RoomErrorCode.INVALID_REQUEST.getMessage());
        }
    }

    private GameRoom validateUpdateGameRoomStatusRequest(
            Long roomId, String sessionId, GameRoomStatusUpdateRequestDto requestDto) {

        GameRoom gameRoom = roomRepository.getGameRoom(roomId);
        String userId = WebsocketSessionHolder.getUserIdFromSessionId(sessionId);

        validateRoomExistence(roomId);

        // 방장과 세션 아이디가 일치하지 않으면 (웹 소켓 세션에 존재하지 않으면)
        if (!gameRoom.isHost(userId)) {
            throw new CustomRoomException(RoomErrorCode.INVALID_REQUEST.getMessage());
        }

        // requestDto의 hostId와 userId가 일치하지 않으면
        if (!requestDto.getHostId().equals(userId)) {
            throw new CustomRoomException(RoomErrorCode.INVALID_REQUEST.getMessage());
        }

        return gameRoom;
    }

    private void validateLeaveGameRoomRequest(Long roomId, String userId) {
        validateRoomExistence(roomId);
        validateUserSession(userId);
        validateUserInRoom(roomId, userId);
    }

    private void validateEnterGameRoomRequest(Long roomId, String userId) {
        validateUserSession(userId);
        validateRoomExistence(roomId);

        // 방이 꽉 찼으면
        if (roomRepository.isFullRoom(roomId)) {
            throw new CustomRoomException(RoomErrorCode.FULL_ROOM.getMessage());
        }
    }

    private void validateCreateGameRoomRequest(GameRoomCreateRequestDto requestDto, String userId) {
        // userId와 requestDto의 hostId가 일치하지 않으면
        if (!userId.equals(requestDto.getHostId())) {
            throw new CustomRoomException(RoomErrorCode.INVALID_REQUEST.getMessage());
        }

        // 요청한 유저가 웹 소켓 세션에 존재하지 않으면
        validateUserSession(userId);
    }

    // 유저 세션이 존재하지 않으면
    private void validateUserSession(String userId) {
        if (WebsocketSessionHolder.isNotConnected(userId)) {
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
    private void validateUserInRoom(Long roomId, String userId) {
        if (!roomRepository.isExistUserInRoom(userId, roomId)) {
            throw new CustomRoomException(RoomErrorCode.NOT_EXIST_USER_IN_ROOM.getMessage());
        }
    }
}
