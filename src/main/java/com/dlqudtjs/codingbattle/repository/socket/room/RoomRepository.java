package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.model.room.WaitRoom;

public interface RoomRepository {

    Integer save(WaitRoom waitRoom);

    void remove(Integer roomId);
}
