package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import com.dlqudtjs.codingbattle.common.constant.MessageType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.GameRoom;
import com.dlqudtjs.codingbattle.model.room.GameRoomUserStatus;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomEnterRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.GameRoomInfoResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.GameRoomLeaveUserStatusResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.GameRoomListResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.GameRoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.messagewrapperdto.GameRoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.messagewrapperdto.GameRoomUserStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.repository.socket.room.RoomRepository;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.room.exception.CustomRoomException;
import com.dlqudtjs.codingbattle.service.room.exception.ErrorCode;
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
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ResponseDto createGameRoom(GameRoomCreateRequestDto requestDto, String token) {
        GameRoomLeaveUserStatusResponseDto leaveUserStatusResponseDto = null;
        String userId = jwtTokenProvider.getUserName(token);

        validateCreateGameRoomRequest(requestDto, userId);

        Integer alreadyEnterRoomId = sessionService.getUserInRoomId(userId);
        if (alreadyEnterRoomId != GameSetting.DEFAULT_ROOM_ID.getValue()) {
            leaveUserStatusResponseDto = leaveRoom(alreadyEnterRoomId, userId);
        }

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
                .status(SuccessCode.CREATE_GAME_ROOM_SUCCESS.getStatus())
                .message(SuccessCode.CREATE_GAME_ROOM_SUCCESS.getMessage())
                .data(gameRoomInfoResponseDto)
                .build();
    }

    @Override
    public ResponseDto enterGameRoom(GameRoomEnterRequestDto requestDto, String token) {
        GameRoomLeaveUserStatusResponseDto leaveUserStatusResponseDto = null;
        String userId = jwtTokenProvider.getUserName(token);

        validateEnterGameRoomRequest(requestDto, userId);

        Integer alreadyEnterRoomId = sessionService.getUserInRoomId(userId);
        if (alreadyEnterRoomId != GameSetting.DEFAULT_ROOM_ID.getValue()) {
            if (alreadyEnterRoomId.equals(requestDto.getRoomId())) {
                throw new CustomRoomException(ErrorCode.SAME_USER_IN_ROOM.getMessage());
            }

            leaveUserStatusResponseDto = leaveRoom(alreadyEnterRoomId, userId);
        }

        GameRoom joinedRoom = roomRepository.joinRoom(userId, requestDto.getRoomId());
        sessionService.enterRoom(userId, requestDto.getRoomId());

        GameRoomInfoResponseDto gameRoomInfoResponseDto = CreateGameRoomResponseDto(
                joinedRoom,
                leaveUserStatusResponseDto
        );

        return ResponseDto.builder()
                .status(SuccessCode.JOIN_GAME_ROOM_SUCCESS.getStatus())
                .message(SuccessCode.JOIN_GAME_ROOM_SUCCESS.getMessage())
                .data(gameRoomInfoResponseDto)
                .build();
    }

    @Override
    public ResponseDto leaveGameRoom(Integer roomId, String token) {
        String userId = jwtTokenProvider.getUserName(token);

        validateLeaveGameRoomRequest(roomId, userId);

        GameRoomLeaveUserStatusResponseDto leaveUserStatusResponseDto = leaveRoom(roomId, userId);

        // 상태 변경
        return ResponseDto.builder()
                .status(SuccessCode.LEAVE_GAME_ROOM_SUCCESS.getStatus())
                .message(SuccessCode.LEAVE_GAME_ROOM_SUCCESS.getMessage())
                .data(leaveUserStatusResponseDto)
                .build();
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
                .status(SuccessCode.GET_GAME_ROOM_LIST_SUCCESS.getStatus())
                .message(SuccessCode.GET_GAME_ROOM_LIST_SUCCESS.getMessage())
                .data(responseDtoList)
                .build();
    }

    @Override
    public SendToRoomMessageResponseDto parseMessage(Integer roomId, String sessionId,
                                                     SendToRoomMessageRequestDto requestDto) {
        String userId = WebsocketSessionHolder.getUserIdFromSessionId(sessionId);

        // 방이 존재하지 않으면
        if (!roomRepository.isExistRoom(roomId)) {
            throw new CustomRoomException(ErrorCode.NOT_EXIST_ROOM.getMessage());
        }

        // 세션이 존재하지 않으면
        if (WebsocketSessionHolder.isNotConnected(userId)) {
            throw new CustomRoomException(ErrorCode.NOT_CONNECT_USER.getMessage());
        }

        // 방에 유저가 존재하지 않으면
        if (!roomRepository.isExistUserInRoom(userId, roomId)) {
            throw new CustomRoomException(ErrorCode.NOT_EXIST_USER_IN_ROOM.getMessage());
        }

        return SendToRoomMessageResponseDto.builder()
                .messageType(MessageType.USER.getMessageType())
                .senderId(userId)
                .message(requestDto.getMessage())
                .sendTime(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    @Override
    public GameRoomStatusUpdateMessageResponseDto updateGameRoomStatus(
            Integer roomId, String sessionId, GameRoomStatusUpdateRequestDto requestDto) {
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
            Integer roomId, String sessionId,
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

    private GameRoomLeaveUserStatusResponseDto leaveRoom(Integer roomId, String userId) {
        GameRoom gameRoom = roomRepository.getGameRoom(roomId);
        // 만약 나가는 유저가 방장이라면 방 삭제 및 방에 있는 모든 유저 leaveRoom
        if (gameRoom.isHost(userId)) {
            // 방에 모든 유저 세션 상태 변경
            leaveAllUserInRoom(roomId);
        } else {
            // 나간 유저만 세션 상태 변경
            sessionService.leaveRoom(userId);
        }
        // 방 나감 (Repository 에서 유저가 방장이라면 방 삭제함)
        roomRepository.leaveRoom(userId, roomId);

        // 방에 있는 모든 유저에게 나간 유저의 상태를 알림
        return GameRoomLeaveUserStatusResponseDto.builder()
                .userId(userId)
                .isHost(gameRoom.isHost(userId))
                .build();
    }

    // 방에 있는 모든 유저 leaveRoom 하는 메서드
    private void leaveAllUserInRoom(Integer roomId) {
        GameRoom gameRoom = roomRepository.getGameRoom(roomId);
        for (String userId : gameRoom.getUserList()) {
            sessionService.leaveRoom(userId);
        }
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
            Integer roomId, String sessionId, GameRoomUserStatusUpdateRequestDto requestDto) {
        GameRoom gameRoom = roomRepository.getGameRoom(roomId);
        String userId = WebsocketSessionHolder.getUserIdFromSessionId(sessionId);

        // 방이 존재하지 않으면
        if (!roomRepository.isExistRoom(roomId)) {
            throw new CustomRoomException(ErrorCode.NOT_EXIST_ROOM.getMessage());
        }

        // 세션 아이디와 요청한 유저 아이디가 일치하지 않으면
        if (!requestDto.getUserId().equals(userId)) {
            throw new CustomRoomException(ErrorCode.INVALID_REQUEST.getMessage());
        }

        // 밤에 설정된 language외 다른 language로 변경하려고 하면
        ProgrammingLanguage language = gameRoom.getLanguage();
        if (!language.equals(ProgrammingLanguage.DEFAULT) &&
                !language.equals(ProgrammingLanguage.valueOf(requestDto.getLanguage().toUpperCase()))) {
            throw new CustomRoomException(ErrorCode.INVALID_REQUEST.getMessage());
        }
    }

    private GameRoom validateUpdateGameRoomStatusRequest(
            Integer roomId, String sessionId, GameRoomStatusUpdateRequestDto requestDto) {

        GameRoom gameRoom = roomRepository.getGameRoom(roomId);
        String userId = WebsocketSessionHolder.getUserIdFromSessionId(sessionId);

        // 방이 존재하지 않으면
        if (gameRoom == null) {
            throw new CustomRoomException(ErrorCode.NOT_EXIST_ROOM.getMessage());
        }

        // 방장과 세션 아이디가 일치하지 않으면 (웹 소켓 세션에 존재하지 않으면)
        if (!gameRoom.isHost(userId)) {
            throw new CustomRoomException(ErrorCode.INVALID_REQUEST.getMessage());
        }

        // requestDto의 hostId와 userId가 일치하지 않으면
        if (!requestDto.getHostId().equals(userId)) {
            throw new CustomRoomException(ErrorCode.INVALID_REQUEST.getMessage());
        }

        return gameRoom;
    }

    private void validateLeaveGameRoomRequest(Integer roomId, String userId) {
        // 방이 존재하지 않으면
        if (!roomRepository.isExistRoom(roomId)) {
            throw new CustomRoomException(ErrorCode.NOT_EXIST_ROOM.getMessage());
        }

        // 요청한 유저가 웹 소켓 세션에 존재하지 않으면
        if (WebsocketSessionHolder.isNotConnected(userId)) {
            throw new CustomRoomException(ErrorCode.NOT_CONNECT_USER.getMessage());
        }

        // 방에 유저가 존재하지 않으면
        if (!roomRepository.isExistUserInRoom(userId, roomId)) {
            throw new CustomRoomException(ErrorCode.NOT_EXIST_USER_IN_ROOM.getMessage());
        }
    }

    private void validateEnterGameRoomRequest(GameRoomEnterRequestDto requestDto, String userId) {
        // 요청한 유저와 토큰이 일치하지 않으면
        if (!userId.equals(requestDto.getUserId())) {
            throw new CustomRoomException(ErrorCode.INVALID_REQUEST.getMessage());
        }

        // 요청한 유저가 웹 소켓 세션에 존재하지 않으면
        if (WebsocketSessionHolder.isNotConnected(userId)) {
            throw new CustomRoomException(ErrorCode.NOT_CONNECT_USER.getMessage());
        }

        // 방이 존재하지 않으면
        if (!roomRepository.isExistRoom(requestDto.getRoomId())) {
            throw new CustomRoomException(ErrorCode.NOT_EXIST_ROOM.getMessage());
        }

        // 방이 꽉 찼으면
        if (roomRepository.isFullRoom(requestDto.getRoomId())) {
            throw new CustomRoomException(ErrorCode.FULL_ROOM.getMessage());
        }
    }

    private void validateCreateGameRoomRequest(GameRoomCreateRequestDto requestDto, String userId) {
        // 요청한 유저와 토큰이 일치하지 않으면
        if (!userId.equals(requestDto.getHostId())) {
            throw new CustomRoomException(ErrorCode.INVALID_REQUEST.getMessage());
        }

        // 요청한 유저가 웹 소켓 세션에 존재하지 않으면
        if (WebsocketSessionHolder.isNotConnected(userId)) {
            throw new CustomRoomException(ErrorCode.NOT_CONNECT_USER.getMessage());
        }
    }
}
