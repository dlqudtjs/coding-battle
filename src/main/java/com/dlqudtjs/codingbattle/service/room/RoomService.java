package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomUserStatusUpdateMessageResponseDto;

public interface RoomService {

    ResponseDto createGameRoom(GameRoomCreateRequestDto requestDto, String userId);

    ResponseDto enterGameRoom(Integer roomId, String userId);

    ResponseDto leaveGameRoom(Integer roomId, String userId);

    void logout(String userId);

    ResponseDto getGameRoomList();

    SendToRoomMessageResponseDto parseMessage(Integer roomId, String sessionId,
                                              SendToRoomMessageRequestDto requestDto);

    GameRoomStatusUpdateMessageResponseDto updateGameRoomStatus(
            Integer roomId, String sessionId, GameRoomStatusUpdateRequestDto requestDto);

    GameRoomUserStatusUpdateMessageResponseDto updateGameRoomUserStatus(
            Integer roomId, String sessionId, GameRoomUserStatusUpdateRequestDto requestDto);
}
