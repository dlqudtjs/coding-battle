package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomCreateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomEnterRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.RoomStatusUpdateMessageRequestDto;
import com.dlqudtjs.codingbattle.entity.room.LeaveRoomUserStatus;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.room.RoomUserStatus;
import com.dlqudtjs.codingbattle.entity.user.User;
import java.util.List;

public interface RoomService {

    Room create(RoomCreateRequestDto requestDto, User user);

    Room enter(RoomEnterRequestDto requestDto);

    LeaveRoomUserStatus leave(Long roomId, User user);

    Room getRoom(Long roomId);

    List<Room> getRoomList();

    Room gameEnd(Long roomId);

    Boolean isExistUserInRoom(User user, Long roomId);

    Boolean isExistRoom(Long roomId);

    Room updateRoomStatus(Long roomId, User user, RoomStatusUpdateMessageRequestDto requestDto);

    RoomUserStatus updateRoomUserStatus(Long roomId, String sessionId, RoomUserStatusUpdateRequestDto requestDto);
}
