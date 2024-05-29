package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.GameRoomStatusUpdateMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomUserStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.user.User;

public interface RoomService {

    ResponseDto createRoom(GameRoomCreateRequestDto requestDto, User user);

    ResponseDto enterRoom(Long roomId, User user);

    ResponseDto leaveGameRoom(Long roomId, User user);

    Room getGameRoom(Long roomId);

    void logout(User user);

    ResponseDto getGameRoomList();

    Room startGame(Long roomId, User user);

    Boolean isExistUserInRoom(Long roomId, User user);

    Boolean isExistRoom(Long roomId);

    Boolean isStartedGame(Long roomId);

    SendToRoomMessageResponseDto parseMessage(Long roomId, String sessionId,
                                              SendToRoomMessageRequestDto requestDto);

    GameRoomStatusUpdateMessageResponseDto updateGameRoomStatus(
            Long roomId, String sessionId, GameRoomStatusUpdateMessageRequestDto requestDto);

    GameRoomUserStatusUpdateMessageResponseDto updateGameRoomUserStatus(
            Long roomId, String sessionId, GameRoomUserStatusUpdateRequestDto requestDto);
}
