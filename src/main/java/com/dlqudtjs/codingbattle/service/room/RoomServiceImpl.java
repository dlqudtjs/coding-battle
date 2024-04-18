package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.WaitRoom;
import com.dlqudtjs.codingbattle.model.room.WaitRoomCreateRequestDto;
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

        // 방장 설정
        WaitRoom room = requestDto.toEntity();
        room.addUser(requestDto.getHostId());

        // 방 생성
        Integer createdRoomId = roomRepository.save(room);

        // 유저의 세션 상태 변경
        sessionService.enterRoom(userId, createdRoomId);

        return ResponseDto.builder()
                .status(SuccessCode.CREATE_WAIT_ROOM_SUCCESS.getStatus())
                .message(SuccessCode.CREATE_WAIT_ROOM_SUCCESS.getMessage())
                .data(createdRoomId)
                .build();
    }

    @Override
    public ResponseDto enterWaitRoom(WaitRoomCreateRequestDto requestDto) {
        return null;
    }

    private void validateCreateWaitRoomRequest(WaitRoomCreateRequestDto requestDto, String userId) {
        // 요청한 유저가 방장이 아니면 예외 발생
        if (!userId.equals(requestDto.getHostId())) {
            throw new CustomRoomException(ErrorCode.INVALID_REQUEST.getMessage());
        }

        // 요청한 유저가 웹 소켓 세션에 존재하지 않으면
        if (!WebsocketSessionHolder.existUser(userId)) {
            throw new CustomRoomException(ErrorCode.NOT_CONNECT_USER.getMessage());
        }
    }
}
