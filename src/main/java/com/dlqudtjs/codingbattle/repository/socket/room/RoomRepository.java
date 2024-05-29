package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import java.util.List;

public interface RoomRepository {

    Room save(Room room, Long roomId);

    Long getNewRoomId();

    Room join(UserInfo userInfo, Long roomId);

    void leave(Long roomId, User user);

    Room getGameRoom(Long roomId);

    List<Room> getGameRoomList();

    Room updateGameRoomStatus(Long roomId, Room room);

    Boolean isExistRoom(Long roomId);

    Boolean isStartedGame(Long roomId);

    Boolean isFullRoom(Long roomId);

    Boolean isExistUserInRoom(User user, Long roomId);
}
