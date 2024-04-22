package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomEnterRequestDto;

public interface RoomService {

    ResponseDto createGameRoom(GameRoomCreateRequestDto requestDto, String token);

    ResponseDto enterGameRoom(GameRoomEnterRequestDto requestDto, String token);

    ResponseDto leaveWaitRoom(Integer roomId, String token);
}
