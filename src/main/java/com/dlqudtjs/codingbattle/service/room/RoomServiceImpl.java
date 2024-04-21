package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.WaitRoom;
import com.dlqudtjs.codingbattle.model.room.requestDto.WaitRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.WaitRoomEnterRequestDto;
import com.dlqudtjs.codingbattle.repository.socket.room.RoomRepository;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.room.exception.CustomRoomException;
import com.dlqudtjs.codingbattle.service.room.exception.ErrorCode;
import com.dlqudtjs.codingbattle.service.session.SessionService;
import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final SessionService sessionService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ResponseDto createWaitRoom(WaitRoomCreateRequestDto requestDto, String token) {
        String userId = jwtTokenProvider.getUserName(token);

        validateCreateWaitRoomRequest(requestDto, userId);

        Integer alreadyEnterRoomId = sessionService.getUserInRoomId(userId);
        if (alreadyEnterRoomId != null) {
            roomRepository.leaveRoom(userId, alreadyEnterRoomId);
        }

        // 방장 설정
        WaitRoom room = requestDto.toEntity();
        room.addUser(requestDto.getHostId());

        // 방 생성
        Integer createdRoomId = roomRepository.save(room);

        // 유저의 세션 상태 변경
        roomRepository.joinRoom(userId, createdRoomId);
        sessionService.enterRoom(userId, createdRoomId);

        return ResponseDto.builder()
                .status(SuccessCode.CREATE_WAIT_ROOM_SUCCESS.getStatus())
                .message(SuccessCode.CREATE_WAIT_ROOM_SUCCESS.getMessage())
                .data(createdRoomId)
                .build();
    }

    @Override
    public ResponseDto enterWaitRoom(WaitRoomEnterRequestDto requestDto, String token) {
        String userId = jwtTokenProvider.getUserName(token);

        validateEnterWaitRoomRequest(requestDto, userId);

        Integer alreadyEnterRoomId = sessionService.getUserInRoomId(userId);

        if (alreadyEnterRoomId != null) {
            if (alreadyEnterRoomId.equals(requestDto.getRoomId())) {
                throw new CustomRoomException(ErrorCode.SAME_USER_IN_ROOM.getMessage());
            }

            roomRepository.leaveRoom(userId, alreadyEnterRoomId);
            sessionService.leaveRoom(userId);
        }

        roomRepository.joinRoom(userId, requestDto.getRoomId());
        sessionService.enterRoom(userId, requestDto.getRoomId());

        return ResponseDto.builder()
                .status(SuccessCode.JOIN_WAIT_ROOM_SUCCESS.getStatus())
                .message(SuccessCode.JOIN_WAIT_ROOM_SUCCESS.getMessage())
                .data(requestDto.getRoomId())
                .build();
    }

    @Override
    public ResponseDto leaveWaitRoom(Integer roomId, String token) {
        String userId = jwtTokenProvider.getUserName(token);

        validateLeaveWaitRoomRequest(roomId, userId);

        roomRepository.leaveRoom(userId, roomId);
        sessionService.leaveRoom(userId);

        // 상태 변경
        return ResponseDto.builder()
                .status(SuccessCode.LEAVE_WAIT_ROOM_SUCCESS.getStatus())
                .message(SuccessCode.LEAVE_WAIT_ROOM_SUCCESS.getMessage())
                .data(roomId)
                .build();
    }

    private void validateLeaveWaitRoomRequest(Integer roomId, String userId) {
        // 방이 존재하지 않으면
        if (!roomRepository.isExistRoom(roomId)) {
            throw new CustomRoomException(ErrorCode.NOT_EXIST_ROOM.getMessage());
        }

        // 요청한 유저가 웹 소켓 세션에 존재하지 않으면
        if (!WebsocketSessionHolder.existUser(userId)) {
            throw new CustomRoomException(ErrorCode.NOT_CONNECT_USER.getMessage());
        }

        // 방에 유저가 존재하지 않으면
        if (!roomRepository.isExistUserInRoom(userId, roomId)) {
            throw new CustomRoomException(ErrorCode.NOT_EXIST_USER_IN_ROOM.getMessage());
        }
    }

    private void validateEnterWaitRoomRequest(WaitRoomEnterRequestDto requestDto, String userId) {
        // 요청한 유저와 토큰이 일치하지 않으면
        if (!userId.equals(requestDto.getUserId())) {
            throw new CustomRoomException(ErrorCode.INVALID_REQUEST.getMessage());
        }

        // 요청한 유저가 웹 소켓 세션에 존재하지 않으면
        if (!WebsocketSessionHolder.existUser(userId)) {
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

    private void validateCreateWaitRoomRequest(WaitRoomCreateRequestDto requestDto, String userId) {
        // 요청한 유저와 토큰이 일치하지 않으면
        if (!userId.equals(requestDto.getHostId())) {
            throw new CustomRoomException(ErrorCode.INVALID_REQUEST.getMessage());
        }

        // 요청한 유저가 웹 소켓 세션에 존재하지 않으면
        if (!WebsocketSessionHolder.existUser(userId)) {
            throw new CustomRoomException(ErrorCode.NOT_CONNECT_USER.getMessage());
        }
    }
}
