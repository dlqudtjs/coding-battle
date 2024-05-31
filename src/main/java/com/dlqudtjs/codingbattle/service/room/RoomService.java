package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomCreateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomEnterRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.RoomStatusUpdateMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.room.LeaveUserStatus;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.room.RoomUserStatus;
import com.dlqudtjs.codingbattle.entity.user.User;
import java.util.List;

public interface RoomService {

    Room create(RoomCreateRequestDto requestDto, User user);

    Room enter(RoomEnterRequestDto requestDto);

    LeaveUserStatus leave(Long roomId, User user);

    Room getRoom(Long roomId);

    void logout(User user);

    List<Room> getRoomList();

    Room start(Long roomId, User user);

    Boolean isExistUserInRoom(User user, Long roomId);

    Boolean isExistRoom(Long roomId);

    Boolean isStartedGame(Long roomId);

    SendToRoomMessageResponseDto parseMessage(Long roomId, String sessionId,
                                              SendToRoomMessageRequestDto requestDto);

    Room updateRoomStatus(Long roomId, String sessionId, RoomStatusUpdateMessageRequestDto requestDto);

    RoomUserStatus updateRoomUserStatus(Long roomId, String sessionId, RoomUserStatusUpdateRequestDto requestDto);
}
