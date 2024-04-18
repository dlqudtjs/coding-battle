package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.model.room.WaitRoom;

public interface RoomRepository {

    Integer save(WaitRoom waitRoom);

    void joinRoom(String userId, Integer roomId);

    void leaveRoom(String userId, Integer roomId);

    Boolean isExistRoom(Integer roomId);
}
