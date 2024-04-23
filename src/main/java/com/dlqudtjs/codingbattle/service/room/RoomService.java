package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomEnterRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.responseDto.GameRoomStatusUpdateResponseDto;
import com.dlqudtjs.codingbattle.model.room.responseDto.GameRoomUserStatusUpdateResponseDto;

public interface RoomService {

    ResponseDto createGameRoom(GameRoomCreateRequestDto requestDto, String token);

    ResponseDto enterGameRoom(GameRoomEnterRequestDto requestDto, String token);

    ResponseDto leaveGameRoom(Integer roomId, String token);

    ResponseDto getGameRoomList();

    void validateSendMessage(Integer roomId, String sessionId, String message);

    GameRoomStatusUpdateResponseDto updateGameRoomStatus(
            Integer roomId, String sessionId, GameRoomStatusUpdateRequestDto requestDto);

    GameRoomUserStatusUpdateResponseDto updateGameRoomUserStatus(
            Integer roomId, String sessionId, GameRoomUserStatusUpdateRequestDto requestDto);
}
