package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.model.room.WaitRoom;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepositoryImpl implements RoomRepository {

    private final ConcurrentHashMap<Integer, WaitRoom> roomMap = new ConcurrentHashMap<>();

    @Override
    public Integer save(WaitRoom waitRoom) {
        Integer roomId = availableRoomId();
        roomMap.put(roomId, waitRoom);
        return roomId;
    }

    @Override
    public void leaveRoom(String userId, Integer roomId) {
        // todo : 방장이 나가면 방 삭제
        WaitRoom room = roomMap.get(roomId);
        room.removeUser(userId);
    }

    @Override
    public Boolean isExistRoom(Integer roomId) {
        return roomMap.containsKey(roomId);
    }

    @Override
    public void remove(Integer roomId) {
        roomMap.remove(roomId);
    }

    private Integer availableRoomId() {
        int roomId = 1;

        while (roomMap.containsKey(roomId)) {
            roomId++;
        }

        return roomId;
    }
}
