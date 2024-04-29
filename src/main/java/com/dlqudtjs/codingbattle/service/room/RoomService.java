package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomEnterRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.messagewrapperdto.GameRoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.messagewrapperdto.GameRoomUserStatusUpdateMessageResponseDto;

public interface RoomService {

    ResponseDto createGameRoom(GameRoomCreateRequestDto requestDto, String token);

    ResponseDto enterGameRoom(GameRoomEnterRequestDto requestDto, String token);

    ResponseDto leaveGameRoom(Integer roomId, String token);

    ResponseDto getGameRoomList();

    void sendToRoomMessage(Integer roomId, Object requestDto);

    SendToRoomMessageResponseDto parseMessage(Integer roomId, String sessionId,
                                              SendToRoomMessageRequestDto requestDto);

    GameRoomStatusUpdateMessageResponseDto updateGameRoomStatus(
            Integer roomId, String sessionId, GameRoomStatusUpdateRequestDto requestDto);

    GameRoomUserStatusUpdateMessageResponseDto updateGameRoomUserStatus(
            Integer roomId, String sessionId, GameRoomUserStatusUpdateRequestDto requestDto);
}
