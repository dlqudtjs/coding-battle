package com.dlqudtjs.codingbattle.repository.room;

import com.dlqudtjs.codingbattle.model.room.WaitRoom;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepositoryImpl implements RoomRepository {

    ConcurrentHashMap<Integer, WaitRoom> roomMap = new ConcurrentHashMap<>();

    @Override
    public Integer save(WaitRoom waitRoom) {
        Integer roomId = availableRoomId();
        roomMap.put(roomId, waitRoom);
        return roomId;
    }

    private Integer availableRoomId() {
        int roomId = 1;

        while (roomMap.containsKey(roomId)) {
            roomId++;
        }

        return roomId;
    }
}
