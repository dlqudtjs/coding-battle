package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomUserStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;

public interface RoomService {

    ResponseDto createGameRoom(GameRoomCreateRequestDto requestDto, String userId);

    ResponseDto enterGameRoom(Long roomId, String userId);

    ResponseDto leaveGameRoom(Long roomId, String userId);

    void logout(String userId);

    ResponseDto getGameRoomList();

    GameRoom startGame(Long roomId);

    Boolean isExistRoom(Long roomId);

    Boolean isStartedGame(Long roomId);

    SendToRoomMessageResponseDto parseMessage(Long roomId, String sessionId,
                                              SendToRoomMessageRequestDto requestDto);

    GameRoomStatusUpdateMessageResponseDto updateGameRoomStatus(
            Long roomId, String sessionId, GameRoomStatusUpdateRequestDto requestDto);

    GameRoomUserStatusUpdateMessageResponseDto updateGameRoomUserStatus(
            Long roomId, String sessionId, GameRoomUserStatusUpdateRequestDto requestDto);
}
