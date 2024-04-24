package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomEnterRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.GameRoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.GameRoomUpdateUserStatusMessageResponseDto;

public interface RoomService {

    ResponseDto createGameRoom(GameRoomCreateRequestDto requestDto, String token);

    ResponseDto enterGameRoom(GameRoomEnterRequestDto requestDto, String token);

    ResponseDto leaveGameRoom(Integer roomId, String token);

    ResponseDto getGameRoomList();

    void validateSendMessage(Integer roomId, String sessionId, String message);

    GameRoomStatusUpdateMessageResponseDto updateGameRoomStatus(
            Integer roomId, String sessionId, GameRoomStatusUpdateRequestDto requestDto);

    GameRoomUpdateUserStatusMessageResponseDto updateGameRoomUserStatus(
            Integer roomId, String sessionId, GameRoomUserStatusUpdateRequestDto requestDto);
}
