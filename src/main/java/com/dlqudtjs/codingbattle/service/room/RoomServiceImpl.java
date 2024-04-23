package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.GameRoom;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomEnterRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomStatusUpdateResponseDto;
import com.dlqudtjs.codingbattle.model.room.responseDto.GameRoomInfoResponseDto;
import com.dlqudtjs.codingbattle.model.room.responseDto.GameRoomListResponseDto;
import com.dlqudtjs.codingbattle.repository.socket.room.RoomRepository;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.room.exception.CustomRoomException;
import com.dlqudtjs.codingbattle.service.room.exception.ErrorCode;
import com.dlqudtjs.codingbattle.service.session.SessionService;
import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
import com.dlqudtjs.codingbattle.websocket.configuration.exception.CustomSocketException;
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
        String userId = jwtTokenProvider.getUserName(token);

        validateCreateGameRoomRequest(requestDto, userId);

        Integer alreadyEnterRoomId = sessionService.getUserInRoomId(userId);
        if (alreadyEnterRoomId != null) {
            roomRepository.leaveRoom(userId, alreadyEnterRoomId);
        }

        // 방장 설정
        GameRoom room = requestDto.toEntity();
        room.addUser(requestDto.getHostId());

        // 방 생성
        GameRoom createdRoom = roomRepository.save(room);

        // 유저의 세션 상태 변경
        roomRepository.joinRoom(userId, createdRoom.getRoomId());
        sessionService.enterRoom(userId, createdRoom.getRoomId());

        GameRoomInfoResponseDto gameRoomInfoResponseDto = CreateGameRoomResponseDto(createdRoom);

        return ResponseDto.builder()
                .status(SuccessCode.CREATE_GAME_ROOM_SUCCESS.getStatus())
                .message(SuccessCode.CREATE_GAME_ROOM_SUCCESS.getMessage())
                .data(gameRoomInfoResponseDto)
                .build();
    }

    @Override
    public ResponseDto enterGameRoom(GameRoomEnterRequestDto requestDto, String token) {
        String userId = jwtTokenProvider.getUserName(token);

        validateEnterGameRoomRequest(requestDto, userId);

        Integer alreadyEnterRoomId = sessionService.getUserInRoomId(userId);

        if (alreadyEnterRoomId != null) {
            if (alreadyEnterRoomId.equals(requestDto.getRoomId())) {
                throw new CustomRoomException(ErrorCode.SAME_USER_IN_ROOM.getMessage());
            }

            roomRepository.leaveRoom(userId, alreadyEnterRoomId);
            sessionService.leaveRoom(userId);
        }

        GameRoom joinedRoom = roomRepository.joinRoom(userId, requestDto.getRoomId());
        sessionService.enterRoom(userId, requestDto.getRoomId());

        GameRoomInfoResponseDto gameRoomInfoResponseDto = CreateGameRoomResponseDto(joinedRoom);

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

        roomRepository.leaveRoom(userId, roomId);
        sessionService.leaveRoom(userId);

        // 상태 변경
        return ResponseDto.builder()
                .status(SuccessCode.LEAVE_GAME_ROOM_SUCCESS.getStatus())
                .message(SuccessCode.LEAVE_GAME_ROOM_SUCCESS.getMessage())
                .data(roomId)
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
    public GameRoomStatusUpdateResponseDto updateGameRoomStatus(
            Integer roomId, String sessionId, GameRoomStatusUpdateRequestDto requestDto) {
        GameRoom gameRoom = validateUpdateGameRoomStatusRequest(roomId, sessionId, requestDto);

        return null;
    }

    private GameRoomInfoResponseDto CreateGameRoomResponseDto(GameRoom room) {
        return GameRoomInfoResponseDto.builder()
                .roomStatus(room.toGameRoomStatusResponseDto())
                .userStatus(room.toGameRoomUserStatusResponseDto())
                .build();
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
            throw new CustomSocketException(ErrorCode.INVALID_REQUEST.getMessage());
        }

        // requestDto의 hostId와 userId가 일치하지 않으면
        if (!requestDto.getHostId().equals(userId)) {
            throw new CustomSocketException(ErrorCode.INVALID_REQUEST.getMessage());
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
