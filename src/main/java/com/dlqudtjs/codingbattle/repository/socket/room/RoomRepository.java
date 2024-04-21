package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.model.room.WaitRoom;

public interface RoomRepository {

    WaitRoom save(WaitRoom waitRoom);

    WaitRoom joinRoom(String userId, Integer roomId);

    void leaveRoom(String userId, Integer roomId);

    Boolean isExistRoom(Integer roomId);

    Boolean isFullRoom(Integer roomId);

    Boolean isExistUserInRoom(String userId, Integer roomId);
}
