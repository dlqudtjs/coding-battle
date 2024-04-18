package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.WaitRoom;
import com.dlqudtjs.codingbattle.model.room.WaitRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.repository.room.RoomRepository;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.room.exception.CustomRoomException;
import com.dlqudtjs.codingbattle.service.room.exception.ErrorCode;
import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitRoomServiceImpl implements WaitRoomService {

    private final RoomRepository roomRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ResponseDto createWaitRoom(WaitRoomCreateRequestDto requestDto, String token) {
        String userId = jwtTokenProvider.getUserName(token);

        // 요청한 유저가 방장이 아니면 예외 발생
        if (!userId.equals(requestDto.getHostId())) {
            throw new CustomRoomException(ErrorCode.INVALID_REQUEST.getMessage());
        }

        // 요청한 유저가 웹 소켓 세션에 존재하지 않으면
        if (!WebsocketSessionHolder.existUser(requestDto.getHostId())) {
            throw new CustomRoomException(ErrorCode.NOT_CONNECT_USER.getMessage());
        }

        // 이미 다른 방에 있다면 해당 방 나가고 새로운 방 생성
        /*
        1. user, 방에 들어있는지 상태
         */

        WaitRoom room = requestDto.toEntity();
        room.addUser(requestDto.getHostId());
        Integer createdRoomId = roomRepository.save(requestDto.toEntity());

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
}
